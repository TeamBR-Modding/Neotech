package com.dyonovan.neotech.common.tiles.machines

import com.teambr.bookshelf.common.tiles.traits.{Inventory, UpdatingTile}
import net.minecraft.nbt.NBTTagCompound

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/11/2016
  */
class TileGrinder extends UpdatingTile with Inventory {
    override var inventoryName: String = "Grinder"
    override def hasCustomName(): Boolean = false
    override def initialSize: Int = 7

    override def writeToNBT(tag : NBTTagCompound) : Unit = {
        super[UpdatingTile].writeToNBT(tag)
        super[Inventory].writeToNBT(tag)
    }

    override def readFromNBT(tag : NBTTagCompound) : Unit = {
        super[UpdatingTile].readFromNBT(tag)
        super[Inventory].readFromNBT(tag)
    }

    override def markDirty(): Unit = {
        super[UpdatingTile].markDirty()
        super[Inventory].markDirty()
    }
}
