package com.teambrmodding.neotech.api.jei.centrifuge;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraftforge.fluids.FluidStack;
import scala.actors.threadpool.Arrays;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/5/2017
 */
public class JEICentrifugeRecipeWrapper extends BlankRecipeWrapper {

    // Variables
    private FluidStack fluidInput;
    private FluidStack fluidOutputOne;
    private FluidStack fluidOutputTwo;

    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    /**
     * Recipe Wrapper
     * @param fluidIn The input fluid
     * @param fluidOutOne The first output
     * @param fluidOutTwo The second output
     */
    public JEICentrifugeRecipeWrapper(FluidStack fluidIn, FluidStack fluidOutOne, FluidStack fluidOutTwo) {
        fluidInput     = fluidIn;
        fluidOutputOne = fluidOutOne;
        fluidOutputTwo = fluidOutTwo;
    }

    /*******************************************************************************************************************
     * Helper Methods                                                                                                  *
     ******************************************************************************************************************/

    /**
     * Used to make sure this recipe has been created correctly
     * @return True if recipe can be displayed
     */
    public boolean isValid() {
        return fluidInput != null && fluidOutputOne != null && fluidOutputTwo != null;
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
        // Add input
        ingredients.setInput(FluidStack.class, fluidInput);

        // Add outputs
        ingredients.setOutputs(FluidStack.class, Arrays.asList(new FluidStack[] {fluidOutputOne, fluidOutputTwo}));
    }
}
