package com.dyonovan.neotech.api.jei.crusher

import java.util

import com.dyonovan.neotech.managers.RecipeManager
import com.dyonovan.neotech.registries.{CrusherRecipes, CrusherRecipeHandler}
import com.teambr.bookshelf.helper.LogHelper

import scala.collection.JavaConversions._

/**
  * Created by Dyonovan on 1/15/2016.
  */
object JEICrusherRecipeMaker {

    def getRecipes: java.util.List[JEICrusherRecipe] = {
        val recipesJEI = new util.ArrayList[JEICrusherRecipe]()
        val crusher = RecipeManager.getHandler[CrusherRecipeHandler](RecipeManager.Crusher).recipes
        for (recipe <- crusher) {
            val input = recipe.getItemStackFromString(recipe.input)
            val output = recipe.getItemStackFromString(recipe.output)
            if (input != null && output != null) {
                output.stackSize = recipe.qty
                recipesJEI.add(new JEICrusherRecipe(recipe.getItemStackFromString(recipe.input), output,
                    recipe.getItemStackFromString(recipe.outputSecondary), recipe.percentChance))
            } else LogHelper.severe("[NeoTech] CrusherRecipe json is corrupt! Please delete and recreate!")
        }
        recipesJEI
    }
}
