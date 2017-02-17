package com.teambrmodding.neotech.api.jei.crucible;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/5/2017
 */
public class JEICrucibleRecipeWrapper extends BlankRecipeWrapper {

    // Variables
    private List<ItemStack> inputs;
    private FluidStack      output;

    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    /**
     * Recipe Wrapper
     * @param inputList List of input items
     * @param outputFluid The output fluid
     */
    public JEICrucibleRecipeWrapper(List<ItemStack> inputList, FluidStack outputFluid) {
        inputs = inputList;
        output = outputFluid;
    }

    /*******************************************************************************************************************
     * Helper Methods                                                                                                  *
     *******************************************************************************************************************/

    /**
     * Used to make sure this recipe has been created correctly
     * @return True if recipe can be displayed
     */
    public boolean isValid() {
        return inputs != null && !inputs.isEmpty() && output != null;
    }

    /*******************************************************************************************************************
     * BlankRecipeWrapper                                                                                              *
     *******************************************************************************************************************/

    /**
     * Exposes the ingredients to JEI for display
     * @param ingredients The ingredients object
     */
    @Override
    public void getIngredients(IIngredients ingredients) {
        // Add inputs
        ingredients.setInputs(ItemStack.class, inputs);

        // Add outputs
        ingredients.setOutput(FluidStack.class, output);
    }
}
