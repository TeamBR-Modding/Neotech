package com.teambrmodding.neotech.common.container.misc

import com.teambrmodding.neotech.common.tiles.misc.TileCrafter
import com.teambr.bookshelf.common.container.BaseContainer
import net.minecraft.entity.player.{EntityPlayer, InventoryPlayer}
import net.minecraft.inventory._
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager

import scala.collection.mutable.ArrayBuffer

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 18, 2015
 */
class ContainerCrafter(playerInventory: InventoryPlayer, tile: TileCrafter) extends BaseContainer(playerInventory, tile) {

    var craftResult1 = new InventoryCraftResult
    var craftResult2 = new InventoryCraftResult

    var crafter = tile
    var craftingGrid1: InventoryCrafting = new DummyCraftingInventory(tile, this)
    var craftingGrid2: InventoryCrafting = new DummyCraftingInventory(tile, this, 10)

    addSlotToContainer(new SlotCrafting(playerInventory.player, craftingGrid1, craftResult1, 0, 80, 31))
    addSlotToContainer(new SlotCrafting(playerInventory.player, craftingGrid2, craftResult2, 1, 80, 59))

    addCraftingGrid(craftingGrid1, 0, 8, 27, 3, 3)
    addCraftingGrid(craftingGrid2, 0, 116, 27, 3, 3)
    addPlayerInventorySlots(8, 84)

    onCraftMatrixChanged(craftingGrid1)
    onCraftMatrixChanged(craftingGrid2)

    override def onCraftMatrixChanged(inv: IInventory): Unit = {
        if(inv == craftingGrid1)
            craftResult1.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftingGrid1, crafter.getWorld))
        if(inv == craftingGrid2)
            craftResult2.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftingGrid2, crafter.getWorld))
    }

    override def canMergeSlot(stack: ItemStack, slot: Slot): Boolean = !slot.isInstanceOf[SlotCrafting]

    def addCraftingGrid(inventory: IInventory, startSlot: Int, x: Int, y: Int, width: Int, height: Int): Unit = {
        var i: Int = 0
        for (h <- 0 until height) {
            for(w <- 0 until width) {
                addSlotToContainer(new Slot(inventory, startSlot + i, x + (w * 18), y + (h * 18)))
                i += 1
            }
        }
    }

    override def canInteractWith(player: EntityPlayer): Boolean = true

    override def transferStackInSlot(player: EntityPlayer, slotId: Int): ItemStack = {
        println(slotId)
        val slot: Slot = inventorySlots.get(slotId).asInstanceOf[Slot]
        if (slot != null && slot.getHasStack) {
            val itemToTransfer: ItemStack = slot.getStack
            val copy: ItemStack = itemToTransfer.copy
            if (slotId < inventorySize) {
                if (!mergeItemStackSafe(itemToTransfer, inventorySize, inventorySlots.size, reverse = true)) return null
            }
            else if (!mergeItemStackSafe(itemToTransfer, 0, inventorySize, reverse = false)) return null
            if (itemToTransfer.stackSize == 0) slot.putStack(null)
            else slot.onSlotChanged()
            if (itemToTransfer.stackSize != copy.stackSize) {
                slot.onPickupFromSlot(player, copy)
                return copy
            }
        }
        null
    }

    class DummyCraftingInventory(tile: TileCrafter, container: Container, offset: Int)
            extends InventoryCrafting(null, 3, 3) {

        def this(tile: TileCrafter, container: Container) = this(tile, container, 0)

        val stacks = new ArrayBuffer[ItemStack]

        private def onCraftingChanged(): Unit = container.onCraftMatrixChanged(this)

        override def getSizeInventory: Int = 9

        override def getStackInSlot(slot: Int): ItemStack = {
            if (slot >= getSizeInventory) null else tile.getStackInSlot(slot + offset)
        }

        override def getStackInRowAndColumn(row: Int, column: Int): ItemStack = {
            if (row >= 0 && row < 3) {
                val k: Int  = row + column * 3
                getStackInSlot(k)
            } else
                null
        }

        //override def getStackInSlotOnClosing(slot: Int): ItemStack = tile.getStackInSlotOnClosing(slot + offset)

        override def decrStackSize(slot: Int, size: Int): ItemStack = {
            val stack:ItemStack = tile.decrStackSize(slot + offset, size)
            onCraftingChanged()
            stack
        }

        override def setInventorySlotContents(slot: Int, stack: ItemStack): Unit = {
            tile.setInventorySlotContents(slot + offset, stack)
            onCraftingChanged()
        }
    }
}
