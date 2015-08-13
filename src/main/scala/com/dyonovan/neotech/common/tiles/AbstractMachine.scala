package com.dyonovan.neotech.common.tiles

import cofh.api.energy.{EnergyStorage, IEnergyHandler}
import com.dyonovan.neotech.collections.StandardValues
import com.teambr.bookshelf.common.tiles.traits.{Inventory, UpdatingTile}
import net.minecraft.inventory.ISidedInventory
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
abstract class AbstractMachine extends UpdatingTile with Inventory with ISidedInventory with IEnergyHandler {

    final val cookSpeed = 200
    final val ENERGY_SMELT = 200

    val values = new StandardValues
    val energy = new EnergyStorage(10000)

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
        if (this.values.burnTime > 0) {
            this.values.burnTime = values.burnTime - 1
            worldObj.markBlockForUpdate(pos)
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
        this.values.cookTime += 1
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
        if (energy.getEnergyStored >= ENERGY_SMELT) {
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

        energy.extractEnergy(ENERGY_SMELT, false)
    }

    def isBurning: Boolean = {
        values.burnTime > 0
    }

    @SideOnly(Side.CLIENT) def getCookProgressScaled(scaleVal: Int): Int =
        ((this.values.cookTime * scaleVal) / Math.max(cookSpeed, 0.001)).toInt

    @SideOnly(Side.CLIENT) def getBurnTimeRemainingScaled(scaleVal: Int): Int =
        ((this.values.burnTime * scaleVal) / Math.max(this.values.currentItemBurnTime, 0.001)).toInt

    override def markDirty(): Unit = {
        super[TileEntity].markDirty()
        super[Inventory].markDirty()
    }

    /** ****************************************************************************************************************
      * *************************************************  Tile Methods  ************************************************
      * *****************************************************************************************************************/

    override def onServerTick(): Unit = {
        doWork()
    }

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].writeToNBT(tag)
        super[Inventory].writeToNBT(tag)
        values.writeToNBT(tag)
        energy.writeToNBT(tag)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].readFromNBT(tag)
        super[Inventory].readFromNBT(tag)
        values.readFromNBT(tag)
        energy.readFromNBT(tag)
        worldObj.markBlockRangeForRenderUpdate(pos, pos)
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
    override def canInsertItem(index: Int, itemStackIn: ItemStack, direction: EnumFacing): Boolean = {
        if (index == 0) {
            return recipe(itemStackIn) != null
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
     * @param index The slot id
     * @param stack The stack to check
     * @return True if you can put this there
     */
    override def isItemValidForSlot(index: Int, stack: ItemStack): Boolean = index == 0

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
        val actual = energy.receiveEnergy(maxReceive, simulate)
        worldObj.markBlockForUpdate(pos)
        actual
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
}
