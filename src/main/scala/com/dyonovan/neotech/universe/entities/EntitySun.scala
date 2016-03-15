package com.dyonovan.neotech.universe.entities

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{Vec3, AxisAlignedBB}
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
    lazy val MASS_CONSTANT = 10
    lazy val BASE_RF_TICK = 200
    lazy val BASE_RADIUS_DRAIN = 0.001F

    lazy val DATA_WATCHER_RADIUS = 20
    lazy val DATA_WATCHER_TYPE = 21

    var radius : Float = 0.23F
    var sunType : EnumSunType = EnumSunType.INERT
    isImmuneToFire = true
    var needsUpdate = false

    def this(world: World, sun: EnumSunType) = {
        this(world)
        sunType = sun
        width = sunType.getDefaultRadius * 2
        height = sunType.getDefaultRadius * 2
        radius = sunType.getDefaultRadius

        getDataWatcher.updateObject(DATA_WATCHER_RADIUS, radius)
        getDataWatcher.setObjectWatched(DATA_WATCHER_RADIUS)

        getDataWatcher.updateObject(DATA_WATCHER_TYPE, sunType.ordinal())
        getDataWatcher.setObjectWatched(DATA_WATCHER_TYPE)
    }

    override def entityInit(): Unit = {
        getDataWatcher.addObjectByDataType(DATA_WATCHER_RADIUS, 3)
        getDataWatcher.addObjectByDataType(DATA_WATCHER_TYPE, 2)
    }

    override def onUpdate(): Unit = {
        super.onUpdate()

        if(!worldObj.isRemote)
            drainPower()

        // Dirty fix until we have all things integrated and can change on the fly
        this.setEntityBoundingBox(
            new AxisAlignedBB(
                posX - getDataWatcher.getWatchableObjectFloat(DATA_WATCHER_RADIUS),
                posY - getDataWatcher.getWatchableObjectFloat(DATA_WATCHER_RADIUS),
                posZ - getDataWatcher.getWatchableObjectFloat(DATA_WATCHER_RADIUS),
                posX + getDataWatcher.getWatchableObjectFloat(DATA_WATCHER_RADIUS),
                posY + getDataWatcher.getWatchableObjectFloat(DATA_WATCHER_RADIUS),
                posZ + getDataWatcher.getWatchableObjectFloat(DATA_WATCHER_RADIUS)))
        width = radius * 2
        height = radius * 2
    }

    override def writeEntityToNBT(tagCompound: NBTTagCompound): Unit = {
        tagCompound.setFloat("Radius", radius)
        sunType.writeInfoToTag(tagCompound)
    }

    override def readEntityFromNBT(tagCompound: NBTTagCompound): Unit = {
        radius = tagCompound.getFloat("Radius")
        getDataWatcher.updateObject(DATA_WATCHER_RADIUS, radius)
        getDataWatcher.setObjectWatched(DATA_WATCHER_RADIUS)

        sunType = sunType.getTypeFromTag(tagCompound)
        getDataWatcher.updateObject(DATA_WATCHER_TYPE, sunType.ordinal())
        getDataWatcher.setObjectWatched(DATA_WATCHER_TYPE)
    }

    override def clientUpdateEntityNBT(tagCompound: NBTTagCompound): Unit = {
        readEntityFromNBT(tagCompound)
    }

    /**
      * Used to move the sun in a direction vector
      *
      * @param dir The direction to move
      */
    def moveSunInDirection(dir : Vec3) : Unit = {
        addVelocity(
            dir.xCoord / (radius * MASS_CONSTANT),
            dir.yCoord / (radius * MASS_CONSTANT),
            dir.zCoord / (radius * MASS_CONSTANT))
    }

    def drainPower() : Int = {
        if(sunType != EnumSunType.INERT) {
            radius -= BASE_RADIUS_DRAIN
            getDataWatcher.updateObject(DATA_WATCHER_RADIUS, radius)
            getDataWatcher.setObjectWatched(DATA_WATCHER_RADIUS)

            // Check if we need to move down a tier
            if (sunType.ordinal() > 0) {
                val smallerSunType = EnumSunType.values()(sunType.ordinal() - 1)
                if (radius <= smallerSunType.getDefaultRadius) {
                    sunType = smallerSunType
                    getDataWatcher.updateObject(DATA_WATCHER_TYPE, sunType.ordinal())
                    getDataWatcher.setObjectWatched(DATA_WATCHER_TYPE)
                }
            }

            BASE_RF_TICK * (radius * 200).toInt
        }else 0
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
