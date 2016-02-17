package com.dyonovan.neotech.common.tiles.misc

import com.dyonovan.neotech.managers.ItemManager
import com.teambr.bookshelf.common.tiles.traits.{Inventory, Syncable}
import net.minecraft.entity.{Entity, EntityList, EntityLivingBase}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.AxisAlignedBB
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

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
    final val NAME = 4

    var entity: Entity = _
    var entityType: String = _
    var scale: Float = 0.0F
    var rotation: Float = 0.0F
    var fitToBlock = true
    var lookAtPlayer = false
    var renderName = true

    override def initialSize: Int = 1

    override def update(): Unit = {
        super.update()
        if (entityType != null && entity == null) {
            onInventoryChanged(0)
        }
    }

    override def onInventoryChanged(slot: Int): Unit = {
        super.onInventoryChanged(0)
        if (getStackInSlot(0) != null && getStackInSlot(0).hasTagCompound) {
            val stack = getStackInSlot(0)
            entityType = stack.getTagCompound.getString("type")
            entity = EntityList.createEntityByName(entityType, worldObj)
            if (entity == null) {
                entityType = null
                worldObj.markBlockForUpdate(pos)
                return
            }
            entity.readFromNBT(stack.getTagCompound)
            entity.posX = 0
            entity.posY = 0
            entity.posZ = 0
            entity.setRotationYawHead(0.0F)
            entity.asInstanceOf[EntityLivingBase].renderYawOffset = 0.0F
            if (stack.hasDisplayName)
                entity.setCustomNameTag(stack.getDisplayName)
            worldObj.markBlockForUpdate(pos)
        } else {
            entity = null
            entityType = null
            worldObj.markBlockForUpdate(pos)
        }
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
        tag.setBoolean("Name", renderName)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[Syncable].readFromNBT(tag)
        super[Inventory].readFromNBT(tag)
        if (tag.hasKey("Type"))
            entityType = tag.getString("Type")
        rotation = tag.getFloat("Rotation")
        scale = tag.getFloat("Scale")
        fitToBlock = tag.getBoolean("Fit")
        lookAtPlayer = tag.getBoolean("Look")
        if (tag.hasKey("Name"))
            renderName = tag.getBoolean("Name")
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
            case NAME =>
                renderName = if (value != 0) true else false
                if (!worldObj.isRemote)
                    sendValueToClient(NAME, value)
            case _ =>
        }
    }

    override def getVariable(id: Int): Double = {
        id match {
            case SIZE => scale
            case DIRECTION => rotation
            case FIT => if (fitToBlock) 1.0 else 0.0
            case LOOK => if (lookAtPlayer) 1.0 else 0.0
            case NAME => if(renderName) 1.0 else 0.0
        }
    }

    @SideOnly(Side.CLIENT)
    override def getRenderBoundingBox: AxisAlignedBB = {
        val box = super.getRenderBoundingBox
        box.expand(0.0, 1.0, 0.0)
    }
}
