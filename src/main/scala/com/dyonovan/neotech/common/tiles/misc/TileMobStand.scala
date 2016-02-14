package com.dyonovan.neotech.common.tiles.misc

import com.dyonovan.neotech.managers.ItemManager
import com.teambr.bookshelf.common.tiles.traits.{Syncable, Inventory}
import net.minecraft.entity.{Entity, EntityList}
import net.minecraft.item.ItemStack
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
class TileMobStand extends TileEntity with Inventory with Syncable {

    final val SIZE = 0
    final val DIRECTION = 1

    var entity: Entity = _
    var scale: Float = 1.0F
    var rotation: Float = 0.0F

    override def initialSize: Int = 1

    override def onInventoryChanged(slot : Int) = {
        super.onInventoryChanged(slot)
        if (getStackInSlot(0) != null && getStackInSlot(0).hasTagCompound) {
            entity = EntityList.createEntityByName(getStackInSlot(0).getTagCompound.getString("type"), worldObj)
        } else entity = null
    }

    override def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean = {
        slot == 0 && stack.getItem.isInstanceOf[ItemManager.mobNet.type] && stack.hasTagCompound
    }

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].writeToNBT(tag)
        super[Inventory].writeToNBT(tag)
        if (entity != null)
            tag.setString("Type", EntityList.getEntityString(entity))
        tag.setFloat("Rotation", rotation)
        tag.setFloat("Scale", scale)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].readFromNBT(tag)
        super[Inventory].readFromNBT(tag)
        if (tag.hasKey("Type") && worldObj != null)
            entity = EntityList.createEntityByName(tag.getString("Type"), worldObj)
        rotation = tag.getFloat("Rotation")
        scale = tag.getFloat("Scale")
    }

    override def setVariable(id: Int, value: Double): Unit = {
        id match {
            case SIZE => scale = value.toFloat
            case DIRECTION => rotation = value.toFloat
            case _ =>
        }
    }

    override def getVariable(id: Int): Double = {
        id match {
            case SIZE => scale
            case DIRECTION => rotation
        }
    }
}
