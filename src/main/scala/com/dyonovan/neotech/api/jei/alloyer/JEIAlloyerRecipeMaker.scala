package com.dyonovan.neotech.api.jei.alloyer

import java.util

import com.dyonovan.neotech.managers.RecipeManager
import com.dyonovan.neotech.registries.{AlloyerRecipe, AlloyerRecipeHandler}

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
        val alloyer = RecipeManager.getHandler[AlloyerRecipeHandler](RecipeManager.Alloyer).recipes.toArray()
        for (i <- alloyer) {
            val recipe = i.asInstanceOf[AlloyerRecipe]
            recipes.add(new JEIAlloyerRecipe(recipe.getFluidFromString(recipe.fluidOne), recipe.getFluidFromString(recipe.fluidTwo), recipe.getFluidFromString(recipe.fluidOut)))
        }
        recipes
    }

}
