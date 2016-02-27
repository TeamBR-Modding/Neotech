package com.dyonovan.neotech.api.jei.centrifuge

import java.util
import java.util.Collections

import com.dyonovan.neotech.api.jei.crucible.JEICrucibleRecipe
import com.dyonovan.neotech.managers.RecipeManager
import com.dyonovan.neotech.registries.{CentrifugeRecipe, CentrifugeRecipeHandler}
import com.teambr.bookshelf.helper.LogHelper

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
        val centrifuge = RecipeManager.getHandler[CentrifugeRecipeHandler](RecipeManager.Centrifuge).recipes.toArray()
        for (i <- centrifuge) {
            val recipe = i.asInstanceOf[CentrifugeRecipe]
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
