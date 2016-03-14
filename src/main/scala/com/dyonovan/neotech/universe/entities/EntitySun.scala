package com.dyonovan.neotech.universe.entities

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 3/12/2016
  */
class EntitySun(world : World) extends Entity(world) {
    var radius : Float = 0.23F
    var sunType : EnumSunType = EnumSunType.INERT
    isImmuneToFire = true
    var needsUpdate = false

    setSize(sunType.getDefaultRadius * 2, sunType.getDefaultRadius * 2)
    radius = sunType.getDefaultRadius

    def this(world: World, sun: EnumSunType) = {
        this(world)
        sunType = sun
        setSize(sunType.getDefaultRadius * 2, sunType.getDefaultRadius * 2)
        radius = sunType.getDefaultRadius

        getDataWatcher.updateObject(20, radius)
        getDataWatcher.setObjectWatched(20)

        getDataWatcher.updateObject(21, sunType.ordinal())
        getDataWatcher.setObjectWatched(21)
    }

    override def entityInit(): Unit = {
        getDataWatcher.addObjectByDataType(20, 3)
        getDataWatcher.addObjectByDataType(21, 2)
    }

    override def writeEntityToNBT(tagCompound: NBTTagCompound): Unit = {
        tagCompound.setFloat("Radius", radius)
        sunType.writeInfoToTag(tagCompound)
    }

    override def readEntityFromNBT(tagCompound: NBTTagCompound): Unit = {
        radius = tagCompound.getFloat("Radius")
        getDataWatcher.updateObject(20, radius)
        getDataWatcher.setObjectWatched(20)

        sunType = sunType.getTypeFromTag(tagCompound)
        getDataWatcher.updateObject(21, sunType.ordinal())
        getDataWatcher.setObjectWatched(21)
    }

    override def clientUpdateEntityNBT(tagCompound: NBTTagCompound): Unit = {
        readEntityFromNBT(tagCompound)
    }

    override def canBeCollidedWith = true

    override def onCollideWithPlayer(entityIn: EntityPlayer): Unit ={
        if(sunType != EnumSunType.INERT)
            entityIn.setFire((radius * 5).toInt)
    }

    override def hitByEntity(entityIn: Entity): Boolean = {
        setDead()
        false
    }
}
