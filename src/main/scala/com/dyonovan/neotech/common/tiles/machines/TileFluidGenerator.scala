package com.dyonovan.neotech.common.tiles.machines

import cofh.api.energy.{IEnergyReceiver, EnergyStorage}
import com.dyonovan.neotech.common.tiles.AbstractMachine
import com.dyonovan.neotech.registries.FluidFuelValues
import com.teambr.bookshelf.common.blocks.properties.PropertyRotation
import com.teambr.bookshelf.util.InventoryUtils
import net.minecraft.inventory.{IInventory, Container}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids._

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since August 21, 2015
  */
class TileFluidGenerator extends AbstractMachine with IFluidHandler {

    final val RF_TICK = 80
    energy = new EnergyStorage(100000)
    val tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 10)

    override def doWork(): Unit = {

        //Generate
        if (this.values.burnTime > 0) {
            this.values.burnTime -= 1
            energy.receiveEnergy(RF_TICK, false)
            worldObj.markBlockForUpdate(pos)
        } else if (energy.getEnergyStored < energy.getMaxEnergyStored && tank.getFluid != null && tank.getFluidAmount >= 100) {
            this.values.burnTime = (FluidFuelValues.getFluidFuelValue(tank.getFluid.getFluid.getName) * .1).toInt
            this.values.currentItemBurnTime = this.values.burnTime
            tank.drain(100, true)
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


    override def spawnActiveParticles(x: Double, y: Double, z: Double): Unit = {}

    override def tryInput() : Unit = {
        for(dir <- EnumFacing.values) {
            if(canInputFromSide(dir, worldObj.getBlockState(pos).getValue(PropertyRotation.FOUR_WAY))) {
                worldObj.getTileEntity(pos.offset(dir)) match {
                    case otherTank : IFluidHandler =>
                        if(otherTank.getTankInfo(dir.getOpposite)(0).fluid != null && canFill(dir, otherTank.getTankInfo(dir.getOpposite)(0).fluid.getFluid))
                            fill(dir, otherTank.drain(dir.getOpposite, 1000, true), doFill = true)
                    case _ =>
                }
            }
        }
    }

    /**
      * Get the output of the recipe
      *
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
    override def getRedstoneOutput: Int = InventoryUtils.calcRedstoneFromInventory(this)

    override def drain(from: EnumFacing, resource: FluidStack, doDrain: Boolean): FluidStack = drain(from, resource, doDrain)

    override def drain(from: EnumFacing, maxDrain: Int, doDrain: Boolean): FluidStack = {
        val fluidAmount = tank.drain(maxDrain, false)
        if (fluidAmount != null && doDrain)
            tank.drain(maxDrain, true)
        worldObj.markBlockForUpdate(pos)

        fluidAmount
    }

    override def canFill(from: EnumFacing, fluid: Fluid): Boolean = {
        (tank.getFluid == null || tank.getFluid.getFluid == fluid) && FluidFuelValues.isFluidFuel(fluid.getName)
    }


    override def canDrain(from: EnumFacing, fluid: Fluid): Boolean = false

    override def fill(from: EnumFacing, resource: FluidStack, doFill: Boolean): Int = {
        if (canFill(from, resource.getFluid)) {
            if (tank.fill(resource, false) > 0) {
                val actual = tank.fill(resource, doFill)
                worldObj.markBlockForUpdate(pos)
                return actual
            }
        }
        0
    }

    override def getTankInfo(from: EnumFacing): Array[FluidTankInfo] = Array(tank.getInfo)

    override def receiveEnergy(from: EnumFacing, maxReceive: Int, simulate: Boolean): Int = 0

    override def extractEnergy(from: EnumFacing, maxExtract: Int, simulate: Boolean): Int = {
        val actual = energy.extractEnergy(maxExtract, simulate)
        worldObj.markBlockForUpdate(pos)
        actual
    }

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super.writeToNBT(tag)
        tank.writeToNBT(tag)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super.readFromNBT(tag)
        tank.readFromNBT(tag)
    }
}
