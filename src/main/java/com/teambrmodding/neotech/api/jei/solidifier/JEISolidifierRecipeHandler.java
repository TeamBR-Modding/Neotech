package com.teambrmodding.neotech.api.jei.solidifier;

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
public class JEISolidifierRecipeHandler implements IRecipeHandler<JEISolidifierRecipeWrapper> {

    @Override
    public Class<JEISolidifierRecipeWrapper> getRecipeClass() {
        return JEISolidifierRecipeWrapper.class;
    }

    @Override
    public String getRecipeCategoryUid() {
        return NeotechJEIPlugin.SOLIDIFIER_UUID;
    }

    @Override
    public String getRecipeCategoryUid(JEISolidifierRecipeWrapper recipe) {
        return  NeotechJEIPlugin.SOLIDIFIER_UUID;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(JEISolidifierRecipeWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(JEISolidifierRecipeWrapper recipe) {
        return recipe.isValid();
    }
}
