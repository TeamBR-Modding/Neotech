package com.dyonovan.neotech.api.jei.solidifier

import java.util

import com.dyonovan.neotech.managers.RecipeManager
import com.dyonovan.neotech.registries.{SolidifierRecipeHandler, SolidifierRecipe}


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
        val solidifier = RecipeManager.getHandler[SolidifierRecipeHandler](RecipeManager.Solidifier).recipes.toArray()
        for (i <- solidifier) {
            val recipe = i.asInstanceOf[SolidifierRecipe]
            recipes.add(new JEISolidifierRecipe(recipe.getFluidFromString(recipe.input), recipe.ore, recipe.getItemStackFromString(recipe.output)))
        }
        recipes
    }
}
