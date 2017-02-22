package com.teambrmodding.neotech.api.jei.fluidGenerator;

import com.teambrmodding.neotech.api.jei.NeotechJEIPlugin;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

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
public class JEIFluidGeneratorHandler implements IRecipeHandler<JEIFluidGeneratorRecipeWrapper> {

    @Override
    public Class<JEIFluidGeneratorRecipeWrapper> getRecipeClass() {
        return JEIFluidGeneratorRecipeWrapper.class;
    }

    @Override
    public String getRecipeCategoryUid() {
        return NeotechJEIPlugin.FLUID_GEN_UUID;
    }

    @Override
    public String getRecipeCategoryUid(JEIFluidGeneratorRecipeWrapper recipe) {
        return NeotechJEIPlugin.FLUID_GEN_UUID;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(JEIFluidGeneratorRecipeWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(JEIFluidGeneratorRecipeWrapper recipe) {
        return recipe.isValid();
    }
}
