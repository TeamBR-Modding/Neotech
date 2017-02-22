package com.teambrmodding.neotech.registries.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

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
public class CrucibleRecipe extends AbstractRecipe<ItemStack, FluidStack> {
    // Variables
    public String inputItemStack, outputFluidStack;

    /**
     * Crucible Recipe Object
     * @param inputItemStack  The input itemstack, can but null if you provide a oreDict
     *                        Format: MODID:ITEM:DAMAGE:STACK_SIZE ore ORE_DICT_TAG
     * @param outputFluidStack The output FluidStack
     */
    public CrucibleRecipe(String inputItemStack, String outputFluidStack) {
        this.inputItemStack = inputItemStack;
        this.outputFluidStack = outputFluidStack;
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
    public FluidStack getOutput(ItemStack input) {
        if(input == null) // Safety Check
            return null;

        if(isValidInput(input))
            return getFluidStackFromString(outputFluidStack);

        return null;
    }

    /**
     * Is the input valid for an output
     *
     * @param input The input object
     * @return True if there is an output
     */
    @Override
    public boolean isValidInput(ItemStack input) {
        return isItemStackValidForRecipeStack(inputItemStack, input);
    }
}
