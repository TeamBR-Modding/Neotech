package com.dyonovan.neotech.common.tiles.storage

import com.teambr.bookshelf.common.tiles.traits.Inventory
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

/**
  * Created by Dyonovan on 1/22/2016.
  */
class TileFlushableChest extends TileEntity with Inventory {

    override var inventoryName: String = _

    override def hasCustomName(): Boolean = false

    override def initialSize: Int = 27

    override def markDirty(): Unit = {
        super[Inventory].markDirty()
        super[TileEntity].markDirty()
    }

    override def writeToNBT(tag: NBTTagCompound): Unit = super[Inventory].writeToNBT(tag)

    override def readFromNBT(tag: NBTTagCompound): Unit = super[Inventory].readFromNBT(tag)
}
