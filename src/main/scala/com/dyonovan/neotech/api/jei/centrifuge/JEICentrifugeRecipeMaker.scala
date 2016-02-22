package com.dyonovan.neotech.api.jei.centrifuge

import java.util
import java.util.Collections

import com.dyonovan.neotech.api.jei.crucible.JEICrucibleRecipe
import com.dyonovan.neotech.managers.RecipeManager
import com.dyonovan.neotech.registries.{CentrifugeRecipe, CentrifugeRecipeHandler}

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
            recipes.add(new JEICentrifugeRecipe(recipe.getFluidFromString(recipe.fluidIn), recipe.getFluidFromString(recipe.fluidOne),
                recipe.getFluidFromString(recipe.fluidTwo)))
        }
        recipes
    }

}
