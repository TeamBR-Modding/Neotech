package com.dyonovan.neotech.pipes.tiles

import java.util
import javax.vecmath.Vector3d

import com.dyonovan.neotech.pipes.network.{ResourceEntity, ItemResourceEntity}
import com.teambr.bookshelf.common.tiles.traits.{Inventory, UpdatingTile}
import com.teambr.bookshelf.util.InventoryUtils
import net.minecraft.inventory.{ISidedInventory, IInventory}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
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
class ItemExtractionPipe extends UpdatingTile with IPipe {
    /**
     * Useful in round robin
     */
    val lastSink : Long = 0

    //Our storage of resources
    var resources : util.ArrayList[ItemResourceEntity] = new util.ArrayList[ItemResourceEntity]()

    override def canConnect(facing: EnumFacing): Boolean =
        getWorld.getTileEntity(getPos.offset(facing)).isInstanceOf[IPipe] || getWorld.getTileEntity(pos.offset(facing)).isInstanceOf[IInventory]

    /**
     * Used to add a resource
     */
    def addResource(resource : ItemResourceEntity) : Unit = {
        resources.add(resource)
    }

    def extractStackOnShortestPath(stack : ItemStack) : Unit = {
        //What we wish to send
        val resource = new ItemResourceEntity(stack.copy(), pos.getX + 0.5, pos.getY + 0.5, pos.getZ + 0.5, 0.05, pos, pos, worldObj)
        val sinks = new util.ArrayList[Long]()
        val distance: util.HashMap[Long, Integer] = new util.HashMap[Long, Integer]
        val parent: util.HashMap[Long, BlockPos] = new util.HashMap[Long, BlockPos]

        distance.put(getPosAsLong, 0)
        parent.put(getPosAsLong, null)

        val queue: util.Queue[BlockPos] = new util.LinkedList[BlockPos]
        queue.add(BlockPos.fromLong(getPosAsLong))

        //Search the graph
        while (!queue.isEmpty) {
            val thisPos: BlockPos = queue.poll
            worldObj.getTileEntity(thisPos) match {
                case thisPipe: IPipe =>
                    for (facing <- EnumFacing.values) {
                        if (thisPipe.canConnect(facing)) {
                            val otherPos: BlockPos = thisPos.offset(facing)
                            if (distance.get(otherPos.toLong) == null) {
                                queue.add(otherPos)
                                distance.put(otherPos.toLong, Integer.MAX_VALUE)
                                parent.put(otherPos.toLong, null)

                                val newDistance: Int = (distance.get(thisPos.toLong) + thisPos.distanceSq(otherPos)).toInt
                                if (newDistance < distance.get(otherPos.toLong)) {
                                    distance.put(otherPos.toLong, newDistance)
                                    parent.put(otherPos.toLong, thisPos)
                                }

                                worldObj.getTileEntity(otherPos) match {
                                    case pipe : IPipe =>
                                        if(pipe.canAcceptResource(resource))
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
        var destination = new BlockPos(pos)
        var shortest = Integer.MAX_VALUE
        for(i <- 0 until sinks.size()) {
            val d = BlockPos.fromLong(sinks.get(i))
            if(distance.get(d.toLong) < shortest) {
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
        if(!resource.pathQueue.isEmpty)
            resources.add(resource)
        else {
            val tempInv = new Inventory() {
                override var inventoryName: String = "TEMPINV"
                override def hasCustomName(): Boolean = false
                override def initialSize: Int = 1
            }

            tempInv.setInventorySlotContents(0, resource.resource)
            for(dir <- EnumFacing.values()) {
                worldObj.getTileEntity(pos.offset(dir)) match {
                    case otherInv : IInventory =>
                        InventoryUtils.moveItemInto(tempInv, 0, otherInv, -1, 64, dir, doMove = true, canStack = true)
                    case _ =>
                }
            }

            if(tempInv.getStackInSlot(0) != null) {
                resource.resource = tempInv.getStackInSlot(0)
                resource.onDropInWorld()
            }
        }
    }

    override def onServerTick() : Unit = {
        //Update our resources
        if(!resources.isEmpty) {
            val iterator = resources.iterator()
            while (iterator.hasNext) {
                val resource = iterator.next()
                resource.setWorld(worldObj)
                if (resource.isDead) {
                    resource.onDropInWorld()
                    iterator.remove()
                }
                else
                    resource.updateEntity()
            }
            worldObj.markBlockForUpdate(pos)
        }

        //Try to pull from inventories
        if(worldObj.rand.nextInt(20) == 10)
            tryExtractItems()
    }

    override def onPipeBroken(): Unit = {
        for(i <- 0 until resources.size())
            resources.get(i).onDropInWorld()
    }

    def tryExtractItems(): Unit = {
        val tempInv = new Inventory() {
            override var inventoryName: String = "TEMPINV"
            override def hasCustomName(): Boolean = false
            override def initialSize: Int = 1
        }

        for(dir <- EnumFacing.values()) {
            worldObj.getTileEntity(pos.offset(dir)) match {
                case sidedInv : ISidedInventory =>
                    for(i <- sidedInv.getSlotsForFace(dir.getOpposite)) {
                        if(tempInv.getStackInSlot(0) == null) {
                            InventoryUtils.moveItemInto(sidedInv, i, tempInv, 0, 64, dir.getOpposite, doMove = true, canStack = true)
                        } else {
                            extractStackOnShortestPath(tempInv.getStackInSlot(0))
                            return
                        }
                    }
                case otherInv : IInventory if !otherInv.isInstanceOf[ISidedInventory] =>
                    for(i <- 0 until otherInv.getSizeInventory) {
                        if (tempInv.getStackInSlot(0) == null) {
                            InventoryUtils.moveItemInto(otherInv, i, tempInv, 0, 64, dir.getOpposite, doMove = true, canStack = true)
                        } else {
                            extractStackOnShortestPath(tempInv.getStackInSlot(0))
                            return
                        }
                    }
                case _ =>
            }
        }
    }

    override def onResourceEnteredPipe(resource: ResourceEntity[_]): Unit = {
        resource match {
            case item : ItemResourceEntity =>
                if(item.destination == pos)
                    tryInsertResource(item)
            case _ =>
        }
    }

    def tryInsertResource(resource : ItemResourceEntity) : Unit = {
        val tempInventory = new Inventory() {
            override var inventoryName: String = "TEMPINV"
            override def hasCustomName(): Boolean = false
            override def initialSize: Int = 1
        }

        tempInventory.setInventorySlotContents(0, resource.resource.copy())

        //Try and insert the stack
        for(dir <- EnumFacing.values()) {
            worldObj.getTileEntity(pos.offset(dir)) match {
                case otherInv : IInventory =>
                    if(InventoryUtils.moveItemInto(tempInventory, 0, otherInv, -1, 64, dir, doMove = true, canStack = true) > 0) {
                        if(tempInventory.getStackInSlot(0) != null && tempInventory.getStackInSlot(0).stackSize <= 0) {
                            resource.resource = null
                            resource.isDead = true
                            tempInventory.setInventorySlotContents(0, null)
                        } else {
                            resource.resource = tempInventory.getStackInSlot(0)
                        }
                    }
                case _ =>
            }
        }

        //If we couldn't fill, move back to source
        if(!resource.isDead) {
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
        resources = new util.ArrayList[ItemResourceEntity]()
        for(i <- 0 until resourceList.tagCount()) {
            val resourceTag = resourceList.getCompoundTagAt(i)
            val item = new ItemResourceEntity()
            item.readFromNBT(resourceTag)
            if(worldObj != null && !worldObj.isRemote) {
                item.setWorld(worldObj)
            }
            resources.add(item)
        }
    }

    @SideOnly(Side.CLIENT)
    override def getRenderBoundingBox : AxisAlignedBB = {
        if(!resources.isEmpty)
            TileEntity.INFINITE_EXTENT_AABB
        else
            super.getRenderBoundingBox
    }
}
