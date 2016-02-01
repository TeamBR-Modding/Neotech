package com.dyonovan.neotech.common.tiles

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
        didWork = false

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
        super.writeToNBT(tag)
        tag.setInteger("BurnTime", burnTime)
        tag.setInteger("CurrentObjectBurnTime", currentObjectBurnTime)
    }

    /**
      * Read the tag
      */
    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super.readFromNBT(tag)
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
      * Used to extract energy from this tile. You should return zero if you don't want to be able to extract
      *
      * @param from The direction pulling from
      * @param maxExtract The maximum amount to extract
      * @param simulate True to just simulate, not actually drain
      * @return How much energy was/should be drained
      */
    override def extractEnergy(from: EnumFacing, maxExtract: Int, simulate: Boolean): Int = {
        val actual = energy.extractEnergy(maxExtract, simulate)
        worldObj.markBlockForUpdate(pos)
        actual
    }

    /**
      * Add energy to an IEnergyReceiver, internal distribution is left entirely to the IEnergyReceiver.
      * @param from Orientation the energy is received from.
      * @param maxReceive Maximum amount of energy to receive.
      * @param simulate If TRUE, the charge will only be simulated.
      * @return Amount of energy that was (or would have been, if simulated) received.
      */
    override def receiveEnergy(from: EnumFacing, maxReceive: Int, simulate: Boolean): Int = 0
}
