package com.teambrmodding.neotech.common.tiles.storage.tanks

import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.FluidStack

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
class TileCreativeTank extends TileIronTank {

    /**
      * Fills fluid into internal tanks, distribution is left entirely to the IFluidHandler.
      *
      * @param from
      * Orientation the Fluid is pumped in from.
      * @param resource
      * FluidStack representing the Fluid and maximum amount of fluid to be filled.
      * @param doFill
      * If false, fill will only be simulated.
      * @return Amount of resource that was (or would have been, if simulated) filled.
      */
    override def fill(from: EnumFacing, resource: FluidStack, doFill: Boolean): Int = {
        if(tanks(TANK).getFluid == null && resource != null) {
            tanks(TANK).setFluid(new FluidStack(resource.getFluid, 8000))
            markForUpdate()
            8000
        }
        else
            0
    }

    /**
      * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
      *
      * @param from
      * Orientation the Fluid is drained to.
      * @param resource
      * FluidStack representing the Fluid and maximum amount of fluid to be drained.
      * @param doDrain
      * If false, drain will only be simulated.
      * @return FluidStack representing the Fluid and amount that was (or would have been, if
      *         simulated) drained.
      */
    override def drain(from: EnumFacing, resource: FluidStack, doDrain: Boolean): FluidStack =
    drain(from, resource.amount, doDrain = false)

    /**
      * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
      *
      * This method is not Fluid-sensitive.
      *
      * @param from
      * Orientation the fluid is drained to.
      * @param maxDrain
      * Maximum amount of fluid to drain.
      * @param doDrain
      * If false, drain will only be simulated.
      * @return FluidStack representing the Fluid and amount that was (or would have been, if
      *         simulated) drained.
      */
    override def drain(from: EnumFacing, maxDrain: Int, doDrain: Boolean): FluidStack = {
        var fluidAmount :FluidStack = null
        for(x <- getOutputTanks) {
            if(x < tanks.length) {
                fluidAmount = tanks(x).drain(maxDrain, false)
                if (fluidAmount != null) {
                    tanks(x).drain(maxDrain, false)
                    onTankChanged(tanks(x))
                    return fluidAmount
                }
                else if(fluidAmount != null)
                    return fluidAmount
            }
        }
        fluidAmount
    }
}
