package com.dyonovan.neotech.pipes.tiles.energy

import java.util

import cofh.api.energy.{EnergyStorage, IEnergyProvider, IEnergyReceiver}
import com.dyonovan.neotech.pipes.entities.EnergyResourceEntity
import com.dyonovan.neotech.pipes.types.{SinkPipe, ExtractionPipe, SimplePipe}
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.util.{Vec3, BlockPos, EnumFacing}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis pauljoda
  * @since August 17, 2015
  */
class EnergyExtractionPipe extends ExtractionPipe[EnergyStorage, EnergyResourceEntity] {

    mode = 2

    override def canConnect(facing: EnumFacing): Boolean =
        super.canConnect(facing) &&
                (getWorld.getTileEntity(getPos.offset(facing)).isInstanceOf[SimplePipe] ||
                    (getWorld.getTileEntity(pos.offset(facing)).isInstanceOf[IEnergyProvider] &&
                            getWorld.getTileEntity(pos.offset(facing)).asInstanceOf[IEnergyProvider].canConnectEnergy(facing.getOpposite)))

    /**
      * This is the speed to extract from. You should be calling this when building your resources to send.
      *
      * This is included as a reminder to the child to have variable speeds
      * @return
      */
    override def getSpeed: Double = {
        if(getUpgradeBoard != null && getUpgradeBoard.getProcessorCount > 0)
            getUpgradeBoard.getProcessorCount * 0.5
        else
            0.5
    }
    /**
      * Used to specify how much RF, check for upgrades here
      * @return
      */
    def getMaxRFDrain : Int = {
        var rate = 200
        if(getUpgradeBoard != null && getUpgradeBoard.getHardDriveCount > 0)
            rate *= (getUpgradeBoard.getHardDriveCount * 4)
        if(acceptRF == -1)
            rate * 10
        else acceptRF
    }
    var acceptRF : Int = -1

    /**
      * Get how many ticks to 'cooldown' between operations.
      * @return 20 = 1 second
      */
    override def getDelay: Int = {
        if(getUpgradeBoard != null && getUpgradeBoard.getProcessorCount > 0)
            20 - getUpgradeBoard.getProcessorCount * 2
        else
            20
    }
    /**
      * This is what is actually called to the child class. Here you should call your extractResources or whatever you want
      * this pipe to do on its action phase. The parent will not automatically call extract
      *
      * This is useful if you wish to set different modes and call different path finding
      */
    override def doExtraction(): Unit = {
        tryExtractResources()
    }

    /**
      * The first step in moving things. You should call this from doExtraction. This is an outside method so you can
      * have additional functions to the pipe besides just extracting. For example, a pipe that pulls items in the world
      */
    override def tryExtractResources(): Unit = {
        for(dir <- EnumFacing.values()) {
            if (canConnect(dir)) {
                getWorld.getTileEntity(pos.offset(dir)) match {
                    case provider: IEnergyProvider =>
                        if (provider.getEnergyStored(dir.getOpposite) > 0) {
                            val tempStorage = new EnergyStorage(getMaxRFDrain)
                            tempStorage.setEnergyStored(provider.extractEnergy(dir.getOpposite, getMaxRFDrain, true))
                            val energyResourceEntity = new EnergyResourceEntity(tempStorage,
                                pos.getX + 0.5, pos.getY + 0.5, pos.getZ + 0.5, getSpeed,
                                pos, pos.north(), getWorld)
                            if (extractOnMode(energyResourceEntity, simulate = true)) {
                                provider.extractEnergy(dir.getOpposite, tempStorage.getEnergyStored, false)
                                extractOnMode(nextResource, simulate = false)
                                return
                            }
                        }
                    case _ =>
                }
            }
        }
    }

    /**
      * This is called when we fail to send a resource. You should put the resource back where you found it or
      * add it to the world
      * @param resource
      */
    override def resourceReturned(resource: EnergyResourceEntity): Unit = {
        val tempStorage = new EnergyStorage(resource.resource.getMaxEnergyStored, resource.resource.getMaxReceive, resource.resource.getMaxExtract)
        tempStorage.setEnergyStored(resource.resource.getEnergyStored)

        for(dir <- EnumFacing.values()) {
            worldObj.getTileEntity(pos.offset(dir)) match {
                case receiver : IEnergyReceiver if tempStorage.getEnergyStored > 0 =>
                    receiver.receiveEnergy(dir.getOpposite, tempStorage.extractEnergy(tempStorage.getEnergyStored, false), false)
                case _ =>
            }
        }

        resource.resource = tempStorage
        if(resource.resource.getEnergyStored <= 0) {
            resource.isDead = true
        } else {
            val tempLocation = new BlockPos(resource.from)
            resource.from = new BlockPos(pos)
            resource.destination = new BlockPos(tempLocation)
            resource.findPathToDestination()
        }
    }

    override def writeToNBT(tag : NBTTagCompound) : Unit = {
        super.writeToNBT(tag)
        super[TileEntity].writeToNBT(tag)
        tag.setInteger("SizeResources", resources.size())
        val resourceList = new NBTTagList
        for(i <- 0 until resources.size()) {
            val resourceTag = new NBTTagCompound
            resources.get(i).writeToNBT(resourceTag)
            resourceList.appendTag(resourceTag)
        }
        tag.setTag("Resources", resourceList)
    }

    override def readFromNBT(tag : NBTTagCompound) : Unit = {
        super.readFromNBT(tag)
        super[TileEntity].readFromNBT(tag)
        val resourceList = tag.getTagList("Resources", 10)
        resources = new util.ArrayList[EnergyResourceEntity]()
        for(i <- 0 until resourceList.tagCount()) {
            val resourceTag = resourceList.getCompoundTagAt(i)
            val item = new EnergyResourceEntity()
            item.readFromNBT(resourceTag)
            if(worldObj != null && !worldObj.isRemote) {
                item.setWorld(worldObj)
            }
            resources.add(item)
        }
    }

    /**
      * Called when the board is removed, reset to default values
      */
    override def resetValues(): Unit = {
        mode = 2
        redstone = 0
        frequency = 0
        for(x <- connections.connections.indices)
            connections.set(x, value = true)
        worldObj.markBlockForUpdate(pos)
    }
}
