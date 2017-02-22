package com.teambrmodding.neotech.api.jei.alloyer;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraftforge.fluids.FluidStack;
import scala.actors.threadpool.Arrays;

import javax.annotation.Nonnull;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/4/2017
 */
public class JEIAlloyerRecipeWrapper extends BlankRecipeWrapper {

    // Variables
    private FluidStack fluidInputOne;
    private FluidStack fluidInputTwo;
    private FluidStack fluidOutput;

    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    /**
     * Recipe Wrapper
     * @param inputOne The first fluid
     * @param inputTwo The second fluid
     * @param output The fluid output
     */
    public JEIAlloyerRecipeWrapper(FluidStack inputOne, FluidStack inputTwo, FluidStack output) {
        fluidInputOne = inputOne;
        fluidInputTwo = inputTwo;
        fluidOutput   = output;
    }

    /*******************************************************************************************************************
     * Helper Methods                                                                                                  *
     *******************************************************************************************************************/

    /**
     * Used to make sure this recipe has been created correctly
     * @return True if recipe can be displayed
     */
    public boolean isValid() {
        return fluidInputOne != null && fluidInputTwo != null && fluidOutput != null;
    }

    /*******************************************************************************************************************
     * BlankRecipeWrapper                                                                                              *
     *******************************************************************************************************************/

    /**
     * Exposes the ingredients to JEI for display
     * @param ingredients The ingredients object
     */
    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        // Add the inputs
        ingredients.setInputs(FluidStack.class, Arrays.asList(new FluidStack[] {fluidInputOne, fluidInputTwo}));

        // Add the output
        ingredients.setOutput(FluidStack.class, fluidOutput);
    }
}
