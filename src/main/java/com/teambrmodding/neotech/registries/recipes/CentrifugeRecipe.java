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
public class CentrifugeRecipe extends AbstractRecipe<FluidStack, Pair<FluidStack, FluidStack>> {
    // Variables
    public String fluidStackInput, fluidStackOutputOne, fluidStackOutputTwo;

    /**
     * Creates a recipe object
     *
     * Paramater format FLUID:AMOUNT
     *
     * @param fluidStackInput The fluid input
     * @param fluidStackOutputOne The first output
     * @param fluidStackOutputTwo The second output
     */
    public CentrifugeRecipe(String fluidStackInput, String fluidStackOutputOne, String fluidStackOutputTwo) {
        this.fluidStackInput = fluidStackInput;
        this.fluidStackOutputOne = fluidStackOutputOne;
        this.fluidStackOutputTwo = fluidStackOutputTwo;
    }

    /***************************************************************************************************************
     * AbstractRecipe                                                                                              *
     ***************************************************************************************************************/

    /**
     * Used to get the output of this recipe
     *
     * @param input The input object
     * @return The output object
     */
    @Nullable
    @Override
    public Pair<FluidStack, FluidStack> getOutput(FluidStack input) {
        if(isValidInput(input))
            return Pair.of(getFluidStackFromString(fluidStackOutputOne), getFluidStackFromString(fluidStackOutputTwo));
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
        return !(input == null || input.getFluid() == null) &&
                getFluidStackFromString(fluidStackInput).getFluid().getName().equalsIgnoreCase(input.getFluid().getName()) &&
                input.amount >= getFluidStackFromString(fluidStackInput).amount;
    }
}
