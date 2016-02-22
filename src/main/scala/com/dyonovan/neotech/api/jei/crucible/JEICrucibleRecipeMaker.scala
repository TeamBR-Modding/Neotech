package com.dyonovan.neotech.api.jei.crucible

import java.util

import com.dyonovan.neotech.managers.RecipeManager
import com.dyonovan.neotech.registries.{CrucibleRecipe, CrucibleRecipeHandler}
import net.minecraftforge.oredict.OreDictionary

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
object JEICrucibleRecipeMaker {

    def getRecipes: util.List[JEICrucibleRecipe] = {
        val recipes = new util.ArrayList[JEICrucibleRecipe]()
        val crucible = RecipeManager.getHandler[CrucibleRecipeHandler](RecipeManager.Crucible).recipes.toArray()
        for (i <- crucible) {
            val recipe = i.asInstanceOf[CrucibleRecipe]
            val inputList = OreDictionary.getOres(recipe.ore)
            recipes.add(new JEICrucibleRecipe(inputList, recipe.getFluidFromString(recipe.output)))
        }
        recipes
    }

}
