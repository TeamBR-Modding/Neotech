package com.dyonovan.neotech.common.tiles.storage

import cofh.api.energy.{IEnergyContainerItem, IEnergyReceiver}
import com.teambr.bookshelf.api.waila.Waila
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.common.tiles.traits.{EnergyHandler, Inventory, UpdatingTile}
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
class TileRFStorage extends UpdatingTile with EnergyHandler with Inventory with Waila {

    var tier = 0
    lazy final val DRAIN_SLOT = 0
    lazy final val FILL_SLOT  = 1

    def this(t: Int) {
        this()
        tier = t
        initEnergy(t)
    }

    /**
      * Sets the energy based on teir
      * @param t The tier
      */
    def initEnergy(t: Int): Unit = {
        t match {
            case 1 =>
                setMaxEnergyStored(amountEnergy(t))
                setMaxExtract(2000)
                setMaxReceive(2000)
            case 2 =>
                setMaxEnergyStored(amountEnergy(t))
                setMaxExtract(10000)
                setMaxReceive(10000)
            case 3 =>
                setMaxEnergyStored(amountEnergy(t))
                setMaxExtract(100000)
                setMaxReceive(100000)
            case 4 =>
                setMaxEnergyStored(amountEnergy(t))
                setMaxExtract(1000000)
                setMaxReceive(1000000)
                energyStorage.setEnergyStored(energyStorage.getMaxEnergyStored)
            case _ =>
        }
        if (worldObj != null)
            worldObj.markBlockForUpdate(pos)
    }

    /**
      * Gets default energy for tier
      * @param t Tier
      * @return
      */
    def amountEnergy(t: Int): Int = {
        t match {
            case 1 => 25000
            case 2 => 1000000
            case 3 => 10000000
            case 4 => 100000000
            case _ => 0
        }
    }

    def getTier: Int = tier

    override def onServerTick(): Unit = {
        //Transfer
        if (energyStorage != null) {
            if (energyStorage.getEnergyStored > 0) {
                for (i <- EnumFacing.values()) {
                    worldObj.getTileEntity(pos.offset(i)) match {
                        case tile: IEnergyReceiver =>
                            val want = tile.receiveEnergy(i.getOpposite, energyStorage.getEnergyStored, true)
                            if (want > 0) {
                                val actual = extractEnergy(i.getOpposite, want, simulate = false)
                                tile.receiveEnergy(i.getOpposite, actual, false)
                            }
                        case _ =>
                    }
                }

                if(getStackInSlot(DRAIN_SLOT) != null && getStackInSlot(DRAIN_SLOT).getItem.isInstanceOf[IEnergyContainerItem]) {
                    val drainItem = getStackInSlot(DRAIN_SLOT).asInstanceOf[IEnergyContainerItem]
                    receiveEnergy(EnumFacing.UP, drainItem.extractEnergy(getStackInSlot(DRAIN_SLOT), energyStorage.getMaxReceive, false), simulate = false)
                }

                if(getStackInSlot(FILL_SLOT) != null && getStackInSlot(FILL_SLOT).getItem.isInstanceOf[IEnergyContainerItem]) {
                    val drainItem = getStackInSlot(FILL_SLOT).asInstanceOf[IEnergyContainerItem]
                    extractEnergy(EnumFacing.UP, drainItem.receiveEnergy(getStackInSlot(DRAIN_SLOT), energyStorage.getMaxExtract, false), simulate = false)
                }
            }
        }
    }

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].writeToNBT(tag)
        super[UpdatingTile].writeToNBT(tag)
        super[EnergyHandler].writeToNBT(tag)
        super[Inventory].writeToNBT(tag)
        tag.setInteger("Tier", tier)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].readFromNBT(tag)
        super[UpdatingTile].readFromNBT(tag)
        super[Inventory].readFromNBT(tag)
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

    /***
      * The initial size of the inventory
      *
      * @return How big to make the inventory on creation
      */
    override def initialSize: Int = 2

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
    def defaultEnergyStorageSize : Int = 25000

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

    /** *****************************************************************************************************************
      * ************************************************ Waila methods **************************************************
      * *****************************************************************************************************************/

    override def returnWailaBody(tipList: java.util.List[String]): java.util.List[String] = {
        var color = ""
        if (getEnergyStored(null) > 0)
            color = GuiColor.GREEN.toString
        else
            color = GuiColor.RED.toString
        tipList.add(color + getEnergyStored(null) + "/" + getMaxEnergyStored(null) + " RF")
        tipList
    }

}
