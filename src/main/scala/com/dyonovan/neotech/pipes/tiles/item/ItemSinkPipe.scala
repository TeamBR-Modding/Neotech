package com.dyonovan.neotech.pipes.tiles.item

import java.util
import com.dyonovan.neotech.pipes.entities.{ResourceEntity, ItemResourceEntity}
import com.dyonovan.neotech.pipes.types.{ExtractionPipe, SimplePipe, SinkPipe}
import com.teambr.bookshelf.common.tiles.traits.{InventorySided, UpdatingTile, Syncable, Inventory}
import com.teambr.bookshelf.util.InventoryUtils
import net.minecraft.inventory.{IInventory, ISidedInventory}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.IFluidHandler
import net.minecraftforge.items.{CapabilityItemHandler, IItemHandler}
import net.minecraftforge.items.wrapper.{SidedInvWrapper, InvWrapper}

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
class ItemSinkPipe extends SinkPipe[ItemStack, ItemResourceEntity] with UpdatingTile {
    val waitingQueue  = new util.ArrayList[ItemResourceEntity]()
    var tempInventory: Inventory = null
    var tempInventoryTest: Inventory = null

    override def canConnect(facing: EnumFacing): Boolean =
        if(super.canConnect(facing))
            getWorld.getTileEntity(pos.offset(facing)) match {
                case inventory : IInventory => true
                case itemHandler : IItemHandler => true
                case source: ExtractionPipe[_, _] => source.connections.get(facing.getOpposite.ordinal())
                case sink: SinkPipe[_, _] => sink.connections.get(facing.getOpposite.ordinal())
                case pipe : SimplePipe => true
                case _ => false
            }
        else
            super.canConnect(facing)

    /**
      * Used to check if this pipe can accept a resource
      *
      * You should not actually change anything, all simulation
      *
      * @param resourceEntity
      * @return
      */
    override def willAcceptResource(resourceEntity: ResourceEntity[_]): Boolean = {
        if(resourceEntity == null || !resourceEntity.isInstanceOf[ItemResourceEntity] || resourceEntity.resource == null || !super.willAcceptResource(resourceEntity))
            return false

        val resource = resourceEntity.asInstanceOf[ItemResourceEntity]

        tempInventory = new Inventory() {
            override def initialSize: Int = 1
        }

        tempInventory.setInventorySlotContents(0, resource.resource.copy())

        //Try and insert the stack
        for(dir <- EnumFacing.values()) {
            if (canConnect(dir)) {
                if (InventoryUtils.moveItemInto(tempInventory, 0, test(worldObj.getTileEntity(pos.offset(dir)), dir.getOpposite), -1, 64, dir, doMove = false)) {
                    waitingQueue.add(resource)
                    return true
                }
            }
        }

        false
    }

    def test(otherObject : AnyRef, dir : EnumFacing) : AnyRef = {
        if(waitingQueue.isEmpty)
            otherObject
        else {
            var otherInv : IItemHandler = null

            if(!otherObject.isInstanceOf[IItemHandler]) {
                otherObject match {
                    case iInventory: IInventory if !iInventory.isInstanceOf[ISidedInventory] => otherInv = new InvWrapper(iInventory)
                    case iSided: ISidedInventory => otherInv = new SidedInvWrapper(iSided, dir)
                    case _ => return null
                }
            } else otherObject match { //If we are a ItemHandler, we want to make sure not to wrap, it can be both IInventory and IItemHandler
                case itemHandler: IItemHandler => otherInv = itemHandler
                case _ => return null
            }

            otherObject match { //Check for sidedness
                case tileEntity: TileEntity if tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir) =>
                    otherInv = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir)
                case _ =>
            }
            val iterator = waitingQueue.iterator() //Remove deads
            while (iterator.hasNext) {
                if (iterator.next().isDead)
                    iterator.remove()
            }

            otherInv match {
                case sided: InventorySided =>
                    tempInventoryTest = new InventorySided() {
                        override def initialSize: Int = otherInv.getSlots
                        override def getSlotsForFace(side: EnumFacing): Array[Int] = sided.getSlotsForFace(side)
                        override def getCapabilityFromTile[T](capability: Capability[T], facing: EnumFacing): T = sided.getCapability(capability, facing)
                        override def canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean = sided.canExtractItem(index, stack, direction)
                        override def canInsertItem(slot: Int, itemStackIn: ItemStack, direction: EnumFacing): Boolean = sided.canInsertItem(slot, itemStackIn, direction)
                    }
                case _ =>
                    tempInventoryTest = new Inventory() {
                        override def initialSize: Int = otherInv.getSlots
                    }
            }

            tempInventoryTest.copyFrom(otherInv)

            for(x <- 0 until waitingQueue.size) {
                val tempInventoryUs = new Inventory() {
                    override def initialSize: Int = 1
                }

                val resource = waitingQueue.get(x)

                if(resource == null || resource.resource == null)
                    return tempInventoryTest

                tempInventoryUs.setInventorySlotContents(0, resource.resource.copy())
                InventoryUtils.moveItemInto(tempInventoryUs, 0, tempInventoryTest, -1, 64, dir.getOpposite, doMove = true)
            }
            tempInventoryTest
        }
    }

    override def tryInsertResource(resource : ItemResourceEntity) : Unit = {
        val tempActualInsert = new Inventory() {
            override def initialSize: Int = 1
        }

        tempActualInsert.setInventorySlotContents(0, resource.resource)

        //Try and insert the stack
        for(dir <- EnumFacing.values()) {
            if(InventoryUtils.moveItemInto(tempActualInsert, 0, worldObj.getTileEntity(pos.offset(dir)), -1, 64, dir, doMove = true)) {
                resource.resource = tempActualInsert.getStackInSlot(0)
                if(resource.resource == null || resource.resource.stackSize <= 0) {
                    resource.isDead = true
                    resource.resource = null
                    waitingQueue.remove(resource)
                }
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

    var coolDown = 0
    override def onServerTick: Unit = {
        coolDown -= 1
        if(coolDown < 0) {
            coolDown = 200
            if (!waitingQueue.isEmpty) {
                val iterator = waitingQueue.iterator()
                while (iterator.hasNext) {
                    if (iterator.next().isDead)
                        iterator.remove()
                }
            }
        }
    }

    override def writeToNBT(tag : NBTTagCompound) = {
        super.writeToNBT(tag)
        super[TileEntity].writeToNBT(tag)
    }

    override def readFromNBT(tag : NBTTagCompound) = {
        super.readFromNBT(tag)
        super[TileEntity].readFromNBT(tag)
    }
}
