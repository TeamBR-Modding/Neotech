package com.teambrmodding.neotech.api.jei.crucible;

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
public class JEICrucibleRecipeHandler implements IRecipeHandler<JEICrucibleRecipeWrapper> {

    @Override
    public Class<JEICrucibleRecipeWrapper> getRecipeClass() {
        return JEICrucibleRecipeWrapper.class;
    }

    @Override
    public String getRecipeCategoryUid(JEICrucibleRecipeWrapper recipe) {
        return NeotechJEIPlugin.CRUCIBLE_UUID;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(JEICrucibleRecipeWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(JEICrucibleRecipeWrapper recipe) {
        return recipe.isValid();
    }
}
