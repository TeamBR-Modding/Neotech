package com.dyonovan.neotech.pipes.tiles.item

import java.util

import com.dyonovan.neotech.pipes.types.{AdvancedPipe, InterfacePipe, SimplePipe}
import com.teambr.bookshelf.client.gui.{GuiColor, GuiTextFormat}
import com.teambr.bookshelf.common.tiles.traits.Inventory
import com.teambr.bookshelf.util.InventoryUtils
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{BlockPos, EnumFacing, StatCollector}
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
class ItemInterfacePipe extends InterfacePipe[IItemHandler, ItemStack] {

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
                case advanced: AdvancedPipe => !advanced.isDisabled(facing.getOpposite) && !advanced.hasIntersect(facing.getOpposite)
                case pipe: SimplePipe => !pipe.hasIntersect(facing.getOpposite)
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

    override def doExtraction(): Unit =
        tryExtractResources()

    override def tryExtractResources(): Unit = {
        for(dir <- EnumFacing.values()) {
            if (canConnectExtract(dir)) {
                val fromObject = worldObj.getTileEntity(pos.offset(dir))
                if (fromObject != null) {
                    val fromInventory =
                        if (fromObject.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite))
                            fromObject.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite)
                        else return
                    for (x <- 0 until fromInventory.getSlots) {
                        if (fromInventory.extractItem(x, getMaxStackExtract, true) != null) {
                            if (fromInventory.getStackInSlot(x) != null &&
                                    findSourceOnMode(fromInventory.getStackInSlot(x).copy(), pos.offset(dir))) {
                                if (foundSource != null) {
                                    InventoryUtils.moveItemInto(fromInventory, x, foundSource._1, -1,
                                        getMaxStackExtract, foundSource._2.getOpposite, doMove = true)
                                    foundSource = null
                                    worldObj.markBlockForUpdate(pos)
                                    return
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /*******************************************************************************************************************
      *************************************** Insertion Methods ********************************************************
      ******************************************************************************************************************/

    var tempInventory: Inventory = null

    /**
      * Used to check if this pipe can accept a resource
      *
      * You should not actually change anything, all simulation
      *
      * @return
      */
    override def willAcceptResource(checkingResource: ItemStack, tilePos : BlockPos, facing : EnumFacing): Boolean = {
        if(checkingResource == null || !checkingResource.isInstanceOf[ItemStack] || !super.willAcceptResource(checkingResource, tilePos, facing))
            return false

        if(tempInventory == null) {
            tempInventory = new Inventory() {
                override def initialSize: Int = 1
            }
        }

        //Try and insert the stack
        if (worldObj != null && worldObj.getTileEntity(tilePos) != null &&
                !worldObj.getTileEntity(tilePos).isInstanceOf[SimplePipe]) { //Checking simple pipe just to be safe, shouldn't ever be a pipe
        val otherTile = worldObj.getTileEntity(tilePos)
            if (otherTile != null) {
                tempInventory.setInventorySlotContents(0, checkingResource.copy())
                val movedStack = InventoryUtils.getStackLeftAfterMove(tempInventory, 0, otherTile, -1, 64, facing.getOpposite, doMove = false)
                tempInventory.clear()
                if (movedStack.isDefined) {
                    return true
                }
            }
        }
        false
    }

    /**
      * Used to get a list of what tiles are attached that can accept resources. Don't worry about if full or not,
      * just if this pipe interfaces with the tile add it here
      *
      * @return A list of the tiles that are valid sinks
      */
    override def getAttachedSinks: util.List[(Long, EnumFacing)] = {
        val returnList = new util.ArrayList[(Long, EnumFacing)]()
        for(dir <- EnumFacing.values()) {
            if (canConnectSink(dir)) {
                worldObj.getTileEntity(pos.offset(dir)) match {
                    case receiver : IItemHandler  => returnList.add((pos.offset(dir).toLong, dir.getOpposite))
                    case receiver : IInventory  => returnList.add((pos.offset(dir).toLong, dir.getOpposite))
                    case _ =>
                }
            }
        }
        returnList
    }

    override def getPipeTypeID: Int = 3
}
