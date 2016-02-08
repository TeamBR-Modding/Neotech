package com.dyonovan.neotech.common.tiles.machines.operators

import com.teambr.bookshelf.common.tiles.traits.{FluidHandler, UpdatingTile}
import net.minecraft.block.BlockLiquid
import net.minecraft.init.Blocks
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
  * @author Paul Davis <pauljoda>
  * @since 2/4/2016
  */
class TilePump extends UpdatingTile with FluidHandler {

    val RANGE = 50
    val TANK = 0

    override def setupTanks(): Unit = {
        tanks += new FluidTank(bucketsToMB(10))
    }

    override def onTankChanged(tank: FluidTank): Unit = worldObj.markBlockForUpdate(pos)

    override def onServerTick() : Unit = {
        if(tanks(TANK).getFluidAmount < tanks(TANK).getCapacity) {
            worldObj.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock match {
                case fluid: BlockFluidBase =>
                    val level = fluid.getFilledPercentage(worldObj, pos.offset(EnumFacing.DOWN))
                    if (level == 1.0F) {
                        val fluidStack = new FluidStack(fluid.getFluid, 1000)
                        if (fill(EnumFacing.DOWN, fluidStack, doFill = false) >= 1000) {
                            fill(EnumFacing.DOWN, fluidStack, doFill = true)
                            worldObj.setBlockToAir(pos.offset(EnumFacing.DOWN))
                        }
                    }
                case water: Blocks.water.type if worldObj.getBlockState(pos.offset(EnumFacing.DOWN)).getValue(BlockLiquid.LEVEL).intValue == 0 =>
                    val fluidStack = new FluidStack(FluidRegistry.WATER, 1000)
                    if (fill(EnumFacing.DOWN, fluidStack, doFill = false) >= 1000) {
                        fill(EnumFacing.DOWN, fluidStack, doFill = true)
                        worldObj.setBlockToAir(pos.offset(EnumFacing.DOWN))
                    }
                case lava: Blocks.lava.type if worldObj.getBlockState(pos.offset(EnumFacing.DOWN)).getValue(BlockLiquid.LEVEL).intValue == 0 =>
                    val fluidStack = new FluidStack(FluidRegistry.LAVA, 1000)
                    if (fill(EnumFacing.DOWN, fluidStack, doFill = false) >= 1000) {
                        fill(EnumFacing.DOWN, fluidStack, doFill = true)
                        worldObj.setBlockToAir(pos.offset(EnumFacing.DOWN))
                    }
                case _ =>
            }
        }
        tryOutput()
    }

    def tryOutput() : Unit = {
        for(dir <- EnumFacing.values) {
            worldObj.getTileEntity(pos.offset(dir)) match {
                case otherTank : IFluidHandler =>
                    if(tanks(TANK).getFluid != null && tanks(TANK).getFluid.getFluid != null && otherTank.canFill(dir.getOpposite, tanks(TANK).getFluid.getFluid)
                            && otherTank.fill(dir.getOpposite, drain(dir, 1000, doDrain = false), false) > 0)
                        otherTank.fill(dir.getOpposite, drain(dir, 1000, doDrain = true), true)
                case _ =>
            }
        }

    }

    override def writeToNBT(tag : NBTTagCompound) : Unit = {
        super[TileEntity].writeToNBT(tag)
        super[FluidHandler].writeToNBT(tag)
    }

    override def readFromNBT(tag : NBTTagCompound) : Unit = {
        super[TileEntity].readFromNBT(tag)
        super[FluidHandler].readFromNBT(tag)
    }
}
