package com.dyonovan.neotech.common.tiles.misc

import com.teambr.bookshelf.common.tiles.traits.InventorySided
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

/**
  * Created by Dyonovan on 2/14/2016.
  */
class TileAttractor extends TileEntity with InventorySided {

    override def initialSize: Int = 4

    override def getSlotsForFace(side: EnumFacing): Array[Int] = Array(0, 1, 2, 3)

    override def getCapabilityFromTile[T](capability: Capability[T], facing: EnumFacing): T = {
        super[TileEntity].getCapability[T](capability, facing)
    }

    override def getCapability[T](capability: Capability[T], facing: EnumFacing) : T =
        super[InventorySided].getCapability[T](capability, facing)

    override def canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean = true

    override def canInsertItem(slot: Int, itemStackIn: ItemStack, direction: EnumFacing): Boolean = false

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].writeToNBT(tag)
        super[InventorySided].writeToNBT(tag)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].readFromNBT(tag)
        super[InventorySided].readFromNBT(tag)
    }
}
