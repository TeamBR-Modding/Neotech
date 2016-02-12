package com.dyonovan.neotech.common.tiles

import cofh.api.energy.IEnergyReceiver
import net.minecraft.item.ItemStack
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
  * @since 1/31/2016
  */
abstract class MachineProcessor extends AbstractMachine with IEnergyReceiver {

    var cookTime = 0
    var didWork  = false

    /**
      * Get the output of the recipe
      *
      * @param stack The input
      * @return The output
      */
    def getOutputForStack(stack: ItemStack): ItemStack

    /**
      * Used to actually cook the item
      */
    def cook() : Unit

    /**
      * Called when the tile has completed the cook process
      */
    def completeCook() : Unit

    /**
      * Used to tell if this tile is able to process
      *
      * @return True if you are able to process
      */
    def canProcess : Boolean

    /**
      * Used to get how long it takes to cook things, you should check for upgrades at this point
      *
      * @return The time it takes in ticks to cook the current item
      */
    def getCookTime : Int

    /**
      * Used to get how much energy to drain per tick, you should check for upgrades at this point
      *
      * @return How much energy to drain per tick
      */
    def getEnergyCostPerTick : Int

    /*******************************************************************************************************************
      ************************************************ Processor methods ***********************************************
      ******************************************************************************************************************/

    /**
      * Used to actually do the processes needed. For processors this should be cooking items and generators should
      * generate RF. This is called every tick allowed, provided redstone mode requirements are met
      */
    var failCoolDown = 0
    override def doWork() = {
        failCoolDown -= 1
        didWork = false

        //Do Operations
        if (failCoolDown < 0 && canProcess) {
            /** We want to check if we are above the value needed before we actually start checking for cooking, this will ensure
              *  we don't run into issues with going one tick over */
            if(cookTime >= getCookTime) {
                completeCook()
                reset()
            }
            if(canProcess) { //For those moments where we completeCook and then are reset, can change this result
                cook()
                energy.extractEnergy(getEnergyCostPerTick, false)
            }
            didWork = true
        } else {
            val update = cookTime > 0
            reset()
            if(update) worldObj.markBlockForUpdate(pos)
        }

        if (didWork) {
            worldObj.markBlockForUpdate(pos)
        }
    }

    /**
      * Use this to set all variables back to the default values, usually means the operation failed
      */
    override def reset() = {
        cookTime = 0
    }

    /**
      * Used to check if this tile is active or not
      *
      * @return True if active state
      */
    override def isActive = cookTime > 0

    /**
      * Write the tag
      */
    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super[AbstractMachine].writeToNBT(tag)
        tag.setInteger("CookTime", cookTime)
    }

    /**
      * Read the tag
      */
    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[AbstractMachine].readFromNBT(tag)
        cookTime = tag.getInteger("CookTime")
    }

    /**
      * Client side method to get how far along this process is to a scale variable
      *
      * @param scaleVal What scale to move to, usually pixels
      * @return What value on new scale this is complete
      */
    @SideOnly(Side.CLIENT)
    def getCookProgressScaled(scaleVal: Int): Int =
        Math.min(((cookTime * scaleVal) / Math.max(getCookTime, 0.001)).toInt, scaleVal)

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

    /**
      * Returns true if automation can insert the given item in the given slot from the given side. Args: slot, item,
      * side
      */
    override def canInsertItem(slot: Int, itemStackIn: ItemStack, direction: EnumFacing): Boolean = {
        if (slot == 0 && getOutputForStack(itemStackIn) != null) {
            if (getStackInSlot(0) == null) return true
            if (getStackInSlot(0).isItemEqual(itemStackIn)) {
                if (getStackInSlot(0).getMaxStackSize >= getStackInSlot(0).stackSize + itemStackIn.stackSize)
                    return true
            }
        }
        false
    }

    /**
      * Returns true if automation can extract the given item in the given slot from the given side. Args: slot, item,
      * side
      */
    override def canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean = index == 1

    /**
      * Used to define if an item is valid for a slot
      *
      * @param slot The slot id
      * @param itemStackIn The stack to check
      * @return True if you can put this there
      */
    override def isItemValidForSlot(slot: Int, itemStackIn: ItemStack): Boolean =
        slot == 0 && getOutputForStack(itemStackIn) != null

    /*******************************************************************************************************************
      ************************************************** Energy methods ************************************************
      ******************************************************************************************************************/

    /**
      * Add energy to an IEnergyReceiver, internal distribution is left entirely to the IEnergyReceiver.
      *
      * @param from Orientation the energy is received from.
      * @param maxReceive Maximum amount of energy to receive.
      * @param simulate If TRUE, the charge will only be simulated.
      * @return Amount of energy that was (or would have been, if simulated) received.
      */
    override def receiveEnergy(from: EnumFacing, maxReceive: Int, simulate: Boolean): Int = {
        if (energy != null) {
            val actual = energy.receiveEnergy(maxReceive, simulate)
            if (worldObj != null)
                worldObj.markBlockForUpdate(pos)
            actual
        } else 0
    }
}
