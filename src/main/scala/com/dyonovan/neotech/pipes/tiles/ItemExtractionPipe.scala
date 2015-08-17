package com.dyonovan.neotech.pipes.tiles

import java.util

import com.dyonovan.neotech.pipes.network.ItemResourceEntity
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

    override def getSpeed: Double = 0.05

    override def resourceReturned(resourceEntity: ItemResourceEntity): Unit = {
        val resource = resourceEntity.asInstanceOf[ItemResourceEntity]
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

        if(tempInv.getStackInSlot(0) != null && tempInv.getStackInSlot(0).stackSize <= 0)
            tempInv.setInventorySlotContents(0, null)

        if(tempInv.getStackInSlot(0) != null) {
            resource.resource = tempInv.getStackInSlot(0)
            resource.onDropInWorld()
        }
    }

    override def tryInsertResource(resource : ItemResourceEntity) : Unit = {
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

    override def tryExtractResources: Unit = {
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
                            if (tempInv.getStackInSlot(0) != null) {
                                extractResourceOnShortestPath(new ItemResourceEntity(tempInv.getStackInSlot(0),
                                    pos.getX + 0.5, pos.getY + 0.5, pos.getZ + 0.5, getSpeed,
                                    pos, pos, worldObj))
                            }
                            tempInv.setInventorySlotContents(0, null)
                        }
                    }
                case otherInv : IInventory if !otherInv.isInstanceOf[ISidedInventory] =>
                    for(i <- 0 until otherInv.getSizeInventory) {
                        if (tempInv.getStackInSlot(0) == null) {
                            InventoryUtils.moveItemInto(otherInv, i, tempInv, 0, 64, dir.getOpposite, doMove = true, canStack = true)
                            if (tempInv.getStackInSlot(0) != null) {
                                extractResourceOnShortestPath(new ItemResourceEntity(tempInv.getStackInSlot(0),
                                    pos.getX + 0.5, pos.getY + 0.5, pos.getZ + 0.5, getSpeed,
                                    pos, pos, worldObj))
                            }
                            tempInv.setInventorySlotContents(0, null)
                        }
                    }
                case _ =>
            }
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

    override def doExtraction: Unit = {
        tryExtractResources
    }
}
