package com.dyonovan.neotech.common.tiles.storage

import com.teambr.bookshelf.api.waila.Waila
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.common.tiles.traits.{Inventory, UpdatingTile}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

/**
  * Created by Dyonovan on 1/23/2016.
  */
class TileDimStorage extends UpdatingTile with Inventory with Waila  {

    private var qty = 0
    final val maxStacks = 64

    override var inventoryName: String = _

    override def hasCustomName(): Boolean = false

    override def initialSize: Int = 1

    override def markDirty(): Unit = {
        super[Inventory].markDirty()
        super[TileEntity].markDirty()
    }

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].writeToNBT(tag)
        super[Inventory].writeToNBT(tag)
        tag.setInteger("Qty", qty)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].readFromNBT(tag)
        super[Inventory].readFromNBT(tag)
        qty = tag.getInteger("Qty")
    }

    /*def increaseQty(stack: ItemStack): Int = {
        if (getStackInSlot(0) == null) {
            //val inStack = stack.copy()
            //inStack.stackSize = 1
            setInventorySlotContents(0, stack)
        }
        if (!isStackEqual(stack)) return 0

        val amount = stack.stackSize
        if (qty + amount <= maxStacks * getStackInSlot(0).getMaxStackSize) {
            qty += amount
            worldObj.markBlockForUpdate(pos)
            amount
        } else {
            val leftover = (qty + amount) - (maxStacks * getStackInSlot(0).getMaxStackSize)
            qty += amount - leftover
            worldObj.markBlockForUpdate(pos)
            amount - leftover
        }
    }

    def decreaseQty(fullStack: Boolean): Int = {
        if (fullStack && qty >= 64) {
            qty -= 64
            64
        } else if (fullStack && qty < 64 && qty > 0) {
            val removed = qty
            qty = 0
            worldObj.markBlockForUpdate(pos)
            removed
        } else if (!fullStack && qty > 0){
            qty -= 1
            worldObj.markBlockForUpdate(pos)
            1
        } else 0
    }*/

    def isStackEqual(stack: ItemStack): Boolean = {
        if (getStackInSlot(0) != null && stack != null)
            getStackInSlot(0).isItemEqual(stack) && ItemStack.areItemStackTagsEqual(getStackInSlot(0), stack)
        else false
    }

    /*def clearQty(): Int = {
        val ret = qty
        qty = 0
        ret
    }

    def checkQty(): Unit = {
        if (qty == 0) setInventorySlotContents(0, null)
    }*/

    def getQty : Int = qty

    def addQty(amount: Int): Unit = {
        qty += amount
        if (qty == 0) setInventorySlotContents(0, null)
    }

    override def returnWailaHead(tipList: java.util.List[String]): java.util.List[String] = {
        if (getStackInSlot(0) == null)
            tipList.add(GuiColor.WHITE + "Empty")
        else {
            tipList.add(GuiColor.ORANGE + getStackInSlot(0).getDisplayName + ": " + GuiColor.WHITE + qty)
        }
        tipList
    }

    override def isItemValidForSlot(index: Int, stack: ItemStack): Boolean = index == 0 && isStackEqual(stack)

    override def getStackInSlot(index: Int): ItemStack = {
        if(index == 0 && qty > 0 && inventoryContents.get(0) != null) {
            //inventoryContents.get(index)
            val returnStack: ItemStack = inventoryContents.get(0).copy()
            if (qty > inventoryContents.get(0).getMaxStackSize)
                returnStack.stackSize = returnStack.getMaxStackSize
            else
                returnStack.stackSize = qty
            returnStack
        } else
            null
    }

    override def setInventorySlotContents(index: Int, stack: ItemStack): Unit = {
        if (stack != null) {
            val setStack = stack.copy()
            setStack.stackSize = 1
            this.inventoryContents.set(index, setStack)
            qty = stack.stackSize
            onInventoryChanged(index)
        } else if (stack == null) {
            this.inventoryContents.set(index, stack)
            qty = 0
        }
    }
/*
    override def decrStackSize(slot : Int, amount : Int) : ItemStack = {
        if (qty > 0) {
            val stack: ItemStack = inventoryContents.get(slot).copy()

            if (qty <= amount) {
                stack.stackSize = qty
                qty = 0
                inventoryContents.set(slot, null)
            }

            qty -= amount
            stack.stackSize = amount
            onInventoryChanged(slot)
            return stack
        }
        null
    }*/
}
