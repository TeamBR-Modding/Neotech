package com.dyonovan.neotech.common.tiles

import cofh.api.energy.{EnergyStorage, IEnergyHandler}
import com.dyonovan.neotech.collections.StandardValues
import com.dyonovan.neotech.common.blocks.traits.Upgradeable
import com.dyonovan.neotech.common.tiles.machines.AutomaticIO
import com.teambr.bookshelf.common.blocks.properties.PropertyRotation
import com.teambr.bookshelf.common.tiles.traits.{Inventory, InventorySided, RedstoneAware, Syncable}
import com.teambr.bookshelf.util.InventoryUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
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
abstract class AbstractMachine extends Syncable with Upgradeable with InventorySided
        with IEnergyHandler with RedstoneAware with AutomaticIO {

    final val cookSpeed = 200
    final val ENERGY_TICK = 20

    val values = new StandardValues
    var energy = new EnergyStorage(10000)

    var redstone : Int = 0

    val BURNTIME_FIELD_ID = 0
    val COOKTIME_FIELD_ID = 1
    val REDSTONE_FIELD_ID = 4
    val IO_FIELD_ID = 5

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
      *
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
        super[InventorySided].markDirty()
    }

    /** ****************************************************************************************************************
      * *************************************************  Tile Methods  ************************************************
      * *****************************************************************************************************************/

    override def onServerTick(): Unit = {
        if(getUpgradeBoard != null && getUpgradeBoard.hasControl) {
            if(redstone == -1 && isPowered)
                return
            if(redstone == 1 && !isPowered)
                return
        }

        if(worldObj.getWorldTime % 20 == 0 && getUpgradeBoard != null && getUpgradeBoard.hasExpansion) {
            tryInput()
            tryOutput()
        }

        doWork()
    }

    def tryOutput() : Unit = {
        for(dir <- EnumFacing.values) {
            if(canOutputFromSide(dir, worldObj.getBlockState(pos).getValue(PropertyRotation.FOUR_WAY))) {
                for(slot <- getOutputSlots)
                    InventoryUtils.moveItemInto(this, slot, worldObj.getTileEntity(pos.offset(dir)), -1, 64, dir, doMove = true, checkSidedSource = false)
            }
        }
    }

    def tryInput() : Unit = {
        for(dir <- EnumFacing.values) {
            if(canInputFromSide(dir, worldObj.getBlockState(pos).getValue(PropertyRotation.FOUR_WAY))) {
                for(x <- getInputSlots)
                    InventoryUtils.moveItemInto(worldObj.getTileEntity(pos.offset(dir)), -1, this, x, 64, dir.getOpposite, doMove = true, checkSidedTarget = false)
            }
        }
    }

    override def onClientTick() : Unit = {
        if(getSupposedEnergy != energy.getMaxEnergyStored)
            sendValueToServer(3, 0)
    }

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super[Upgradeable].writeToNBT(tag)
        super[TileEntity].writeToNBT(tag)
        super[InventorySided].writeToNBT(tag)
        super[AutomaticIO].writeToNBT(tag)
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
        super[InventorySided].readFromNBT(tag)
        super[AutomaticIO].readFromNBT(tag)
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
        resetIO()
    }

    /** *****************************************************************************************************************
      * *********************************************** Inventory methods ***********************************************
      * *****************************************************************************************************************/

    override def getSlotsForFace(side: EnumFacing): Array[Int] = {
        side match {
            case EnumFacing.UP => Array[Int](0)
            case EnumFacing.DOWN => Array[Int](1)
            case _ => Array[Int](0, 1)
        }
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

    def getOutputSlots : Array[Int] = Array(1)

    def getInputSlots : Array[Int] = Array(0)

    /**
      * Used to define if an item is valid for a slot
      *
      * @param slot The slot id
      * @param itemStackIn The stack to check
      * @return True if you can put this there
      */
    override def isItemValidForSlot(slot: Int, itemStackIn: ItemStack): Boolean = slot == 0 && recipe(itemStackIn) != null

    override def initialSize: Int = 2

    override def hasCapability(capability: Capability[_], facing : EnumFacing) = true

    override def getCapabilityFromTile[T](capability: Capability[T], facing: EnumFacing) : T = super[TileEntity].getCapability[T](capability, facing)

    override def getCapability[T](capability: Capability[T], facing: EnumFacing) : T =
        super[InventorySided].getCapability[T](capability, facing)


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
            case REDSTONE_FIELD_ID => redstone = value.toInt
            case IO_FIELD_ID => toggleMode(EnumFacing.getFront(value.toInt))
            case 3 => updateClient = true
        }
    }

    override def getVariable(id : Int) : Double = {
        id match {
            case BURNTIME_FIELD_ID => this.values.burnTime
            case COOKTIME_FIELD_ID => this.values.cookTime
        }
    }

    def moveRedstoneMode(mod : Int) : Unit = {
        redstone += mod
        if(redstone < -1)
            redstone = 1
        else if(redstone > 1)
            redstone = -1
    }

    def getRedstoneModeName : String = {
        redstone match {
            case -1 => "Low"
            case 0 => "Disabled"
            case 1 => "High"
            case _ => "Error"
        }
    }

    def setRedstoneMode(newMode : Int) : Unit = {
        this.redstone = newMode
    }
}
