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
                                nextResource.resource.setEnergyStored(provider.extractEnergy(dir.getOpposite, getMaxRFDrain, false))
                                extractOnMode(nextResource, simulate = false)
                                return
                            }
                        }
                    case _ =>
                }
            }
        }
    }

    override def extractOnRoundRobin(resource: EnergyResourceEntity, simulate: Boolean): Boolean = {
        //Sometimes we won't get anything, get lost
        if (resource == null)
            return false

        if (simulate) {
            sinks.clear()
            distance.clear()
            parent.clear()
            queue.clear()

            distance.put(getPosAsLong, 0) //We are right here
            parent.put(getPosAsLong, null) //No parent

            queue.add(BlockPos.fromLong(getPosAsLong)) //Add ourselves

            //Search the graph
            while (!queue.isEmpty) {
                val thisPos: BlockPos = queue.poll
                getWorld.getTileEntity(thisPos) match {
                    //Make sure this is a pipe
                    case thisPipe: SimplePipe =>
                        for (facing <- EnumFacing.values) {
                            //Add children
                            if (thisPipe.canConnect(facing)) {
                                val otherPos: BlockPos = thisPos.offset(facing)
                                if (distance.get(otherPos.toLong) == null) {
                                    //If it hasn't already been added
                                    queue.add(otherPos)
                                    distance.put(otherPos.toLong, Integer.MAX_VALUE) //We will set the distance later
                                    parent.put(otherPos.toLong, null) //Also parent

                                    val newDistance: Int = (distance.get(thisPos.toLong) + thisPos.distanceSq(otherPos)).toInt
                                    //If our distance is less than what existed, replace
                                    if (newDistance < distance.get(otherPos.toLong)) {
                                        distance.put(otherPos.toLong, newDistance)
                                        parent.put(otherPos.toLong, thisPos)
                                    }

                                    getWorld.getTileEntity(otherPos) match {
                                        //Add to sinks
                                        case pipe: SinkPipe[EnergyStorage, EnergyResourceEntity] if pipe.frequency == frequency =>
                                            if (pipe.willAcceptResource(resource))
                                                sinks.add(pipe.getPosAsLong)
                                        case _ =>
                                    }
                                }
                            }
                        }
                    case _ =>
                }
            }

            //Find the next source
            var destination: BlockPos = null
            var pickNext: Boolean = lastSink == 0
            val lastLastSink = lastSink
            for (i <- 0 until sinks.size()) {
                if (pickNext) {
                    destination = BlockPos.fromLong(sinks.get(i))
                    lastSink = sinks.get(i)
                    pickNext = false
                }
                if (sinks.get(i) == lastSink && destination == null)
                    pickNext = true
            }

            if (destination == null && !sinks.isEmpty) {
                destination = BlockPos.fromLong(sinks.get(0))
                lastSink = sinks.get(0)
            }
            else if (destination == null) {
                lastSink = 0
                return false
            }

            if(!simulate)
                lastSink = lastLastSink

            //Build the path to the shortest
            resource.pathQueue.clear()
            resource.destination = destination
            var u: BlockPos = destination
            while (parent.get(u.toLong) != null) {
                resource.pathQueue.push(new Vec3(u.getX + 0.5, u.getY + 0.5, u.getZ + 0.5))
                u = parent.get(u.toLong)
            }
        }

        if (!resource.pathQueue.isEmpty) {
            worldObj.getTileEntity(resource.destination) match {
                case acceptor:EnergySinkPipe=>
                    acceptRF = acceptor.pingAmountNeeded()
                case _=> acceptRF = -1
            }

            //If we have a path add it
            if (!simulate) {
                resources.add(resource)
                sinks.clear()
                distance.clear()
                parent.clear()
                queue.clear()
            } else {
                nextResource = resource
            }
            true
        }
        else
            false
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
