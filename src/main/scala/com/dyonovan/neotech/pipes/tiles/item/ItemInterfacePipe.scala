package com.dyonovan.neotech.pipes.tiles.item

import java.util

import com.dyonovan.neotech.pipes.entities.{ItemResourceEntity, ResourceEntity}
import com.dyonovan.neotech.pipes.types.{InterfacePipe, SimplePipe}
import com.teambr.bookshelf.client.gui.{GuiColor, GuiTextFormat}
import com.teambr.bookshelf.common.tiles.traits.Inventory
import com.teambr.bookshelf.util.InventoryUtils
import net.minecraft.inventory.{IInventory, ISidedInventory}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{BlockPos, EnumFacing, StatCollector}
import net.minecraftforge.items.wrapper.{InvWrapper, SidedInvWrapper}
import net.minecraftforge.items.{CapabilityItemHandler, IItemHandler}

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
class ItemInterfacePipe extends InterfacePipe[ItemStack, ItemResourceEntity] {

    override def getDescription : String = {
        GuiColor.YELLOW +  "" + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.itemInterfacePipe.name") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.itemInterfacePipe.desc") + "\n\n" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + StatCollector.translateToLocal("neotech.text.upgrades") + ":\n" + GuiTextFormat.RESET +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.processors") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.itemInterfacePipe.processorUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.hardDrives") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.itemInterfacePipe.hardDriveUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.control") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.energyInterfacePipe.controlUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.expansion") + ":\n" +
                GuiColor.WHITE +  StatCollector.translateToLocal("neotech.energyInterfacePipe.expansionUpgrade.desc")
    }

    /*******************************************************************************************************************
      ************************************** Extraction Methods ********************************************************
      ******************************************************************************************************************/

    override def canConnect(facing: EnumFacing): Boolean =
        if(super.canConnect(facing)) {
            getWorld.getTileEntity(pos.offset(facing)) match {
                case tile : TileEntity if tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite) => true
                case pipe: SimplePipe => true
                case _ => false
            }
        }
        else
            super.canConnect(facing)

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
            if (canConnectExtract(dir)) {
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
                                pos.offset(dir), pos.north(), pos.north(), worldObj), simulate = true)) {
                                InventoryUtils.moveItemInto(otherInv, x, tempInv, 0, nextResource.resource.stackSize, dir, doMove = true)
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
    override def returnResource(resource: ItemResourceEntity): Unit = {
        resource.isDead = true
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

    /*******************************************************************************************************************
      *************************************** Insertion Methods ********************************************************
      ******************************************************************************************************************/

    val waitingQueue  = new util.ArrayList[ItemResourceEntity]()
    var tempInventory: Inventory = null

    /**
      * Used to check if this pipe can accept a resource
      *
      * You should not actually change anything, all simulation
      *
      * @param resourceEntity
      * @return
      */
    override def willAcceptResource(resourceEntity: ResourceEntity[_], tilePos : BlockPos): Boolean = {
        if(resourceEntity == null || !resourceEntity.isInstanceOf[ItemResourceEntity] || resourceEntity.resource == null || !super.willAcceptResource(resourceEntity, tilePos))
            return false

        val resource = resourceEntity.asInstanceOf[ItemResourceEntity]

        tempInventory = new Inventory() {
            override def initialSize: Int = 1
        }

        //Try and insert the stack
        for(dir <- EnumFacing.values()) {
            if (worldObj != null && pos.offset(dir).toLong == tilePos.toLong && canConnectSink(dir) && tilePos.toLong != resource.fromTileLocation.toLong
                    && worldObj.getTileEntity(tilePos) != null && !worldObj.getTileEntity(tilePos).isInstanceOf[SimplePipe]) { //Checking simple pipe just to be safe, shouldn't ever be a pipe
                val otherTile = createTileAndSimulate(worldObj.getTileEntity(pos.offset(dir)), dir.getOpposite, pos.offset(dir))
                if (otherTile != null) {
                    tempInventory.setInventorySlotContents(0, resource.resource.copy())

                    val movedStack = InventoryUtils.getStackLeftAfterMove(tempInventory, 0, otherTile, -1, 64, dir, doMove = false)
                    otherTile.invalidate()
                    if (movedStack.isDefined) {
                        return true
                    }
                }
            }
        }
        false
    }


    /**
      * Called when the resource has found its target and is actually sending, change resource size here
      *
      * @param resource
      */
    override def resourceBeingExtracted(resource: ItemResourceEntity): Unit = {
        val tilePos = resource.destinationTile
        for(dir <- EnumFacing.values()) {
            if (worldObj != null && pos.offset(dir).toLong == tilePos.toLong && canConnectSink(dir) && tilePos.toLong != resource.fromTileLocation.toLong
                    && worldObj.getTileEntity(tilePos) != null && !worldObj.getTileEntity(tilePos).isInstanceOf[SimplePipe]) { //Checking simple pipe just to be safe, shouldn't ever be a pipe
            val otherTile = createTileAndSimulate(worldObj.getTileEntity(pos.offset(dir)), dir.getOpposite, pos.offset(dir))
                if (otherTile != null) {
                    tempInventory.setInventorySlotContents(0, resource.resource.copy())

                    val movedStack = InventoryUtils.getStackLeftAfterMove(tempInventory, 0, otherTile, -1, 64, dir, doMove = false)
                    otherTile.invalidate()
                    if (movedStack.isDefined) {
                        waitingQueue.add(resource)
                        if (movedStack.get != null)
                            resource.resource.stackSize = resource.resource.stackSize - movedStack.get.stackSize
                        return
                    }
                }
            }
        }
    }

    def createTileAndSimulate(otherObject : AnyRef, dir : EnumFacing, pos : BlockPos) : TileEntity = {
        var otherTile : TileEntity = null

        if(worldObj.getTileEntity(pos) != null) {
            otherTile = worldObj.getBlockState(pos).getBlock.createTileEntity(worldObj, worldObj.getBlockState(pos))
            val otherTag = new NBTTagCompound
            worldObj.getTileEntity(pos).writeToNBT(otherTag)
            otherTile.readFromNBT(otherTag)
            otherTile.setWorldObj(worldObj)
        }

        if(waitingQueue.isEmpty)
            otherTile
        else {
            if(!otherObject.isInstanceOf[IItemHandler]) {
                otherObject match {
                    case iInventory: IInventory => //Must be an inventory
                    case itemHandler : IItemHandler => //Or an ItemHandler
                    case _ => return null
                }
            }

            val iterator = waitingQueue.iterator() //Remove deads
            while (iterator.hasNext) {
                if (iterator.next().isDead)
                    iterator.remove()
            }

            for(x <- 0 until waitingQueue.size) {
                tempInventory.setStackInSlot(0, waitingQueue.get(x).resource.copy())
                InventoryUtils.moveItemInto(tempInventory, 0, otherTile, -1, 64, dir, doMove = true)
            }
            otherTile
        }
    }

    /**
      * Used to get a list of what tiles are attached that can accept resources. Don't worry about if full or not,
      * just if this pipe interfaces with the tile add it here
      *
      * @return A list of the tiles that are valid sinks
      */
    override def getAttachedSinks: util.List[Long] = {
        val returnList = new util.ArrayList[Long]()
        for(dir <- EnumFacing.values()) {
            if (canConnectSink(dir)) {
                worldObj.getTileEntity(pos.offset(dir)) match {
                    case receiver : IItemHandler  => returnList.add(pos.offset(dir).toLong)
                    case receiver : IInventory  => returnList.add(pos.offset(dir).toLong)
                    case _ =>
                }
            }
        }
        returnList
    }

    override def tryInsertResource(resource : ItemResourceEntity, dir : EnumFacing) : Unit = {
        val tempActualInsert = new Inventory() {
            override def initialSize: Int = 1
        }

        tempActualInsert.setInventorySlotContents(0, resource.resource)

        //Try and insert the stack
        if(canConnectSink(dir) && InventoryUtils.moveItemInto(tempActualInsert, 0, worldObj.getTileEntity(pos.offset(dir)), -1, 64, dir, doMove = true)) {
            resource.resource = tempActualInsert.getStackInSlot(0)
            if(resource.resource == null || resource.resource.stackSize <= 0) {
                resource.isDead = true
                resource.resource = null
                waitingQueue.remove(resource)
            }
        }

        //If we couldn't fill, move back to source
        if(!resource.isDead) {
            resource.isDead = true
        }
    }

    var coolDownInput = 0
    override def onServerTick(): Unit = {
        super.onServerTick()
        coolDownInput -= 1
        if(coolDownInput < 0) {
            coolDownInput = 200
            if (!waitingQueue.isEmpty) {
                val iterator = waitingQueue.iterator()
                while (iterator.hasNext) {
                    if (iterator.next().isDead)
                        iterator.remove()
                }
            }
        }
    }

    override def getPipeTypeID: Int = 3
}
