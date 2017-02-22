package com.teambrmodding.neotech.registries.recipes;

import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

/**
* This file was created for NeoTech
* <p>
* NeoTech is licensed under the
* Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
* http://creativecommons.org/licenses/by-nc-sa/4.0/
*
* @author Paul Davis - pauljoda
* @since 2/22/2017
*/
public class FluidFuelRecipe extends AbstractRecipe<FluidStack, Pair<Integer, Integer>> {
    public String fluidStackInput;
    public int burnTime, burnRate;

    /**
     * Creates a recipe
     * @param fluidStackInput The input
     * @param burnTime        How many ticks it burns
     * @param burnRate        How many RF/Tick to generate
     */
    public FluidFuelRecipe(String fluidStackInput, int burnTime, int burnRate) {
        this.fluidStackInput = fluidStackInput;
        this.burnTime = burnTime;
        this.burnRate = burnRate;
    }

    /***************************************************************************************************************
     * AbstractRecipe                                                                                              *
     ***************************************************************************************************************/

    /**
     * Used to get the output of this recipe
     *
     * @param input The input object
     * @return The output object, can be null
     */
    @Nullable
    @Override
    public Pair<Integer, Integer> getOutput(FluidStack input) {
        if(input == null || input.getFluid() == null)
            return null;

        if(isValidInput(input))
            return Pair.of(burnTime, burnRate);

        return null;
    }

    /**
     * Is the input valid for an output
     *
     * @param input The input object
     * @return True if there is an output
     */
    @Override
    public boolean isValidInput(FluidStack input) {
        return input != null && input.getFluid() != null &&
                input.getFluid().getName().equalsIgnoreCase(getFluidStackFromString(fluidStackInput).getFluid().getName());
    }
}
