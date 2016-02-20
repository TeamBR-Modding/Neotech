package com.dyonovan.neotech.common.tiles.storage

import java.util

import com.dyonovan.neotech.common.blocks.traits.Upgradeable
import com.dyonovan.neotech.managers.ItemManager
import com.teambr.bookshelf.api.waila.Waila
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.common.tiles.traits.{Inventory, UpdatingTile}
import com.teambr.bookshelf.util.WorldUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

import scala.util.control.Breaks._

/**
  * Created by Dyonovan on 1/23/2016.
  */
class TileDimStorage extends UpdatingTile with Inventory with Waila with Upgradeable {

    final val BASE_STACKS = 64

    //Syncable
    final val MAX_STACK_SIZE = 0

    var qty = 0
    var lock = false
    var maxStacks = BASE_STACKS

    override def initialSize: Int = 1

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].writeToNBT(tag)
        super[Inventory].writeToNBT(tag)
        super[Upgradeable].writeToNBT(tag)
        tag.setInteger("Qty", qty)
        tag.setBoolean("Lock", lock)
        tag.setInteger("MaxStacks", maxStacks)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].readFromNBT(tag)
        super[Inventory].readFromNBT(tag)
        super[Upgradeable].readFromNBT(tag)
        qty = tag.getInteger("Qty")
        if (tag.hasKey("MaxStacks")) maxStacks = tag.getInteger("MaxStacks")
        val lastLock = lock
        if (tag.hasKey("Lock")) lock = tag.getBoolean("Lock")
        if (worldObj != null && worldObj.isRemote && (lastLock != lock))
            worldObj.markBlockRangeForRenderUpdate(pos, pos)
    }

    def isStackEqual(stack: ItemStack): Boolean = {
        if (getStackInSlot(0) == null) true
        else if (stack != null)
            getStackInSlot(0).isItemEqual(stack) && ItemStack.areItemStackTagsEqual(getStackInSlot(0), stack)
        else false
    }

    def getQty: Int = qty

    def addQty(amount: Int): Unit = {
        qty += amount
        checkQty()
    }

    def checkQty(): Unit = {
        if (qty == 0 && !isLocked)
            inventoryContents.set(0, null)
    }

    def isLocked: Boolean = {
        lock
    }

    def setLock(l: Boolean): Unit = {
        if (l && qty == 0) return
        lock = l
        checkQty()
    }

    /*
     * Inventory Methods
     */

    override def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean = {
        slot == 0 && isStackEqual(stack) && !stack.isItemEqual(new ItemStack(ItemManager.upgradeMBFull))
    }

    override def insertItem(slot: Int, originalStack: ItemStack, simulate: Boolean): ItemStack = {
        if (slot == 0) {
            if (originalStack == null) return null
            if (!isItemValidForSlot(slot, originalStack)) return originalStack

            if (inventoryContents.get(0) == null) {
                if (!simulate) {
                    val newStack = originalStack.copy()
                    newStack.stackSize = 1
                    inventoryContents.set(0, newStack)
                    qty = originalStack.stackSize
                    worldObj.markBlockForUpdate(pos)
                }
                return null
            } else {
                if (qty == inventoryContents.get(0).getMaxStackSize * maxStacks) return originalStack
                val returnStack = originalStack.copy()
                returnStack.stackSize = originalStack.stackSize - Math.min(originalStack.stackSize, (inventoryContents.get(0).getMaxStackSize * maxStacks) - qty)
                if (!simulate) {
                    qty += originalStack.stackSize - returnStack.stackSize
                    worldObj.markBlockForUpdate(pos)
                }
                if (returnStack.stackSize > 0) return returnStack else return null
            }
        }
        originalStack
    }

    override def extractItem(extractSlot: Int, amount: Int, simulate: Boolean): ItemStack = {
        if (extractSlot == 0) {
            if (amount == 0 || inventoryContents.get(0) == null || qty == 0) return null

            val actual = Math.min(Math.min(amount, qty), inventoryContents.get(0).getMaxStackSize)
            val returnStack = inventoryContents.get(0).copy()
            returnStack.stackSize = actual
            if (!simulate) {
                qty -= actual
                checkQty()
                worldObj.markBlockForUpdate(pos)
            }
            return returnStack
        }
        null
    }

    /*
     * Waila Methods
     */
    override def returnWailaHead(tipList: java.util.List[String]): java.util.List[String] = {
        if (getStackInSlot(0) == null)
            tipList.add(GuiColor.WHITE + "Empty")
        else {
            tipList.add(GuiColor.ORANGE + getStackInSlot(0).getDisplayName + ": " + GuiColor.WHITE + qty)
            if (isLocked) tipList.add(GuiColor.RED + "Locked")
        }
        tipList.add("Max Stacks: " + maxStacks)
        tipList
    }

    def dropStacks(dropQty: Int, stack: ItemStack): Unit = {
        var actQty = dropQty
        val stacks = new util.ArrayList[ItemStack]()
        //Setup List of ItemStacks
        breakable {
            while (true) {
                if (actQty == 0) break
                else if (actQty > stack.getMaxStackSize) {
                    val addStack = stack.copy()
                    addStack.stackSize = stack.getMaxStackSize
                    stacks.add(addStack)
                    actQty -= stack.getMaxStackSize
                } else {
                    val addStack = stack.copy()
                    addStack.stackSize = actQty
                    stacks.add(addStack)
                    break
                }
            }
        }
        //Drop ItemStack
        WorldUtils.dropStack(worldObj, stacks, pos)
    }

    override def upgradeInventoryChanged(slot: Int) = {
        maxStacks = BASE_STACKS * (hardDriveCount + 1)
        if (getStackInSlot(0) != null && getQty > maxStacks * getStackInSlot(0).getMaxStackSize) {
            val leftOver = qty - (maxStacks * getStackInSlot(0).getMaxStackSize)
            dropStacks(leftOver, getStackInSlot(0).copy())
            qty = maxStacks * getStackInSlot(0).getMaxStackSize
        }
        worldObj.markBlockForUpdate(pos)
    }
}