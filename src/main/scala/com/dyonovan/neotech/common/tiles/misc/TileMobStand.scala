package com.dyonovan.neotech.common.tiles.misc

import com.dyonovan.neotech.managers.ItemManager
import com.teambr.bookshelf.common.tiles.traits.{Inventory, Syncable}
import net.minecraft.entity.{Entity, EntityList}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

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
class TileMobStand extends Syncable with Inventory {

    final val SIZE = 0
    final val DIRECTION = 1
    final val FIT = 2
    final val LOOK = 3

    var entity: Entity = _
    var entityType: String = _
    var scale: Float = 0.0F
    var rotation: Float = 0.0F
    var fitToBlock = true
    var lookAtPlayer = false

    override def initialSize: Int = 1

    override def onServerTick(): Unit = {
        if (entityType != null && entity == null) entity = EntityList.createEntityByName(entityType, worldObj)
    }

    override def onInventoryChanged(slot: Int) = {
        super.onInventoryChanged(slot)
        if (getStackInSlot(0) != null && getStackInSlot(0).hasTagCompound) {
            entityType = getStackInSlot(0).getTagCompound.getString("type")
            entity = EntityList.createEntityByName(entityType, worldObj)
        } else {
            entity = null
            entityType = null
        }
        worldObj.markBlockForUpdate(pos)
    }

    override def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean = {
        slot == 0 && stack.getItem.isInstanceOf[ItemManager.mobNet.type] && stack.hasTagCompound
    }

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super[Syncable].writeToNBT(tag)
        super[Inventory].writeToNBT(tag)
        if (entity != null)
            tag.setString("Type", EntityList.getEntityString(entity))
        tag.setFloat("Rotation", rotation)
        tag.setFloat("Scale", scale)
        tag.setBoolean("Fit", fitToBlock)
        tag.setBoolean("Look", lookAtPlayer)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[Syncable].readFromNBT(tag)
        super[Inventory].readFromNBT(tag)
        if (tag.hasKey("Type"))
            entityType = tag.getString("Type")
        if (worldObj != null && entityType != null)
            entity = EntityList.createEntityByName(entityType, worldObj)
        rotation = tag.getFloat("Rotation")
        scale = tag.getFloat("Scale")
        fitToBlock = tag.getBoolean("Fit")
        lookAtPlayer = tag.getBoolean("Look")
    }

    override def setVariable(id: Int, value: Double): Unit = {
        id match {
            case SIZE =>
                scale = value.toFloat
                if (!worldObj.isRemote)
                    sendValueToClient(SIZE, value)
            case DIRECTION =>
                rotation = value.toFloat
                if (!worldObj.isRemote)
                    sendValueToClient(DIRECTION, value)
            case FIT =>
                fitToBlock = if (value != 0) true else false
                if (!worldObj.isRemote)
                    sendValueToClient(FIT, value)
            case LOOK =>
                lookAtPlayer = if (value != 0) true else false
                if (!worldObj.isRemote)
                    sendValueToClient(LOOK, value)
            case _ =>
        }
    }

    override def getVariable(id: Int): Double = {
        id match {
            case SIZE => scale
            case DIRECTION => rotation
            case FIT => if (fitToBlock) 1.0 else 0.0
            case LOOK => if (lookAtPlayer) 1.0 else 0.0
        }
    }
}
