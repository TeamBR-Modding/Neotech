package com.teambrmodding.neotech.api.jei.alloyer

import java.util

import com.teambrmodding.neotech.managers.RecipeManager
import com.teambrmodding.neotech.registries.AlloyerRecipeHandler
import com.teambr.bookshelf.helper.LogHelper

import scala.collection.JavaConversions._

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/22/2016
  */
object JEIAlloyerRecipeMaker {

    def getRecipes: util.List[JEIAlloyerRecipe] = {
        val recipes = new util.ArrayList[JEIAlloyerRecipe]()
        val alloyer = RecipeManager.getHandler[AlloyerRecipeHandler](RecipeManager.Alloyer).recipes
        for (recipe <- alloyer) {
            val fluid1 = recipe.getFluidFromString(recipe.fluidOne)
            val fluid2 = recipe.getFluidFromString(recipe.fluidTwo)
            val fluidOut = recipe.getFluidFromString(recipe.fluidOut)
            if (fluid1 != null && fluid2 != null && fluidOut != null)
                recipes.add(new JEIAlloyerRecipe(fluid1, fluid2, fluidOut))
            else LogHelper.severe("[NeoTech] AlloyerRecipe json is corrupt! Please delete and recreate!")
        }
        recipes
    }

}
