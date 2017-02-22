package com.teambrmodding.neotech.common.tiles.storage.tanks;

import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/11/2017
 */
public class TileCreativeTank extends TileBasicTank {

    /**
     * Fills fluid into internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param resource
     * FluidStack representing the Fluid and maximum amount of fluid to be filled.
     * @param doFill
     * If false, fill will only be simulated.
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if(tanks[TANK].getFluid() == null && resource != null) {
            tanks[TANK].setFluid(new FluidStack(resource.getFluid(), 8000));
            markForUpdate(3);
            return 8000;
        }
        return 0;
    }

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param resource
     * FluidStack representing the Fluid and maximum amount of fluid to be drained.
     * @param doDrain
     * If false, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     *         simulated) drained.
     */
    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return drain(resource.amount, false);
    }

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * This method is not Fluid-sensitive.
     *
     * @param maxDrain
     * Maximum amount of fluid to drain.
     * @param doDrain
     * If false, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     *         simulated) drained.
     */
    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return tanks[TANK].drain(maxDrain, false);
    }
}
