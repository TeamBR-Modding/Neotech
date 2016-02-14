package com.dyonovan.neotech.common.tiles.misc

import com.teambr.bookshelf.common.tiles.traits.Inventory
import net.minecraft.client.renderer.entity.{RenderCow, Render}
import net.minecraft.entity.{EntityList, Entity}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/13/2016
  */
class TileMobStand extends TileEntity with Inventory {

    override def initialSize: Int = 1

    override def onInventoryChanged(slot : Int) = {
        super.onInventoryChanged(slot)
        if (getStackInSlot(0) != null && getStackInSlot(0).hasTagCompound) {
            val entity = EntityList.createEntityByName(getStackInSlot(0).getTagCompound.getString("type"), worldObj)

        }
    }

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].writeToNBT(tag)
        super[Inventory].writeToNBT(tag)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].readFromNBT(tag)
        super[Inventory].readFromNBT(tag)
    }
}
