package com.teambrmodding.neotech.api.jei.alloyer;

import com.teambrmodding.neotech.api.jei.NeotechRecipeCategoryUID;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

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
public class JEIAlloyerRecipeHandler implements IRecipeHandler<JEIAlloyerRecipeWrapper> {

    @Override
    public Class<JEIAlloyerRecipeWrapper> getRecipeClass() {
        return JEIAlloyerRecipeWrapper.class;
    }

    @Override
    public String getRecipeCategoryUid() {
        return NeotechRecipeCategoryUID.ALLOYER();
    }

    @Override
    public String getRecipeCategoryUid(JEIAlloyerRecipeWrapper recipe) {
        return NeotechRecipeCategoryUID.ALLOYER();
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(JEIAlloyerRecipeWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(JEIAlloyerRecipeWrapper recipe) {
        return recipe.isValid();
    }
}
