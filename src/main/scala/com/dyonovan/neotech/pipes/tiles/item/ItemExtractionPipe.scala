package com.dyonovan.neotech.pipes.tiles.item

import java.util

import com.dyonovan.neotech.pipes.entities.ItemResourceEntity
import com.dyonovan.neotech.pipes.types.{ExtractionPipe, SimplePipe}
import com.teambr.bookshelf.common.tiles.traits.Inventory
import com.teambr.bookshelf.util.InventoryUtils
import net.minecraft.inventory.{IInventory, ISidedInventory}
import net.minecraft.item.ItemStack
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
 * @since August 16, 2015
 */
class ItemExtractionPipe extends ExtractionPipe[ItemStack, ItemResourceEntity] {

    override def canConnect(facing: EnumFacing): Boolean =
        getWorld.getTileEntity(getPos.offset(facing)).isInstanceOf[SimplePipe] || getWorld.getTileEntity(pos.offset(facing)).isInstanceOf[IInventory]

    /**
     * This is the speed to extract from. You should be calling this when building your resources to send.
     *
     * This is included as a reminder to the child to have variable speeds
     * @return
     */
    override def getSpeed: Double = 0.05

    /**
     * Used to specify how big a stack to pull. Judge with upgrades here
     * @return
     */
    def getMaxStackExtract : Int = 1

    /**
     * Get how many ticks to 'cooldown' between operations.
     * @return 20 = 1 second
     */
    override def getDelay: Int = 20

    override def doExtraction(): Unit = {
        tryExtractResources()
    }

    override def tryExtractResources(): Unit = {
        val tempInv = new Inventory() {
            override var inventoryName: String = "TEMPINV"
            override def hasCustomName(): Boolean = false
            override def initialSize: Int = 1
        }

        for(dir <- EnumFacing.values()) {
            worldObj.getTileEntity(pos.offset(dir)) match {
                case sidedInv : ISidedInventory =>
                    for(i <- sidedInv.getSlotsForFace(dir.getOpposite)) {
                        tempInv.setInventorySlotContents(0, null)
                        if (sidedInv.getStackInSlot(i) != null && InventoryUtils.moveItemInto(sidedInv, i, tempInv, 0, getMaxStackExtract, dir.getOpposite, doMove = false, canStack = true) > 0) {
                            if(extractResourceOnShortestPath(new ItemResourceEntity(sidedInv.getStackInSlot(i),
                                pos.getX + 0.5, pos.getY + 0.5, pos.getZ + 0.5, getSpeed,
                                pos, pos, worldObj), simulate = true)) {
                                InventoryUtils.moveItemInto(sidedInv, i, tempInv, 0, getMaxStackExtract, dir.getOpposite, doMove = true, canStack = true)
                                extractResourceOnShortestPath(new ItemResourceEntity(tempInv.getStackInSlot(0),
                                    pos.getX + 0.5, pos.getY + 0.5, pos.getZ + 0.5, getSpeed,
                                    pos, pos, worldObj), simulate = false)
                                return
                            }
                        }
                    }
                case otherInv : IInventory if !otherInv.isInstanceOf[ISidedInventory] =>
                    for(i <- 0 until otherInv.getSizeInventory) {
                        tempInv.setInventorySlotContents(0, null)
                        if (otherInv.getStackInSlot(i) != null && InventoryUtils.moveItemInto(otherInv, i, tempInv, 0, getMaxStackExtract, dir.getOpposite, doMove = false, canStack = true) > 0) {
                            if(extractResourceOnShortestPath(new ItemResourceEntity(otherInv.getStackInSlot(i),
                                pos.getX + 0.5, pos.getY + 0.5, pos.getZ + 0.5, getSpeed,
                                pos, pos, worldObj), simulate = true)) {
                                InventoryUtils.moveItemInto(otherInv, i, tempInv, 0, getMaxStackExtract, dir.getOpposite, doMove = true, canStack = true)
                                extractResourceOnShortestPath(new ItemResourceEntity(tempInv.getStackInSlot(0),
                                    pos.getX + 0.5, pos.getY + 0.5, pos.getZ + 0.5, getSpeed,
                                    pos, pos, worldObj), simulate = false)
                                return
                            }
                        }
                    }
                case _ =>
            }
        }
    }

    /**
     * This is called when we fail to send a resource. You should put the resource back where you found it or
     * add it to the world
     */
    override def resourceReturned(resource: ItemResourceEntity): Unit = {
        val tempInventory = new Inventory() {
            override var inventoryName: String = "TEMPINV"
            override def hasCustomName(): Boolean = false
            override def initialSize: Int = 1
        }

        tempInventory.setInventorySlotContents(0, resource.resource)

        //Try and insert the stack
        for(dir <- EnumFacing.values()) {
            worldObj.getTileEntity(pos.offset(dir)) match {
                case isided : ISidedInventory =>
                    for(i <- isided.getSlotsForFace(dir.getOpposite)) {
                        if(InventoryUtils.moveItemInto(tempInventory, 0, isided, i, 64, dir, doMove = true, canStack = true) > 0) {
                            resource.resource = tempInventory.getStackInSlot(0)
                            if(resource.resource == null || resource.resource.stackSize <= 0) {
                                resource.isDead = true
                                resource.resource = null
                            }
                        }
                    }
                case otherInv : IInventory if !otherInv.isInstanceOf[ISidedInventory] =>
                    for(i <- 0 until otherInv.getSizeInventory) {
                        if(InventoryUtils.moveItemInto(tempInventory, 0, otherInv, i, 64, dir, doMove = true, canStack = true) > 0) {
                            resource.resource = tempInventory.getStackInSlot(0)
                            if(resource.resource == null || resource.resource.stackSize <= 0) {
                                resource.isDead = true
                                resource.resource = null
                            }
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
}
