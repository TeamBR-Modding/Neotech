package com.dyonovan.neotech.common.tiles

import cofh.api.energy.{EnergyStorage, IEnergyHandler}
import com.dyonovan.neotech.collections.StandardValues
import com.dyonovan.neotech.common.blocks.traits.Upgradeable
import com.teambr.bookshelf.common.container.IInventoryCallback
import com.teambr.bookshelf.common.tiles.traits.{Syncable, Inventory, UpdatingTile}
import net.minecraft.inventory.{IInventory, ISidedInventory}
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
  * @author Dyonovan
  * @since August 11, 2015
  */
abstract class AbstractMachine extends Syncable with Upgradeable with Inventory with ISidedInventory with IEnergyHandler {

    final val cookSpeed = 200
    final val ENERGY_TICK = 20

    val values = new StandardValues
    var energy = new EnergyStorage(10000)

    val BURNTIME_FIELD_ID = 0
    val COOKTIME_FIELD_ID = 1

    var updateClient = false
    def changeEnergy(initial : Int): Unit = {
        if(getUpgradeBoard != null && getUpgradeBoard.getHardDriveCount > 0) {
            energy = new EnergyStorage(10000 * (getUpgradeBoard.getHardDriveCount * 10))
        }
        else {
            energy = new EnergyStorage(10000)
        }
        energy.setEnergyStored(initial)
        updateClient = true
        worldObj.markBlockForUpdate(pos)
    }

    def getSupposedEnergy : Int = {
        if(getUpgradeBoard != null && getUpgradeBoard.getHardDriveCount > 0)
            10000 * (getUpgradeBoard.getHardDriveCount * 10)
        else
            10000
    }

    def spawnActiveParticles(x: Double, y: Double, z: Double)

    /**
      * Get the output of the recipe
      * @param stack The input
      * @return The output
      */
    def recipe(stack: ItemStack): ItemStack

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
    def getRedstoneOutput: Int

    /** *****************************************************************************************************************
      * ***********************************************  Furnace Methods  ***********************************************
      * *****************************************************************************************************************/

    protected def doWork(): Unit = {
        var didWork: Boolean = false

        if(getSupposedEnergy != energy.getMaxEnergyStored)
            changeEnergy(energy.getEnergyStored)

        if(this.values.cookTime > 0) {
            if(getUpgradeBoard != null && getUpgradeBoard.getProcessorCount > 0)
                energy.extractEnergy(ENERGY_TICK * getUpgradeBoard.getProcessorCount + (ENERGY_TICK * 0.4).toInt, false)
            else
                energy.extractEnergy(ENERGY_TICK, false)
            worldObj.markBlockForUpdate(pos)
        }

        if (this.values.burnTime > 0) {
            this.values.burnTime = values.burnTime - 1
            sendValueToClient(BURNTIME_FIELD_ID, this.values.burnTime)
        }
        if (canSmelt(getStackInSlot(0), recipe(getStackInSlot(0)), getStackInSlot(1)) && !values.isPowered) {
            if (this.values.burnTime <= 0) {
                this.values.burnTime = cookSpeed
                this.values.currentItemBurnTime = this.values.burnTime
                cook()
                didWork = true
            }
            else if (isBurning) {
                didWork = cook()
            }
            else {
                this.values.cookTime = 0
                this.values.burnTime = 0
                didWork = true
            }
        }
        else if (this.values.burnTime <= 0) {
            this.values.cookTime = 0
            didWork = true
        }
        if (didWork) {
            worldObj.markBlockForUpdate(pos)
        }
    }

    private def cook(): Boolean = {
        var movement : Int = 1
        if(getUpgradeBoard != null && getUpgradeBoard.getProcessorCount > 0)
            movement = 20 * getUpgradeBoard.getProcessorCount
        this.values.cookTime += movement
        if (this.values.cookTime >= this.values.currentItemBurnTime) {
            this.values.cookTime = 0
            this.smeltItem()
            true
        }
        else {
            false
        }
    }

    def canSmelt(input: ItemStack, result: ItemStack, output: ItemStack): Boolean = {
        if (energy.getEnergyStored >= ENERGY_TICK) {
            if (input == null || result == null)
                return false
            else if (output == null)
                return true
            else if (!output.isItemEqual(result))
                return false
            else {
                val minStackSize: Int = output.stackSize + result.stackSize
                return minStackSize <= getInventoryStackLimit && minStackSize <= result.getMaxStackSize
            }
        }
        false
    }

    def smeltItem(): Unit = {
        var recipeResult = recipe(getStackInSlot(0))
        decrStackSize(0, 1)
        if (getStackInSlot(1) == null) {
            recipeResult = recipeResult.copy
            recipeResult.stackSize = recipeResult.stackSize
            setInventorySlotContents(1, recipeResult)
        } else {
            getStackInSlot(1).stackSize += recipeResult.stackSize
        }

        worldObj.markBlockForUpdate(pos)
    }

    def isBurning: Boolean = {
        values.burnTime > 0
    }

    @SideOnly(Side.CLIENT) def getCookProgressScaled(scaleVal: Int): Int =
        ((this.values.cookTime * scaleVal) / Math.max(cookSpeed, 0.001)).toInt

    @SideOnly(Side.CLIENT) def getBurnTimeRemainingScaled(scaleVal: Int): Int =
        ((this.values.burnTime * scaleVal) / Math.max(this.values.currentItemBurnTime, 0.001)).toInt

    override def markDirty(): Unit = {
        super[Upgradeable].markDirty()
        super[TileEntity].markDirty()
        super[Inventory].markDirty()
    }

    /** ****************************************************************************************************************
      * *************************************************  Tile Methods  ************************************************
      * *****************************************************************************************************************/

    override def onServerTick(): Unit = {
        doWork()
    }

    override def onClientTick() : Unit = {
        if(getSupposedEnergy != energy.getMaxEnergyStored)
            sendValueToServer(3, 0)
    }

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super[Upgradeable].writeToNBT(tag)
        super[TileEntity].writeToNBT(tag)
        super[Inventory].writeToNBT(tag)
        values.writeToNBT(tag)
        energy.writeToNBT(tag)
        if(updateClient && worldObj != null) {
            tag.setBoolean("UpdateEnergy", true)
            updateClient = false
        }
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[Upgradeable].readFromNBT(tag)
        super[TileEntity].readFromNBT(tag)
        super[Inventory].readFromNBT(tag)
        val tempCook = values.cookTime
        values.readFromNBT(tag)
        if(tag.hasKey("UpdateEnergy") && worldObj != null  )
            changeEnergy(tag.getInteger("Energy"))
        energy.readFromNBT(tag)
        if(worldObj != null && (tempCook == 0 && this.values.cookTime > 0) || (tempCook > 0 && this.values.cookTime == 0))
            worldObj.markBlockRangeForRenderUpdate(pos, pos)
    }

    /**
      * Called when the board is removed, reset to default values
      */
    override def resetValues(): Unit = {
        //TODO: reset upgrade things
    }

    /** *****************************************************************************************************************
      * *********************************************** Inventory methods ***********************************************
      * *****************************************************************************************************************/

    override def getSlotsForFace(side: EnumFacing): Array[Int] = {
        Array[Int](0, 1)
    }

    /**
      * Returns true if automation can insert the given item in the given slot from the given side. Args: slot, item,
      * side
      */
    override def canInsertItem(slot: Int, itemStackIn: ItemStack, direction: EnumFacing): Boolean = {
        if (slot == 0 && recipe(itemStackIn) != null) {
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
      * @param slot The slot id
      * @param itemStackIn The stack to check
      * @return True if you can put this there
      */
    override def isItemValidForSlot(slot: Int, itemStackIn: ItemStack): Boolean = slot == 0 && recipe(itemStackIn) != null

    override var inventoryName: String = _

    override def hasCustomName(): Boolean = false

    override def initialSize: Int = 2

    /** *****************************************************************************************************************
      * *********************************************** Energy methods **************************************************
      * *****************************************************************************************************************/
    /**
      * Add energy to an IEnergyReceiver, internal distribution is left entirely to the IEnergyReceiver.
      *
      * @param from
	 * Orientation the energy is received from.
      * @param maxReceive
	 * Maximum amount of energy to receive.
      * @param simulate
	 * If TRUE, the charge will only be simulated.
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

    override def extractEnergy(from: EnumFacing, maxExtract: Int, simulate: Boolean): Int = 0

    /**
      * Returns the amount of energy currently stored.
      */
    override def getEnergyStored(from: EnumFacing): Int = energy.getEnergyStored

    /**
      * Returns the maximum amount of energy that can be stored.
      */
    override def getMaxEnergyStored(from: EnumFacing): Int = energy.getMaxEnergyStored

    /**
      * Returns TRUE if the TileEntity can connect on a given side.
      */
    override def canConnectEnergy(from: EnumFacing): Boolean = true

    override def setVariable(id : Int, value : Double): Unit = {
        id match {
            case BURNTIME_FIELD_ID => this.values.burnTime = value.toInt
            case COOKTIME_FIELD_ID => this.values.cookTime = value.toInt
            case 3 => updateClient = true
        }
    }

    override def getVariable(id : Int) : Double = {
        id match {
            case BURNTIME_FIELD_ID => this.values.burnTime
            case COOKTIME_FIELD_ID => this.values.cookTime
        }
    }
}
