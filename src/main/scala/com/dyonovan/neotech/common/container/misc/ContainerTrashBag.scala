package com.dyonovan.neotech.common.container.misc

import com.dyonovan.neotech.common.container.{InventoryNull, SlotNull}
import com.teambr.bookshelf.common.container.BaseContainer
import com.teambr.bookshelf.common.container.slots.{PhantomSlot, SLOT_SIZE}
import net.minecraft.entity.item.EntityPainting
import net.minecraft.entity.player.{EntityPlayer, InventoryPlayer}
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

import scala.util.control.Breaks._
/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/26/2016
  */
class ContainerTrashBag(inventory : IInventory, playerInventory : InventoryPlayer, bag : ItemStack)
        extends BaseContainer(playerInventory, inventory) {
    addPlayerInventorySlots(85)
    val replacer = new SlotNull(SLOT_SIZE.STANDARD, new InventoryNull, 0, -1000, 0)
    addSlotToContainer(replacer)

    breakable {
        for (x <- 0 until this.inventorySlots.size()) {
            val slot = inventorySlots.get(x)
            if (slot != null) {
                if (slot.getStack != null && slot.getStack.equals(bag)) {
                    val x = slot.xDisplayPosition
                    val y = slot.yDisplayPosition
                    slot.xDisplayPosition = -1000
                    replacer.xDisplayPosition = x
                    replacer.yDisplayPosition = y
                    break
                }
            }
        }
    }

    addSlotToContainer(new PhantomSlot(inventory, 0, 80, 35))

    override def onContainerClosed(player : EntityPlayer) : Unit = {
        if(!bag.hasTagCompound)
            bag.setTagCompound(new NBTTagCompound)
        if(inventory.getStackInSlot(0) != null)
            inventory.getStackInSlot(0).writeToNBT(bag.getTagCompound)
        else
            bag.setTagCompound(new NBTTagCompound)
        player.inventory.setInventorySlotContents(player.inventory.currentItem, bag)
    }
}
