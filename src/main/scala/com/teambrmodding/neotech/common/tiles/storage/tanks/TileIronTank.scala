package com.teambrmodding.neotech.common.tiles.storage.tanks

import com.teambrmodding.neotech.utils.TimeUtils
import com.teambr.bookshelf.common.tiles.traits.{FluidHandler, UpdatingTile}
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.{EnumSkyBlock, World}
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.capability.CapabilityFluidHandler

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 7/28/2016
  */
class TileIronTank extends UpdatingTile with FluidHandler {

    lazy val TANK = 0

    // Changed in child classes to have larger capacity
    def CAPACITY  = 8000

    /**
      * Used to set up the tanks needed. You can insert any number of tanks
      */
    override def setupTanks(): Unit = {
        tanks += new FluidTank(CAPACITY)
    }

    /**
      * Which tanks can input
      *
      * @return
      */
    override def getInputTanks: Array[Int] = Array(TANK)

    /**
      * Which tanks can output
      *
      * @return
      */
    override def getOutputTanks: Array[Int] = Array(TANK)

    /**
      * Called when something happens to the tank, you should mark the block for update here if a tile
      */
    override def onTankChanged(tank: FluidTank): Unit = {
        worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 6)
        markForUpdate(3)
    }

    override def writeToNBT(tag: NBTTagCompound): NBTTagCompound = {
        super[TileEntity].writeToNBT(tag)
        super[FluidHandler].writeToNBT(tag)
        tag
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].readFromNBT(tag)
        super[FluidHandler].readFromNBT(tag)
    }

    var offset = 0.0F
    var dir = 0.01F

    var lastLightLevel = 0

    override def onClientTick(): Unit = {
        if (tanks(TANK).getFluid != null && !this.isInstanceOf[TileVoidTank]) {
            offset += dir
            if (offset >= 0.3 || offset <= -0.3)
                dir = -dir
        }

        val light = getBrightness
        if (lastLightLevel != getBrightness) {
            lastLightLevel = light
            worldObj.setLightFor(EnumSkyBlock.BLOCK, pos, light)
        }
    }

    override def onServerTick(): Unit = {
        if(TimeUtils.onSecond(10) && tanks(TANK).getFluid != null) {
            worldObj.getTileEntity(pos.offset(EnumFacing.DOWN)) match {
                case tile : TileEntity if tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP) =>
                    val tank = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP)
                    for(tankInfo <- tank.getTankProperties) {
                        if(tankInfo.canFillFluidType(tanks(TANK).getFluid)) {
                            val actualDrain = tank.fill(tanks(TANK).drain(1000, false), false)
                            if(actualDrain > 0) {
                                tank.fill(tanks(TANK).drain(actualDrain, true), true)
                                markForUpdate(3)
                                return
                            }
                        }
                    }
                case _ =>
            }
        }
    }

    def getBrightness: Int = {
        if (tanks(TANK).getFluid != null) {
            return tanks(TANK).getFluid.getFluid.getLuminosity * (tanks(TANK).getFluidAmount / tanks(TANK).getCapacity)
        }
        0
    }

    def getFluidLevelScaled: Float = {
        Math.min(14.99F, (14 * tanks(TANK).getFluidAmount / tanks(TANK).getCapacity.toFloat) + 1.31F + offset)
    }
}
