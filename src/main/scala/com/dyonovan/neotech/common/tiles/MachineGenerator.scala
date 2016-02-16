package com.dyonovan.neotech.common.tiles

import cofh.api.energy.IEnergyReceiver
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/1/2016
  *
  * Base machine class for all generating blocks
  */
abstract class MachineGenerator extends AbstractMachine {

    var burnTime              = 0
    var currentObjectBurnTime = 0
    var didWork               = false

    override def BASE_ENERGY  = 32000

    /**
      * Called to tick generation. This is where you add power to the generator
      */
    def generate() : Unit

    /**
      * Called per tick to manage burn time. You can do nothing here if there is nothing to generate. You should decrease burn time here
      * You should be handling checks if burnTime is 0 in this method, otherwise the tile won't know what to do
      *
      * @return True if able to continue generating
      */
    def manageBurnTime() : Boolean

    /**
      * This method handles how much energy to produce per tick
      *
      * @return How much energy to produce per tick
      */
    def getEnergyProduced: Int

    /*******************************************************************************************************************
      ************************************************ Generator methods ***********************************************
      ******************************************************************************************************************/

    /**
      * Used to actually do the processes needed. For processors this should be cooking items and generators should
      * generate RF. This is called every tick allowed, provided redstone mode requirements are met
      */
    override def doWork(): Unit = {
        didWork = burnTime == 1

        //Transfer
        if (energyStorage.getEnergyStored > 0) {
            for (i <- EnumFacing.values()) {
                worldObj.getTileEntity(pos.offset(i)) match {
                    case tile: IEnergyReceiver =>
                        val want = tile.receiveEnergy(i.getOpposite, energyStorage.getEnergyStored, true)
                        if (want > 0) {
                            val actual = extractEnergy(i, want, simulate = false)
                            tile.receiveEnergy(i.getOpposite, actual, false)
                            didWork = true
                        }
                    case _ =>
                }
            }
        }

        if(manageBurnTime()) {
            generate()
            didWork = true
        } else
            reset()

        if (didWork) {
            worldObj.markBlockForUpdate(pos)
        }
    }

    /**
      * Use this to set all variables back to the default values, usually means the operation failed
      */
    override def reset() = {
        burnTime = 0
        currentObjectBurnTime = 0
    }

    /**
      * Used to check if this tile is active or not
      *
      * @return True if active state
      */
    override def isActive = burnTime > 0

    /**
      * Write the tag
      */
    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super[AbstractMachine].writeToNBT(tag)
        tag.setInteger("BurnTime", burnTime)
        tag.setInteger("CurrentObjectBurnTime", currentObjectBurnTime)
    }

    /**
      * Read the tag
      */
    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[AbstractMachine].readFromNBT(tag)
        burnTime              = tag.getInteger("BurnTime")
        currentObjectBurnTime = tag.getInteger("CurrentObjectBurnTime")
    }

    /**
      * Client side method to get how far along this process is to a scale variable
      *
      * @param scaleVal What scale to move to, usually pixels
      * @return What value on new scale this is complete
      */
    @SideOnly(Side.CLIENT)
    def getBurnProgressScaled(scaleVal: Int): Int =
        ((burnTime * scaleVal) / Math.max(currentObjectBurnTime, 0.001)).toInt

    /*******************************************************************************************************************
      ************************************************ Energy methods **************************************************
      ******************************************************************************************************************/

    /**
      * Return true if you want this to be able to provide energy
      * @return
      */
    def isProvider : Boolean = true

    /**
      * Return true if you want this to be able to receive energy
      * @return
      */
    def isReceiver : Boolean = false

    /*******************************************************************************************************************
      ************************************************ Inventory methods ***********************************************
      ******************************************************************************************************************/

    /**
      * Used to get what slots you can use per face
      *
      * @param side The face to check
      * @return An array of slots to interface with
      */
    override def getSlotsForFace(side: EnumFacing): Array[Int] = {
        side match {
            case EnumFacing.UP => getInputSlots
            case EnumFacing.DOWN => getOutputSlots
            case _ => getInputSlots ++ getOutputSlots
        }
    }
}
