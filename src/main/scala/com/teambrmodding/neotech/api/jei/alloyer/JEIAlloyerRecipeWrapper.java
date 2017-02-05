package com.teambrmodding.neotech.api.jei.alloyer;

import com.teambr.bookshelf.helper.LogHelper;
import com.teambrmodding.neotech.managers.RecipeManager;
import com.teambrmodding.neotech.registries.AlloyerRecipe;
import com.teambrmodding.neotech.registries.AlloyerRecipeHandler;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraftforge.fluids.FluidStack;
import scala.actors.threadpool.Arrays;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * This file was created for NeoTech
 * <p>
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

    /*******************************************************************************************************************
     * Class Methods                                                                                                   *
     *******************************************************************************************************************/

    public static List<JEIAlloyerRecipeWrapper> buildRecipeList() {
        ArrayList<JEIAlloyerRecipeWrapper> recipes = new ArrayList<>();
        AlloyerRecipeHandler alloyerRecipeHandler = (AlloyerRecipeHandler) RecipeManager.getHandler("Alloyer").get();
        for(AlloyerRecipe recipe : alloyerRecipeHandler.recipes()) {
            FluidStack fluidInputOne = recipe.getFluidFromString(recipe.fluidOne());
            FluidStack fluidInputTwo = recipe.getFluidFromString(recipe.fluidTwo());
            FluidStack fluidOutput   = recipe.getFluidFromString(recipe.fluidOut());
            if(fluidInputOne != null && fluidInputTwo != null && fluidOutput != null)
                recipes.add(new JEIAlloyerRecipeWrapper(fluidInputOne, fluidInputTwo, fluidOutput));
            else
                LogHelper.severe("[Neotech] Alloyer Recipe json is corrupt! Please delete and run again.");
        }

        return recipes;
    }
}
