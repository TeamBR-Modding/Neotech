package com.dyonovan.neotech.pipes.collections

import com.teambr.bookshelf.traits.NBTSavable
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/21/2016
  */
class ConnectedSides extends NBTSavable {
    val connections = new Array[Boolean](EnumFacing.values().size)
    for (elem <- connections.indices) {connections(elem) = true}

    def get(pos : Int) : Boolean = connections(pos)

    def set(pos : Int, value : Boolean) = connections(pos) = value

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        for(x <- connections.indices)
            tag.setBoolean("Connection" + x, connections(x))
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        for(x <- 0 until EnumFacing.values.size)
            connections(x) = tag.getBoolean("Connection" + x)
    }
}
