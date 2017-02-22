package com.teambrmodding.neotech.api.jei.centrifuge;

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
public class JEICentrifugeRecipeHandler implements IRecipeHandler<JEICentrifugeRecipeWrapper> {

    @Override
    public Class<JEICentrifugeRecipeWrapper> getRecipeClass() {
        return JEICentrifugeRecipeWrapper.class;
    }

    @Override
    public String getRecipeCategoryUid() {
        return NeotechJEIPlugin.CENTRIFUGE_UUID;
    }

    @Override
    public String getRecipeCategoryUid(JEICentrifugeRecipeWrapper recipe) {
        return NeotechJEIPlugin.CENTRIFUGE_UUID;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(JEICentrifugeRecipeWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(JEICentrifugeRecipeWrapper recipe) {
        return recipe.isValid();
    }
}
