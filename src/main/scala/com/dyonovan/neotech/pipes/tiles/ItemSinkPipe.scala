package com.dyonovan.neotech.pipes.tiles

import com.dyonovan.neotech.pipes.network.{ItemResourceEntity, ResourceEntity}
import com.teambr.bookshelf.common.tiles.traits.Inventory
import com.teambr.bookshelf.util.InventoryUtils
import net.minecraft.inventory.{ISidedInventory, IInventory}
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
class ItemSinkPipe extends IPipe {
    override def canConnect(facing: EnumFacing): Boolean =
        getWorld.getTileEntity(getPos.offset(facing)).isInstanceOf[IPipe] || getWorld.getTileEntity(pos.offset(facing)).isInstanceOf[IInventory]

    override def canAcceptResource(resource: ResourceEntity[_]): Boolean = {
        resource match {
            case item : ItemResourceEntity =>
                val tempInventory = new Inventory() {
                    override var inventoryName: String = "TEMPINV"
                    override def hasCustomName(): Boolean = false
                    override def initialSize: Int = 1
                }

                tempInventory.setInventorySlotContents(0, item.resource.copy())
                //Try and insert the stack
                for(dir <- EnumFacing.values()) {
                    worldObj.getTileEntity(pos.offset(dir)) match {
                        case otherInv : IInventory =>
                            if(InventoryUtils.moveItemInto(tempInventory, 0, otherInv, -1, 64, dir, doMove = false, canStack = true) > 0) {
                                return true
                            }
                        case _ =>
                    }
                }
                false
            case _ => false
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

        tempInventory.setInventorySlotContents(0, resource.resource)

        //Try and insert the stack
        for(dir <- EnumFacing.values()) {
            worldObj.getTileEntity(pos.offset(dir)) match {
                case isided : ISidedInventory =>
                    for(i <- isided.getSlotsForFace(dir.getOpposite)) {
                        if(InventoryUtils.moveItemInto(tempInventory, 0, isided, i, 64, dir, doMove = true, canStack = true) > 0) {
                            resource.isDead = true
                            resource.resource = null
                            resource.resource = tempInventory.getStackInSlot(0)
                        }
                    }
                case otherInv : IInventory if !otherInv.isInstanceOf[ISidedInventory] =>
                    for(i <- 0 until otherInv.getSizeInventory) {
                         if(InventoryUtils.moveItemInto(tempInventory, 0, otherInv, i, 64, dir, doMove = true, canStack = true) > 0) {
                             resource.isDead = true
                             resource.resource = null
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
}
