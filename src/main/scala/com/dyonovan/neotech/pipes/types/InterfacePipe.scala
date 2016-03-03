package com.dyonovan.neotech.pipes.types

import java.util

import com.dyonovan.neotech.pipes.collections.WorldPipes
import net.minecraft.util.{BlockPos, EnumFacing}

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
trait InterfacePipe[T, S <: AnyRef] extends AdvancedPipe {

    /**
      * Used to get the information to display on the tabs in machines. This can be the unlocalized version
      */
    def getDescription : String = getBlockType.getUnlocalizedName + ".description"

    /*******************************************************************************************************************
      ************************************** Extraction Methods ********************************************************
      ******************************************************************************************************************/

    WorldPipes.pipes.add(this)

    /**
      * Useful in round robin
      */
    var lastSink: Long = 0
    var shouldRefreshCache = true

    //Cache for locations
    val sinkPipes = new util.ArrayList[Long]()
    val sinkTileValues = new util.ArrayList[(Long, EnumFacing)]()

    //Used in path finding
    val distance: util.HashMap[Long, Integer] = new util.HashMap[Long, Integer]
    val parent: util.HashMap[Long, BlockPos] = new util.HashMap[Long, BlockPos]
    val queue: util.Queue[BlockPos] = new util.LinkedList[BlockPos]

    //Create a queue
    var foundSource: (T, EnumFacing) = _

    //Set the initial cooldown to max
    var coolDown = getDelay

    /**
      * Get how many ticks to 'cooldown' between operations.
      *
      * @return 20 = 1 second
      */
    def getDelay: Int

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
      * Extracts on the current mode
      *
      * @return
      */
    def findSourceOnMode(resource : S, extractFrom : BlockPos) : Boolean = {
        if(!isResourceValidForFilter(resource))
            return false
        mode match {
            case 0 => extractResourceOnShortestPath(resource, extractFrom)
            case 1 => extractResourceOnLongestPath(resource, extractFrom)
            case 2 => extractOnRoundRobin(resource, extractFrom)
            case _ => extractResourceOnShortestPath(resource, extractFrom)
        }
    }

    override def onServerTick() : Unit = {
        coolDown -= 1
        if(coolDown <= 0) {
            if(getUpgradeBoard != null && getUpgradeBoard.hasControl) {
                if(redstone == -1 && isPowered)
                    return
                if(redstone == 1 && !isPowered)
                    return
            }
            coolDown = getDelay
            doExtraction()
        }
    }


    /**
      * Make sure we don't lose everything when we are broken
      */
    override def onPipeBroken(): Unit = {}

    /**
      * Used to extract the resource on the shortest path possible
      *
      * This will handle finding things that will accept it and will pick the shortest path of all of them
      *
      * You can use this to simulate as well, you are responsible for removing from the tile
      *
      * @param resource The resource to check
      */
    def extractResourceOnShortestPath(resource: S, extractFrom : BlockPos): Boolean = {
        //Sometimes we won't get anything, get lost
        if (resource == null)
            return false

        if(shouldRefreshCache)
            rebuildMap()

        //Find the shortest
        var destination : BlockPos = null
        var fromSide : EnumFacing = null
        var shortest = Integer.MAX_VALUE
        for (i <- 0 until sinkTileValues.size()) {
            if (getWorld != null && getWorld.getTileEntity(BlockPos.fromLong(sinkTileValues.get(i)._1)) != null &&
                    sinkTileValues.get(i)._1 != extractFrom.toLong &&
                    parent.get(sinkTileValues.get(i)._1) != null &&
                    getWorld.getTileEntity(parent.get(sinkTileValues.get(i)._1)) != null &&
                    getWorld.getTileEntity(parent.get(sinkTileValues.get(i)._1)).asInstanceOf[InterfacePipe[T, S]]
                            .willAcceptResource(resource, BlockPos.fromLong(sinkTileValues.get(i)._1), sinkTileValues.get(i)._2)) {
                val d = BlockPos.fromLong(sinkTileValues.get(i)._1)
                if (distance.get(d.toLong) < shortest) {
                    destination = d
                    fromSide = sinkTileValues.get(i)._2
                    shortest = distance.get(d.toLong)
                }
            }
            if(destination != null) {
                foundSource = (getWorld.getTileEntity(destination).asInstanceOf[T], fromSide)
                return true
            }
        }
        false
    }

    /**
      * Used to extract the resource on the longest path possible
      *
      * This will handle finding things that will accept it and will pick the longest path of all of them
      *
      * You can use this to simulate as well, you are responsible for removing from the tile
      *
      * @param resource The resource to check
      */
    def extractResourceOnLongestPath(resource: S, extractFrom : BlockPos): Boolean = {
        //Sometimes we won't get anything, get lost
        if (resource == null)
            return false

        if (shouldRefreshCache)
            rebuildMap()

        //Find the longest
        var destination: BlockPos = null
        var fromSide : EnumFacing = null
        var longest = Integer.MIN_VALUE
        for (i <- 0 until sinkTileValues.size()) {
            if (getWorld != null && getWorld.getTileEntity(BlockPos.fromLong(sinkTileValues.get(i)._1)) != null &&
                    parent.get(sinkTileValues.get(i)._1) != null &&
                    sinkTileValues.get(i)._1 != extractFrom.toLong &&
                    getWorld.getTileEntity(parent.get(sinkTileValues.get(i)._1)) != null &&
                    getWorld.getTileEntity(parent.get(sinkTileValues.get(i)._1)).asInstanceOf[InterfacePipe[T, S]]
                            .willAcceptResource(resource, BlockPos.fromLong(sinkTileValues.get(i)._1), sinkTileValues.get(i)._2)) {
                val d = BlockPos.fromLong(sinkTileValues.get(i)._1)
                if (distance.get(d.toLong) > longest) {
                    destination = d
                    fromSide = sinkTileValues.get(i)._2
                    longest = distance.get(d.toLong)
                }
            }

            if(destination != null) {
                foundSource = (getWorld.getTileEntity(destination).asInstanceOf[T], fromSide)
                return true
            }
        }
        false
    }

    /**
      * Extracts the resource in a round robin path, you are responsible for moving resource
      *
      * @param resource The resource to send
      * @return True if valid source
      */
    def extractOnRoundRobin(resource: S, extractFrom : BlockPos): Boolean = {
        //Sometimes we won't get anything, get lost
        if (resource == null)
            return false

        if(shouldRefreshCache)
            rebuildMap()

        //Find the next source
        var destination: BlockPos = null
        var fromSide : EnumFacing = null
        var pickNext: Boolean = lastSink == 0
        for (i <- 0 until sinkTileValues.size()) {
            if (getWorld != null && getWorld.getTileEntity(BlockPos.fromLong(sinkTileValues.get(i)._1)) != null &&
                    parent.get(sinkTileValues.get(i)._1) != null &&
                    sinkTileValues.get(i)._1 != extractFrom.toLong &&
                    getWorld.getTileEntity(parent.get(sinkTileValues.get(i)._1)) != null &&
                    getWorld.getTileEntity(parent.get(sinkTileValues.get(i)._1)).asInstanceOf[InterfacePipe[T, S]]
                            .willAcceptResource(resource, BlockPos.fromLong(sinkTileValues.get(i)._1), sinkTileValues.get(i)._2)) {
                if (pickNext) {
                    destination = BlockPos.fromLong(sinkTileValues.get(i)._1)
                    fromSide = sinkTileValues.get(i)._2
                    lastSink = sinkTileValues.get(i)._1
                    pickNext = false
                }
                if (sinkTileValues.get(i)._1 == lastSink && destination == null)
                    pickNext = true
            }
        }

        if(destination == null && pickNext) {
            for (i <- 0 until sinkTileValues.size()) {
                if (getWorld != null && getWorld.getTileEntity(BlockPos.fromLong(sinkTileValues.get(i)._1)) != null &&
                        parent.get(sinkTileValues.get(i)._1) != null &&
                        sinkTileValues.get(i)._1 != extractFrom.toLong &&
                        getWorld.getTileEntity(parent.get(sinkTileValues.get(i)._1)) != null &&
                        getWorld.getTileEntity(parent.get(sinkTileValues.get(i)._1)).asInstanceOf[InterfacePipe[T, S]]
                                .willAcceptResource(resource, BlockPos.fromLong(sinkTileValues.get(i)._1), sinkTileValues.get(i)._2)) {
                    if (pickNext) {
                        destination = BlockPos.fromLong(sinkTileValues.get(i)._1)
                        fromSide = sinkTileValues.get(i)._2
                        lastSink = sinkTileValues.get(i)._1
                        pickNext = false
                    }
                    if (sinkTileValues.get(i)._1 == lastSink && destination == null)
                        pickNext = true
                }
            }
        }

        if(destination == null) {
            lastSink = 0
            return false
        }

        foundSource = (getWorld.getTileEntity(destination).asInstanceOf[T], fromSide)
        true
    }

    def rebuildMap() : Unit = {
        sinkPipes.clear()
        sinkTileValues.clear()
        distance.clear()
        parent.clear()
        queue.clear()

        distance.put(getPosAsLong, 0) //We are right here
        parent.put(getPosAsLong, null) //No parent
        sinkPipes.add(getPosAsLong)

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
                                    case pipe: InterfacePipe[T, S] if pipe.frequency == frequency && doTypesMatch(pipe) =>
                                        if(!sinkPipes.contains(pipe.getPosAsLong))
                                            sinkPipes.add(pipe.getPosAsLong)
                                    case _ =>
                                }
                            }
                        }
                    }
                case _ =>
            }
        }
        //Add all sink tiles
        for(x <- 0 until sinkPipes.size()) { //Iterate Sink Pipes
        val sinkPipe = sinkPipes.get(x) //Get sink pipe
            for(j <- 0 until getWorld.getTileEntity(BlockPos.fromLong(sinkPipe)).asInstanceOf[InterfacePipe[T, S]].getAttachedSinks.size()) { //Get attached tiles
            val tileValues = getWorld.getTileEntity(BlockPos.fromLong(sinkPipe)).asInstanceOf[InterfacePipe[T, S]].getAttachedSinks.get(j) //Get tile at side
                distance.put(tileValues._1, distance.get(sinkPipe) + 1) //Add one to distance with pipe as base
                parent.put(tileValues._1, BlockPos.fromLong(sinkPipe)) //Add pipe as parent
                if(!sinkTileValues.contains(tileValues))
                    sinkTileValues.add(tileValues)
            }
        }
        shouldRefreshCache = false
    }

    /**
      * Used to make sure the other pipe matches our type
      *
      * @param pipe The other pipe
      * @return True if matched
      */
    def doTypesMatch(pipe : InterfacePipe[_, _]) : Boolean = pipe.getPipeTypeID == getPipeTypeID

    /**
      * Used to define types, that way only like minded will interact
      *
      * @return
      */
    def getPipeTypeID : Int

    /*******************************************************************************************************************
      *************************************** Insertion Methods ********************************************************
      ******************************************************************************************************************/

    /**
      * Used to get a list of what tiles are attached that can accept resources. Don't worry about if full or not,
      * just if this pipe interfaces with the tile add it here
      *
      * @return A list of the tiles that are valid sinks, with the relevent face
      */
    def getAttachedSinks: util.List[(Long, EnumFacing)]

    /**
      * Used to check if this pipe can accept a resource
      *
      * You should not actually change anything, all simulation
      *
      * @param resource
      * @return
      */
    def willAcceptResource(resource: S, pos : BlockPos, side : EnumFacing) : Boolean = {
        if(getUpgradeBoard != null && getUpgradeBoard.hasControl) {
            if(redstone == -1 && isPowered)
                return false
            if(redstone == 1 && !isPowered)
                return false
        }
        if(!isResourceValidForFilter(resource))
            return false
        true
    }
}
