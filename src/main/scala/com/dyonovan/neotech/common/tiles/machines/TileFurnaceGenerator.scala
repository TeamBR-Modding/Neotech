package com.dyonovan.neotech.common.tiles.machines

import cofh.api.energy.{IEnergyReceiver, EnergyStorage}
import com.dyonovan.neotech.common.tiles.AbstractMachine
import net.minecraft.inventory.Container
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.{EnumParticleTypes, EnumFacing}

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 13, 2015
 */
class TileFurnaceGenerator extends AbstractMachine {

    final val RF_TICK = 20

    override val energy = new EnergyStorage(100000)

    override def doWork(): Unit = {

        //Generate
        if (this.values.burnTime > 0) {
            this.values.burnTime -= 1
            energy.receiveEnergy(RF_TICK, false)
            worldObj.markBlockForUpdate(pos)
        } else if (energy.getEnergyStored < energy.getMaxEnergyStored && getStackInSlot(0) != null) {
            this.values.burnTime = (TileEntityFurnace.getItemBurnTime(getStackInSlot(0)) / 2.0).toInt
            this.values.currentItemBurnTime = this.values.burnTime
            decrStackSize(0, 1)
        }
        //Transfer
        if (energy.getEnergyStored > 0) {
            for (i <- EnumFacing.values()) {
                worldObj.getTileEntity(pos.offset(i)) match {
                    case tile: IEnergyReceiver =>
                        val want = tile.receiveEnergy(i.getOpposite, energy.getEnergyStored, true)
                        if (want > 0) {
                            val actual = energy.extractEnergy(want, false)
                            tile.receiveEnergy(i.getOpposite, actual, false)
                            worldObj.markBlockForUpdate(pos)
                        }
                    case _ =>
                }
            }
        }
    }
    /**
     * Get the output of the recipe
     * @param stack The input
     * @return The output
     */
    override def recipe(stack: ItemStack): ItemStack = null

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
    override def getRedstoneOutput: Int = Container.calcRedstoneFromInventory(this)

    override def initialSize: Int = 1

    override def canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean = false

    override def getSlotsForFace(side: EnumFacing): Array[Int] = Array[Int](0)

    override def receiveEnergy(from: EnumFacing, maxReceive: Int, simulate: Boolean): Int = 0

    override def extractEnergy(from: EnumFacing, maxExtract: Int, simulate: Boolean): Int = {
        val actual = energy.extractEnergy(maxExtract, simulate)
        worldObj.markBlockForUpdate(pos)
        actual
    }

    override def isItemValidForSlot(index: Int, stack: ItemStack): Boolean = {
        TileEntityFurnace.getItemBurnTime(stack) > 0
    }

    override def canInsertItem(index: Int, itemStackIn: ItemStack, direction: EnumFacing): Boolean = {
        index match {
            case 0 => TileEntityFurnace.getItemBurnTime (itemStackIn) > 0
            case _ => false
        }
    }

    override def spawnActiveParticles(x: Double, y: Double, z: Double): Unit = {
        worldObj.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0, 0, 0)
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0, 0)
    }
}
