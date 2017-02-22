package com.teambrmodding.neotech.registries.recipes;

import com.teambrmodding.neotech.collections.SolidifierMode;
import net.minecraft.item.ItemStack;
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
public class SolidifierRecipe extends AbstractRecipe<Pair<SolidifierMode, FluidStack>, ItemStack> {
    public String inputFluidStack, outputItemStack;
    public SolidifierMode requiredMode;

    /**
     * Creates recipe
     * @param inputFluidStack Input Fluid Stack
     * @param outputItemStack Output ItemStack
     */
    public SolidifierRecipe(SolidifierMode mode, String inputFluidStack, String outputItemStack) {
        this.requiredMode = mode;
        this.inputFluidStack = inputFluidStack;
        this.outputItemStack = outputItemStack;
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
    public ItemStack getOutput(Pair<SolidifierMode, FluidStack> input) {
        if(input == null || input.getRight().getFluid() == null)
            return null;

        if(isValidInput(input))
            return getItemStackFromString(outputItemStack);

        return null;
    }

    /**
     * Is the input valid for an output
     *
     * @param input The input object
     * @return True if there is an output
     */
    @Override
    public boolean isValidInput(Pair<SolidifierMode, FluidStack> input) {
        return input.getLeft() == requiredMode &&
                !(input == null || input.getRight().getFluid() == null) &&
                getFluidStackFromString(inputFluidStack).getFluid().getName().equalsIgnoreCase(input.getRight().getFluid().getName()) &&
                input.getRight().amount >= getFluidStackFromString(inputFluidStack).amount;
    }
}
