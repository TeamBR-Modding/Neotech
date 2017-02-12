package com.teambrmodding.neotech.common.tiles.storage

import java.util

import cofh.api.energy.{IEnergyContainerItem, IEnergyReceiver}
import com.teambr.bookshelf.common.tiles.{EnergyHandler, InventoryHandler}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since August 15, 2015
  */
class TileRFStorage extends EnergyHandler with InventoryHandler {

    var tier = 0
    lazy final val DRAIN_SLOT = 1
    lazy final val FILL_SLOT  = 0

    def this(t: Int) {
        this()
        tier = t
        initEnergy(t)
    }

    /**
      * Sets the energy based on tier
      *
      * @param t The tier
      */
    def initEnergy(t: Int): Unit = {
        t match {
            case 1 =>
                energyStorage.setMaxStored(amountEnergy(t))
                energyStorage.setMaxExtract(2000)
                energyStorage.setMaxInsert(2000)
            case 2 =>
                energyStorage.setMaxStored(amountEnergy(t))
                energyStorage.setMaxExtract(10000)
                energyStorage.setMaxInsert(10000)
            case 3 =>
                energyStorage.setMaxStored(amountEnergy(t))
                energyStorage.setMaxExtract(100000)
                energyStorage.setMaxInsert(100000)
            case 4 =>
                energyStorage.setMaxStored(amountEnergy(t))
                energyStorage.setMaxExtract(4096000)
                energyStorage.setMaxInsert(4096000)
                energyStorage.setCurrentStored(energyStorage.getMaxStored)
            case _ =>
        }
        if (world != null)
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 6)
    }

    /**
      * Gets default energy for tier
      *
      * @param t Tier
      * @return
      */
    def amountEnergy(t: Int): Int = {
        t match {
            case 1 => 32000
            case 2 => 512000
            case 3 => 4096000
            case 4 => 4096000
            case _ => 0
        }
    }

    def getTier: Int = tier

    override def onServerTick(): Unit = {
        super[EnergyHandler].onServerTick()
        //Transfer
        if (energyStorage.getEnergyStored > 0) {
            for (i <- EnumFacing.values()) {
                world.getTileEntity(pos.offset(i)) match {
                    case tile: IEnergyReceiver =>
                        val want = tile.receiveEnergy(i.getOpposite, energyStorage.getEnergyStored, true)
                        if (want > 0) {
                            val actual = extractEnergy(i.getOpposite, want, simulate = false)
                            tile.receiveEnergy(i.getOpposite, actual, false)
                        }
                    case _ =>
                }
            }
        }

        if(getStackInSlot(DRAIN_SLOT) != null && getStackInSlot(DRAIN_SLOT).getItem.isInstanceOf[IEnergyContainerItem]) {
            val drainItem = getStackInSlot(DRAIN_SLOT).getItem.asInstanceOf[IEnergyContainerItem]
            val amount = receiveEnergy(EnumFacing.UP, drainItem.extractEnergy(getStackInSlot(DRAIN_SLOT), energyStorage.getMaxInsert, true), true)
            if(amount > 0)
                receiveEnergy(EnumFacing.UP, drainItem.extractEnergy(getStackInSlot(DRAIN_SLOT), amount, false), false)
        }

        if(getStackInSlot(FILL_SLOT) != null && getStackInSlot(FILL_SLOT).getItem.isInstanceOf[IEnergyContainerItem]) {
            val fillItem = getStackInSlot(FILL_SLOT).getItem.asInstanceOf[IEnergyContainerItem]
            extractEnergy(EnumFacing.UP, fillItem.receiveEnergy(getStackInSlot(FILL_SLOT), Math.min(energyStorage.getMaxExtract, if (tier == 4)
                energyStorage.getMaxExtract else energyStorage.getEnergyStored), false), simulate = false)
        }

    }

    override def writeToNBT(tag: NBTTagCompound): NBTTagCompound = {
        super[TileEntity].writeToNBT(tag)
        super[UpdatingTile].writeToNBT(tag)
        super[EnergyHandler].writeToNBT(tag)
        super[Inventory].writeToNBT(tag)
        tag.setInteger("Tier", tier)
        tag
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].readFromNBT(tag)
        super[UpdatingTile].readFromNBT(tag)
        super[Inventory].readFromNBT(tag)
        if(inventoryContents.size() != getInitialSize) {
            inventoryContents = new util.Stack[ItemStack]()
            inventoryContents.setSize(getInitialSize)
        }
        if (tag.hasKey("Tier")) {
            if (tier == 0) initEnergy(tag.getInteger("Tier"))
            tier = tag.getInteger("Tier")

            if (tier != 4) //Why bother if creative
                super[EnergyHandler].readFromNBT(tag)
        }
    }

    /*******************************************************************************************************************
      ***************************************** Inventory Methods ******************************************************
      ******************************************************************************************************************/

    /** *
      * The initial size of the inventory
      *
      * @return How big to make the inventory on creation
      */
    override def getInitialSize : Int = 2

    /**
      * Used to define if an item is valid for a slot
      *
      * @param index The slot id
      * @param stack The stack to check
      * @return True if you can put this there
      */
    override def isItemValidForSlot(index: Int, stack: ItemStack): Boolean = stack.getItem.isInstanceOf[IEnergyContainerItem]

    /** *****************************************************************************************************************
      * *********************************************** Energy methods **************************************************
      * *****************************************************************************************************************/

    /**
      * Used to define the default energy storage for this energy handler
      *
      * @return
      */
    def getDefaultEnergyStorageSize : Int = 25000

    /**
      * Return true if you want this to be able to provide energy
      *
      * @return
      */
    def isProvider : Boolean = true

    /**
      * Return true if you want this to be able to receive energy
      *
      * @return
      */
    def isReceiver : Boolean = true

    /**
      * Used to extract energy from this tile. You should return zero if you don't want to be able to extract
      *
      * @param from The direction pulling from
      * @param maxExtract The maximum amount to extract
      * @param simulate True to just simulate, not actually drain
      * @return How much energy was/should be drained
      */
    override def extractEnergy(from: EnumFacing, maxExtract: Int, simulate: Boolean): Int =
    super.extractEnergy(from, maxExtract, if(tier == 4) true else simulate)

    /**
      * Used to output the redstone single from this structure
      *
      * Use a range from 0 - 16.
      *
      * 0 Usually means that there is nothing in the tile, so take that for lowest level. Like the generator has no energy while
      * 16 is usually the flip side of that. Output 16 when it is totally full and not less
      *
      * @return int range 0 - 16
      */
    def getRedstoneOutput: Int = (energyStorage.getEnergyStored * 16) / energyStorage.getMaxStored

    /** *****************************************************************************************************************
      * ************************************************ Waila methods **************************************************
      * *****************************************************************************************************************/

    /*override def returnWailaBody(tipList: java.util.List[String]): java.util.List[String] = {
        var color = ""
        if (getEnergyStored(null) > 0)
            color = GuiColor.GREEN.toString
        else
            color = GuiColor.RED.toString
        tipList.add(color +
                NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language))
                        .format(getEnergyStored(null)) + " / " +
                NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language))
                        .format(getMaxEnergyStored(null)) + " RF")
        tipList
    }*/
}
