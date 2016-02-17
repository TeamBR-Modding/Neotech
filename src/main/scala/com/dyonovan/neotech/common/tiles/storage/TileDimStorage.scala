package com.dyonovan.neotech.common.tiles.storage

import com.teambr.bookshelf.api.waila.Waila
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.common.tiles.traits.{Inventory, UpdatingTile}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

/**
  * Created by Dyonovan on 1/23/2016.
  */
class TileDimStorage extends UpdatingTile with Inventory with Waila {

    var qty = 0
    var lock = false
    final val maxStacks = 64

    override def initialSize: Int = 1

    override def markDirty(): Unit = {
        super[TileEntity].markDirty()
    }

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].writeToNBT(tag)
        super[Inventory].writeToNBT(tag)
        tag.setInteger("Qty", qty)
        tag.setBoolean("Lock", lock)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].readFromNBT(tag)
        super[Inventory].readFromNBT(tag)
        qty = tag.getInteger("Qty")
        val lastLock = lock
        if (tag.hasKey("Lock")) lock = tag.getBoolean("Lock")
        if(worldObj != null && worldObj.isRemote && (lastLock != lock))
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

    override def isItemValidForSlot(index: Int, stack: ItemStack): Boolean = index == 0 && isStackEqual(stack)

    override def insertItem(slot: Int, originalStack: ItemStack, simulate: Boolean): ItemStack = {
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
            null
        } else {
            if (qty == inventoryContents.get(0).getMaxStackSize * maxStacks) return originalStack
            val returnStack = originalStack.copy()
            returnStack.stackSize = originalStack.stackSize - Math.min(originalStack.stackSize, (inventoryContents.get(0).getMaxStackSize * maxStacks) - qty)
            if (!simulate) {
                qty += originalStack.stackSize - returnStack.stackSize
                worldObj.markBlockForUpdate(pos)
            }
            if (returnStack.stackSize > 0) returnStack else null
        }
    }

    override def extractItem(extractSlot: Int, amount: Int, simulate: Boolean): ItemStack = {
        if (amount == 0 || inventoryContents.get(0) == null || qty == 0) return null

        val actual = Math.min(Math.min(amount, qty), inventoryContents.get(0).getMaxStackSize)
        val returnStack = inventoryContents.get(0).copy()
        returnStack.stackSize = actual
        if (!simulate) {
            qty -= actual
            checkQty()
            worldObj.markBlockForUpdate(pos)
        }
        returnStack
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
        tipList
    }
}