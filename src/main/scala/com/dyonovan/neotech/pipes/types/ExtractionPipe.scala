package com.dyonovan.neotech.pipes.types

import java.util

import com.dyonovan.neotech.pipes.entities.ResourceEntity
import com.teambr.bookshelf.common.tiles.traits.UpdatingTile
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{Vec3, AxisAlignedBB, BlockPos, EnumFacing}
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
trait ExtractionPipe[T, R <: ResourceEntity[T]] extends UpdatingTile with SimplePipe {
    /**
     * Useful in round robin
     */
    var lastSink: Long = 0

    val sinks = new util.ArrayList[Long]()
    val distance: util.HashMap[Long, Integer] = new util.HashMap[Long, Integer]
    val parent: util.HashMap[Long, BlockPos] = new util.HashMap[Long, BlockPos]
    val queue: util.Queue[BlockPos] = new util.LinkedList[BlockPos]
    //Create a queue
    var nextResource: R = _

    /**
     * Get how many ticks to 'cooldown' between operations.
     * @return 20 = 1 second
     */
    def getDelay: Int

    //Set the initial cooldown to max
    var coolDown = getDelay

    //Our storage of resources
    var resources: util.ArrayList[R] = new util.ArrayList[R]()

    /**
     * Used to add a resource
     */
    def addResource(resource: R): Unit = {
        resources.add(resource)
    }

    /**
     * Used to extract the resource on the shortest path possible
     *
     * This will handle finding things that will accept it and will pick the shortest path of all of them
     *
     * You can use this to simulate as well, you are responsible for removing from the tile
     *
     * @param resource The resource to check
     */
    def extractResourceOnShortestPath(resource: R, simulate: Boolean): Boolean = {
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
                                        case pipe: SinkPipe[T, R] =>
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

            //Find the shortest
            var destination = new BlockPos(getPos)
            var shortest = Integer.MAX_VALUE
            for (i <- 0 until sinks.size()) {
                val d = BlockPos.fromLong(sinks.get(i))
                if (distance.get(d.toLong) < shortest) {
                    destination = d
                    shortest = distance.get(d.toLong)
                }
            }

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
            //If we have a path add it
            if (!simulate) {
                resources.add(resource)
                distance.clear()
                parent.clear()
                queue.clear()
                sinks.clear()
            } else {
                nextResource = resource
            }
            true
        }
        else
            false
    }

    def extractOnRoundRobin(resource: R, simulate: Boolean): Boolean = {
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
                                        case pipe: SinkPipe[T, R] =>
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
    def resourceReturned(resource : R)

    override def onServerTick() : Unit = {
        //Update our resources
        if(!resources.isEmpty) {
            val iterator = resources.iterator()
            while (iterator.hasNext) {
                val resource = iterator.next()
                if(getWorld != null)
                    resource.setWorld(getWorld)
                if (resource.isDead || resource.resource == null || resource == null) {
                    resource.onDropInWorld()
                    iterator.remove()
                }
                else
                    resource.updateEntity()
            }
            getWorld.markBlockForUpdate(getPos)
        }

        coolDown = coolDown - 1
        if(coolDown <= 0) {
            coolDown = getDelay
            doExtraction() //TODO: Add different modes
        }
    }

    /**
     * This is what is actually called to the child class. Here you should call your extractResources or whatever you want
     * this pipe to do on its action phase. The parent will not automatically call extract
     *
     * This is useful if you wish to set different modes and call different path finding
     */
    def doExtraction() : Unit

    /**
     * The first step in moving things. You should call this from doExtraction. This is an outside method so you can
     * have additional functions to the pipe besides just extracting. For example, a pipe that pulls items in the world
     */
    def tryExtractResources() : Unit

    /**
     * This is the speed to extract from. You should be calling this when building your resources to send.
     *
     * This is included as a reminder to the child to have variable speeds
     * @return
     */
    def getSpeed : Double

    /**
     * Called when something passes by. We only care if the resource is being sent back to us
     */
    override def onResourceEnteredPipe(resource: ResourceEntity[_]): Unit = {
        resource match {
            case matchingResource: R if resource.destination == getPos && !resource.isDead =>
                resourceReturned(matchingResource)
            case _ =>
        }
    }

    /**
     * Make sure we don't lose everything when we are broken
     */
    override def onPipeBroken(): Unit = {
        for(i <- 0 until resources.size())
            resources.get(i).onDropInWorld()
    }

    /**
     * This is mainly used to sending info to the client so it knows what to render. It will also be used to save on world
     * exit. You should only be saving the things needed for those instances.
     *
     * @param tag
     */
    override def writeToNBT(tag : NBTTagCompound)

    /**
     * Receives the data from the server. Will not be full info needed for the resources.
     *
     * If you are on the server side, you must set the resource world object to the worldObj. Additional info may be
     * required.
     *
     * Note, if you do forget to set the world, the onServerTick method will try to save it. But for safety, just add it
     * @param tag
     */
    override def readFromNBT(tag : NBTTagCompound)

    /**
     * If we have some important stuff, make sure we always render. Otherwise you'll only see what is in the pipe
     * if the pipe is in view. To cut on resources, if there is nothing in the buffer we return the standard render box
     * @return
     */
    @SideOnly(Side.CLIENT)
    override def getRenderBoundingBox : AxisAlignedBB = {
        if(!resources.isEmpty)
            TileEntity.INFINITE_EXTENT_AABB
        else
            super.getRenderBoundingBox
    }
}
