package com.teambrmodding.neotech.registries.recipes;

import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public class AlloyerRecipe extends AbstractRecipe<Pair<FluidStack, FluidStack>, FluidStack> {
    // Variables
    public String fluidStackOne, fluidStackTwo, fluidStackOutput;

    /**
     * Creates A recipe object
     *
     * All fluids should be format FLUID:AMOUNT
     *
     * @param fluidOne The first fluid
     * @param fluidTwo The second fluid
     * @param fluidOutput The fluid output
     */
    public AlloyerRecipe(String fluidOne, String fluidTwo, String fluidOutput) {
        this.fluidStackOne = fluidOne;
        this.fluidStackTwo = fluidTwo;
        this.fluidStackOutput = fluidOutput;
    }

    /**
     * Used to check if a single fluid is valid for the recipes
     *
     * @param input FluidStack
     * @return Boolean
     */
    public boolean isValidSingleInput(FluidStack input) {
        return input != null &&
                (getFluidStackFromString(fluidStackOne).getFluid().getName().equalsIgnoreCase(input.getFluid().getName()) ||
                        getFluidStackFromString(fluidStackTwo).getFluid().getName().equalsIgnoreCase(input.getFluid().getName())) &&
                (getFluidStackFromString(fluidStackOne).amount <= input.amount || getFluidStackFromString(fluidStackTwo).amount <= input.amount);
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
    public FluidStack getOutput(Pair<FluidStack, FluidStack> input) {
        if(isValidInput(input))
            return getFluidStackFromString(fluidStackOutput);
        return null;
    }

    /**
     * Is the input valid for an output
     *
     * @param input The input object
     * @return True if there is an output
     */
    @Override
    public boolean isValidInput(Pair<FluidStack, FluidStack> input) {
        return !(input.getLeft() == null || input.getRight() == null ||
                input.getLeft().getFluid() == null || input.getRight().getFluid() == null) &&
                ((getFluidStackFromString(fluidStackOne).getFluid().getName().equalsIgnoreCase(input.getLeft().getFluid().getName()) &&
                        getFluidStackFromString(fluidStackTwo).getFluid().getName().equalsIgnoreCase(input.getRight().getFluid().getName()) &&
                        getFluidStackFromString(fluidStackOne).amount <= input.getLeft().amount &&
                        getFluidStackFromString(fluidStackTwo).amount <= input.getRight().amount) ||
                        (getFluidStackFromString(fluidStackTwo).getFluid().getName().equalsIgnoreCase(input.getLeft().getFluid().getName()) &&
                                getFluidStackFromString(fluidStackOne).getFluid().getName().equalsIgnoreCase(input.getRight().getFluid().getName()) &&
                                getFluidStackFromString(fluidStackTwo).amount <= input.getLeft().amount &&
                                getFluidStackFromString(fluidStackOne).amount <= input.getRight().amount));
    }
}
