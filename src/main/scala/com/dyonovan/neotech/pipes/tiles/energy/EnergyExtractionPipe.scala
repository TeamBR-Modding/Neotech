package com.dyonovan.neotech.pipes.tiles.energy

import java.util

import cofh.api.energy.{EnergyStorage, IEnergyProvider, IEnergyReceiver}
import com.dyonovan.neotech.pipes.entities.EnergyResourceEntity
import com.dyonovan.neotech.pipes.types.{ExtractionPipe, SimplePipe}
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.util.{BlockPos, EnumFacing}

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
    override def canConnect(facing: EnumFacing): Boolean =
        getWorld.getTileEntity(getPos.offset(facing)).isInstanceOf[SimplePipe] || getWorld.getTileEntity(pos.offset(facing)).isInstanceOf[IEnergyProvider]

    /**
     * This is the speed to extract from. You should be calling this when building your resources to send.
     *
     * This is included as a reminder to the child to have variable speeds
     * @return
     */
    override def getSpeed: Double = 1

    /**
     * Used to specify how much RF, check for upgrades here
     * @return
     */
    def getMaxRFDrain : Int = 200

    /**
     * Get how many ticks to 'cooldown' between operations.
     * @return 20 = 1 second
     */
    override def getDelay: Int = 20

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
        var tempStorage = new EnergyStorage(getMaxRFDrain)

        for(dir <- EnumFacing.values()) {
            worldObj.getTileEntity(pos.offset(dir)) match {
                case provider : IEnergyProvider =>
                    if(provider.getEnergyStored(dir.getOpposite) > 0) {
                        tempStorage = new EnergyStorage(getMaxRFDrain)
                        tempStorage.setEnergyStored(provider.extractEnergy(dir.getOpposite, getMaxRFDrain, true))
                        if(extractResourceOnShortestPath(new EnergyResourceEntity(tempStorage,
                            pos.getX + 0.5, pos.getY + 0.5, pos.getZ + 0.5, getSpeed,
                            pos, pos.north(), worldObj), simulate = true)) {
                            val resource = new EnergyResourceEntity(new EnergyStorage(getMaxRFDrain),
                                pos.getX + 0.5, pos.getY + 0.5, pos.getZ + 0.5, getSpeed,
                                pos, pos.north(), worldObj)
                            resource.resource.setEnergyStored(provider.extractEnergy(dir.getOpposite, getMaxRFDrain, false))
                            extractResourceOnShortestPath(resource, simulate = false)
                            return
                        }
                    }
                case _ =>
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
}
