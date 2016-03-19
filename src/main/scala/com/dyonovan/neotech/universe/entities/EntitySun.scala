package com.dyonovan.neotech.universe.entities

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.datasync.{DataSerializers, EntityDataManager}
import net.minecraft.util.math.{AxisAlignedBB, Vec3d}
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
    lazy val BASE_RADIUS_DRAIN = 0.0001F

    lazy val DATA_WATCHER_RADIUS = EntityDataManager.createKey[java.lang.Float](classOf[EntitySun], DataSerializers.FLOAT)
    lazy val DATA_WATCHER_TYPE =  EntityDataManager.createKey[java.lang.Integer](classOf[EntitySun], DataSerializers.VARINT)

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

        getDataManager.set[java.lang.Float](DATA_WATCHER_RADIUS, radius)
        getDataManager.set[java.lang.Integer](DATA_WATCHER_TYPE, sunType.ordinal())
    }

    override def entityInit(): Unit = {
        getDataManager.register[java.lang.Float](DATA_WATCHER_RADIUS, radius)
        getDataManager.register[java.lang.Integer](DATA_WATCHER_TYPE, sunType.ordinal())
    }

    override def onUpdate(): Unit = {
        super.onUpdate()

        if(!worldObj.isRemote)
            drainPower()

        // Dirty fix until we have all things integrated and can change on the fly
        if(worldObj.isRemote) {
            this.setEntityBoundingBox(
                new AxisAlignedBB(
                    posX - getDataManager.get(DATA_WATCHER_RADIUS),
                    posY - getDataManager.get(DATA_WATCHER_RADIUS),
                    posZ - getDataManager.get(DATA_WATCHER_RADIUS),
                    posX + getDataManager.get(DATA_WATCHER_RADIUS),
                    posY + getDataManager.get(DATA_WATCHER_RADIUS),
                    posZ + getDataManager.get(DATA_WATCHER_RADIUS)))
            width = radius * 2
            height = radius * 2
        }
    }

    override def writeEntityToNBT(tagCompound: NBTTagCompound): Unit = {
        tagCompound.setFloat("Radius", radius)
        sunType.writeInfoToTag(tagCompound)
    }

    override def readEntityFromNBT(tagCompound: NBTTagCompound): Unit = {
        radius = tagCompound.getFloat("Radius")
        getDataManager.set[java.lang.Float](DATA_WATCHER_RADIUS, radius)

        sunType = sunType.getTypeFromTag(tagCompound)
        getDataManager.set[java.lang.Integer](DATA_WATCHER_TYPE, sunType.ordinal())

        this.setEntityBoundingBox(
            new AxisAlignedBB(
                posX - getDataManager.get[java.lang.Float](DATA_WATCHER_RADIUS),
                posY - getDataManager.get[java.lang.Float](DATA_WATCHER_RADIUS),
                posZ - getDataManager.get[java.lang.Float](DATA_WATCHER_RADIUS),
                posX + getDataManager.get[java.lang.Float](DATA_WATCHER_RADIUS),
                posY + getDataManager.get[java.lang.Float](DATA_WATCHER_RADIUS),
                posZ + getDataManager.get[java.lang.Float](DATA_WATCHER_RADIUS)))
        width = radius * 2
        height = radius * 2
    }

    /**
      * Used to move the sun in a direction vector
      *
      * @param dir The direction to move
      */
    def moveSunInDirection(dir : Vec3d) : Unit = {
        addVelocity(
            dir.xCoord / (radius * MASS_CONSTANT),
            dir.yCoord / (radius * MASS_CONSTANT),
            dir.zCoord / (radius * MASS_CONSTANT))
    }

    def drainPower() : Int = {
        if (sunType.ordinal() > 1) {
            radius -= BASE_RADIUS_DRAIN
            getDataManager.set[java.lang.Float](DATA_WATCHER_RADIUS, radius)

            this.setEntityBoundingBox(
                new AxisAlignedBB(
                    posX - getDataManager.get[java.lang.Float](DATA_WATCHER_RADIUS),
                    posY - getDataManager.get[java.lang.Float](DATA_WATCHER_RADIUS),
                    posZ - getDataManager.get[java.lang.Float](DATA_WATCHER_RADIUS),
                    posX + getDataManager.get[java.lang.Float](DATA_WATCHER_RADIUS),
                    posY + getDataManager.get[java.lang.Float](DATA_WATCHER_RADIUS),
                    posZ + getDataManager.get[java.lang.Float](DATA_WATCHER_RADIUS)))
            width = radius * 2
            height = radius * 2

            // Check if we need to move down a tier
            if (sunType.ordinal() > 1) {
                val smallerSunType = EnumSunType.values()(sunType.ordinal() - 1)
                if (radius <= smallerSunType.getDefaultRadius) {
                    sunType = smallerSunType
                    getDataManager.set[java.lang.Integer](DATA_WATCHER_TYPE, sunType.ordinal())
                }
            }

            BASE_RF_TICK * (radius * 200).toInt
        } else if(sunType == EnumSunType.INERT) 0 else BASE_RF_TICK * (radius * 25).toInt
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
