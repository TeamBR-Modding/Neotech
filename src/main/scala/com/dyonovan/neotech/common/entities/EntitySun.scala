package com.dyonovan.neotech.common.entities

import net.minecraft.entity.Entity
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
    override def entityInit(): Unit = {

    }

    override def writeEntityToNBT(tagCompound: NBTTagCompound): Unit = {

    }

    override def readEntityFromNBT(tagCompund: NBTTagCompound): Unit = {
    }
}
