package com.dyonovan.neotech.pipes.types

import java.util
import javax.vecmath.Vector3d

import com.dyonovan.neotech.pipes.network.ResourceEntity
import com.teambr.bookshelf.common.tiles.traits.UpdatingTile
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{AxisAlignedBB, BlockPos, EnumFacing}
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
    val lastSink : Long = 0

    //Our storage of resources
    var resources : util.ArrayList[R] = new util.ArrayList[R]()

    /**
     * Used to add a resource
     */
    def addResource(resource : R) : Unit = {
        resources.add(resource)
    }

    def extractResourceOnShortestPath(resource : R): Unit = {
        //Sometimes we won't get anything, get lost
        if(resource == null)
            return

        //What we wish to send
        val sinks = new util.ArrayList[Long]()
        val distance: util.HashMap[Long, Integer] = new util.HashMap[Long, Integer]
        val parent: util.HashMap[Long, BlockPos] = new util.HashMap[Long, BlockPos]

        distance.put(getPosAsLong, 0) //We are right here
        parent.put(getPosAsLong, null) //No parent

        val queue: util.Queue[BlockPos] = new util.LinkedList[BlockPos] //Create a queue
        queue.add(BlockPos.fromLong(getPosAsLong)) //Add ourselves

        //Search the graph
        while (!queue.isEmpty) {
            val thisPos: BlockPos = queue.poll
            getWorld.getTileEntity(thisPos) match { //Make sure this is a pipe
                case thisPipe: SimplePipe =>
                    for (facing <- EnumFacing.values) { //Add children
                        if (thisPipe.canConnect(facing)) {
                            val otherPos: BlockPos = thisPos.offset(facing)
                            if (distance.get(otherPos.toLong) == null) { //If it hasn't already been added
                                queue.add(otherPos)
                                distance.put(otherPos.toLong, Integer.MAX_VALUE) //We will set the distance later
                                parent.put(otherPos.toLong, null) //Also parent

                                val newDistance: Int = (distance.get(thisPos.toLong) + thisPos.distanceSq(otherPos)).toInt
                                //If our distance is less than what existed, replace
                                if (newDistance < distance.get(otherPos.toLong)) {
                                    distance.put(otherPos.toLong, newDistance)
                                    parent.put(otherPos.toLong, thisPos)
                                }

                                getWorld.getTileEntity(otherPos) match { //Add to sinks
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
            resource.pathQueue.push(new Vector3d(u.getX + 0.5, u.getY + 0.5, u.getZ + 0.5))
            u = parent.get(u.toLong)
        }

        if (!resource.pathQueue.isEmpty) //If we have a path add it
            resources.add(resource)
        else //Otherwise put it back
            resourceReturned(resource)
    }

    def resourceReturned(resource : R)

    override def onServerTick() : Unit = {
        //Update our resources
        if(!resources.isEmpty) {
            val iterator = resources.iterator()
            while (iterator.hasNext) {
                val resource = iterator.next()
                resource.setWorld(getWorld)
                if (resource.isDead || resource.resource == null) {
                    resource.onDropInWorld()
                    iterator.remove()
                }
                else
                    resource.updateEntity()
            }
            getWorld.markBlockForUpdate(getPos)
        }

        //Try to pull from inventories
        if(getWorld.rand.nextInt(20) == 10)
            doExtraction() //TODO: Add different modes
    }

    def doExtraction() : Unit

    def tryExtractResources() : Unit

    def getSpeed : Double

    override def onResourceEnteredPipe(resource: ResourceEntity[_]): Unit = {
        resource match {
            case matchingResource: R if resource.destination == getPos && !resource.isDead =>
                tryInsertResource(matchingResource)
            case _ =>
        }
    }

    def tryInsertResource(resource : R)

    override def onPipeBroken(): Unit = {
        for(i <- 0 until resources.size())
            resources.get(i).onDropInWorld()
    }

    override def writeToNBT(tag : NBTTagCompound)

    override def readFromNBT(tag : NBTTagCompound)

    @SideOnly(Side.CLIENT)
    override def getRenderBoundingBox : AxisAlignedBB = {
        if(!resources.isEmpty)
            TileEntity.INFINITE_EXTENT_AABB
        else
            super.getRenderBoundingBox
    }
}
