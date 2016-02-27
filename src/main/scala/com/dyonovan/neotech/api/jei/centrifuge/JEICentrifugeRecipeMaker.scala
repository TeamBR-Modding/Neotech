package com.dyonovan.neotech.api.jei.centrifuge

import java.util

import com.dyonovan.neotech.managers.RecipeManager
import com.dyonovan.neotech.registries.CentrifugeRecipeHandler
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
object JEICentrifugeRecipeMaker {

    def getRecipes: util.List[JEICentrifugeRecipe] = {
        val recipes = new util.ArrayList[JEICentrifugeRecipe]()
        val centrifuge = RecipeManager.getHandler[CentrifugeRecipeHandler](RecipeManager.Centrifuge).recipes
        for (recipe <- centrifuge) {
            val input = recipe.getFluidFromString(recipe.fluidIn)
            val out1 = recipe.getFluidFromString(recipe.fluidOne)
            val out2 = recipe.getFluidFromString(recipe.fluidTwo)
            if (input != null && out1 != null && out2 != null)
                recipes.add(new JEICentrifugeRecipe(recipe.getFluidFromString(recipe.fluidIn), recipe.getFluidFromString(recipe.fluidOne),
                    recipe.getFluidFromString(recipe.fluidTwo)))
            else LogHelper.severe("[NeoTech] CentrifugeRecipe json is corrupt! Please delete and recreate!")
        }
        recipes
    }

}
