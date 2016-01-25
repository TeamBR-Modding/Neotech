package com.dyonovan.neotech.common.tiles.storage

import com.teambr.bookshelf.api.waila.Waila
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.common.tiles.traits.{UpdatingTile, Inventory}
import com.teambr.bookshelf.traits.NBTSavable
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

/**
  * Created by Dyonovan on 1/23/2016.
  */
class TileDimStorage extends UpdatingTile with Inventory with Waila {

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

    def increaseQty(stack: ItemStack): Int = {
        if (getStackInSlot(0) == null) setInventorySlotContents(0, new ItemStack(stack.getItem, 1, stack.getItemDamage))
        if (!getStackInSlot(0).isItemEqual(stack)) return 0

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
    }

    def isStackEqual(stack: ItemStack): Boolean = {
        if (getStackInSlot(0) != null && stack != null)
            getStackInSlot(0).isItemEqual(stack)
        else false
    }

    def checkQty(): Unit = {
        if (qty == 0) setInventorySlotContents(0, null)
    }

    def getQty() : Int = qty

    override def returnWailaHead(tipList: java.util.List[String]): java.util.List[String] = {
        if (getStackInSlot(0) == null)
            tipList.add(GuiColor.WHITE + "Empty")
        else {
            tipList.add(GuiColor.ORANGE + getStackInSlot(0).getDisplayName)
            tipList.add(GuiColor.YELLOW + "Qty: " + GuiColor.WHITE + qty )
        }
        tipList
    }

}
