package com.teambrmodding.neotech.api.jei.solidifier

import java.util

import com.teambrmodding.neotech.managers.RecipeManager
import com.teambrmodding.neotech.registries.SolidifierRecipeHandler
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
  * @since 2/20/2016
  */
object JEISolidifierRecipeMaker {

    def getRecipes: util.List[JEISolidifierRecipe] = {
        val recipes = new util.ArrayList[JEISolidifierRecipe]()
        val solidifier = RecipeManager.getHandler[SolidifierRecipeHandler](RecipeManager.Solidifier).recipes
        for (recipe <- solidifier) {
            val input = recipe.getFluidFromString(recipe.input)
            val output = recipe.getItemStackFromString(recipe.output)
            if (input != null && output != null)
                recipes.add(new JEISolidifierRecipe(input, output))
            else
                LogHelper.severe("[NeoTech] SolidifierRecipe json is corrupt! Please delete and recreate!")
        }
        recipes
    }
}
