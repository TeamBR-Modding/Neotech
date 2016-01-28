package com.dyonovan.neotech.pipes.tiles.item

import java.util

import com.dyonovan.neotech.pipes.entities.ItemResourceEntity
import com.dyonovan.neotech.pipes.types.{ExtractionPipe, SimplePipe}
import com.teambr.bookshelf.common.tiles.traits.Inventory
import com.teambr.bookshelf.util.InventoryUtils
import net.minecraft.inventory.{IInventory, ISidedInventory}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{BlockPos, EnumFacing}
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
class ItemExtractionPipe extends ExtractionPipe[ItemStack, ItemResourceEntity] {
    override def canConnect(facing: EnumFacing): Boolean =
        super.canConnect(facing) &&
                (getWorld.getTileEntity(getPos.offset(facing)).isInstanceOf[SimplePipe] ||
                        (getWorld.getTileEntity(pos.offset(facing)).isInstanceOf[IItemHandler] || getWorld.getTileEntity(pos.offset(facing)).isInstanceOf[IInventory]))
    /**
      * This is the speed to extract from. You should be calling this when building your resources to send.
      *
      * This is included as a reminder to the child to have variable speeds
      *
      * @return
      */
    override def getSpeed: Double = {
        if(getUpgradeBoard != null && getUpgradeBoard.getProcessorCount > 0)
            getUpgradeBoard.getProcessorCount * 0.05
        else
            0.05
    }

    /**
      * Used to specify how big a stack to pull. Judge with upgrades here
      *
      * @return
      */
    def getMaxStackExtract : Int = {
        if(getUpgradeBoard != null && getUpgradeBoard.getHardDriveCount > 0) {
            getUpgradeBoard.getHardDriveCount * 8
        } else
            1
    }

    /**
      * Get how many ticks to 'cooldown' between operations.
      *
      * @return 20 = 1 second
      */
    override def getDelay: Int = {
        if(getUpgradeBoard != null && getUpgradeBoard.getProcessorCount > 0)
            20 - getUpgradeBoard.getProcessorCount * 2
        else
            20
    }

    override def doExtraction(): Unit = {
        tryExtractResources()
    }

    override def tryExtractResources(): Unit = {
        val tempInv = new Inventory() {
            override def initialSize: Int = 1
        }

        for(dir <- EnumFacing.values()) {
            if (canConnect(dir)) {
                val otherObject = worldObj.getTileEntity(pos.offset(dir))
                var otherInv: IItemHandler = null

                if (!otherObject.isInstanceOf[IItemHandler]) {
                    otherObject match {
                        case iInventory: IInventory if !iInventory.isInstanceOf[ISidedInventory] => otherInv = new InvWrapper(iInventory)
                        case iSided: ISidedInventory => otherInv = new SidedInvWrapper(iSided, dir.getOpposite)
                        case _ =>
                    }
                } else otherObject match { //If we are a ItemHandler, we want to make sure not to wrap, it can be both IInventory and IItemHandler
                    case itemHandler: IItemHandler => otherInv = itemHandler
                    case _ =>
                }

                otherObject match { //Check for sidedness
                    case tileEntity: TileEntity if tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite) =>
                        otherInv = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite)
                    case _ =>
                }

                if (otherInv != null) {
                    for (x <- 0 until otherInv.getSlots) {
                        if (otherInv.extractItem(x, getMaxStackExtract, true) != null) {
                            if (otherInv.getStackInSlot(x) != null && extractOnMode(new ItemResourceEntity(otherInv.extractItem(x, getMaxStackExtract, true),
                                pos.getX + 0.5, pos.getY + 0.5, pos.getZ + 0.5, getSpeed,
                                pos, pos, worldObj), simulate = true)) {
                                InventoryUtils.moveItemInto(otherInv, x, tempInv, 0, getMaxStackExtract, dir, doMove = true)
                                if (tempInv.getStackInSlot(0) != null) {
                                    nextResource.resource = tempInv.getStackInSlot(0)
                                    extractOnMode(nextResource, simulate = false)
                                    return
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
      * This is called when we fail to send a resource. You should put the resource back where you found it or
      * add it to the world
      */
    override def resourceReturned(resource: ItemResourceEntity): Unit = {
        val tempInventory = new Inventory() {
            override def initialSize: Int = 1
        }

        tempInventory.setInventorySlotContents(0, resource.resource)

        //Try and insert the stack
        for(dir <- EnumFacing.values()) {
            val otherObject = worldObj.getTileEntity(pos.offset(dir))
            var otherInv : IItemHandler = null

            if(!otherObject.isInstanceOf[IItemHandler]) {
                otherObject match {
                    case iInventory: IInventory if !iInventory.isInstanceOf[ISidedInventory] => otherInv = new InvWrapper(iInventory)
                    case iSided: ISidedInventory => otherInv = new SidedInvWrapper(iSided, dir.getOpposite)
                    case _ => return
                }
            } else otherObject match { //If we are a ItemHandler, we want to make sure not to wrap, it can be both IInventory and IItemHandler
                case itemHandler: IItemHandler => otherInv = itemHandler
                case _ => return
            }

            otherObject match { //Check for sidedness
                case tileEntity: TileEntity if tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite) =>
                    otherInv = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite)
                case _ =>
            }

            InventoryUtils.moveItemInto(tempInventory, 0, otherInv, -1, 64, dir, doMove = true)
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
