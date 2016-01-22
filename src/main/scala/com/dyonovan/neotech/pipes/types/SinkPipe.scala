package com.dyonovan.neotech.pipes.types

import com.dyonovan.neotech.common.blocks.traits.Upgradeable
import com.dyonovan.neotech.pipes.collections.ConnectedSides
import com.dyonovan.neotech.pipes.entities.ResourceEntity
import com.teambr.bookshelf.common.tiles.traits.Syncable
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis pauljoda
  * @since August 16, 2015
  */
trait SinkPipe[T, R <: ResourceEntity[T]] extends SimplePipe with Upgradeable with Syncable {
    val connections = new ConnectedSides

    override def canConnect(facing: EnumFacing): Boolean = connections.get(facing.ordinal()) && super[SimplePipe].canConnect(facing)

    /**
      * Used to check if this pipe can accept a resource
      *
      * You should not actually change anything, all simulation
      * @param resource
      * @return
      */
    def willAcceptResource(resource: ResourceEntity[_]) : Boolean

    /**
      * Called when the resource enters this pipe. If it is meant for us, we will try and insert
      * @param resource
      */
    override def onResourceEnteredPipe(resource: ResourceEntity[_]): Unit = {
        resource match {
            case matchedResource: R if resource.destination == getPos => tryInsertResource(matchedResource)
            case _ =>
        }
    }

    /**
      * Try and insert the resource into an inventory.
      *
      * It is pretty good practice to send the resource back if you can't remove all of it
      * @param resource
      */
    def tryInsertResource(resource : R)

    /**
      * This is mainly used to sending info to the client so it knows what to render. It will also be used to save on world
      * exit. You should only be saving the things needed for those instances.
      *
      * @param tag
      */
    override def writeToNBT(tag : NBTTagCompound) : Unit = {
        super[Upgradeable].writeToNBT(tag)
        connections.writeToNBT(tag)
    }

    /**
      * Receives the data from the server. Will not be full info needed for the resources.
      *
      * If you are on the server side, you must set the resource world object to the worldObj. Additional info may be
      * required.
      *
      * Note, if you do forget to set the world, the onServerTick method will try to save it. But for safety, just add it
      * @param tag
      */
    override def readFromNBT(tag : NBTTagCompound) : Unit = {
        super[Upgradeable].readFromNBT(tag)
        connections.readFromNBT(tag)
    }

    override def markDirty() = {
        super[Upgradeable].markDirty()
    }

    @SideOnly(Side.CLIENT)
    def getGUIHeight : Int = {
        var baseHeight = 41
        if(getUpgradeBoard != null && getUpgradeBoard.hasControl)
            baseHeight += 60
        if(getUpgradeBoard != null && getUpgradeBoard.hasExpansion)
            baseHeight += 30
        baseHeight
    }

    override def setVariable(id : Int, value : Double) = {
        id match {
            case 2 =>
                connections.set(value.toInt, !connections.get(value.toInt))
                getWorld.markBlockRangeForRenderUpdate(getPos, getPos)
            case _ =>
        }
    }

    override def getVariable(id : Int) : Double = 0.0D

    override def resetValues() = {
        for(x <- connections.connections.indices)
            connections.set(x, value = true)
    }
}
