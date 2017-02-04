package com.teambrmodding.neotech.api.jei.grinder

import java.util

import com.teambrmodding.neotech.managers.RecipeManager
import com.teambrmodding.neotech.registries.CrusherRecipeHandler
import com.teambr.bookshelf.helper.LogHelper

import scala.collection.JavaConversions._


/**
  * Created by Dyonovan on 1/16/2016.
  */
object JEIGrinderRecipeMaker  {

    def getRecipes: java.util.List[JEIGrinderRecipe] = {
        val recipes = new util.ArrayList[JEIGrinderRecipe]()
        val crusher = RecipeManager.getHandler[CrusherRecipeHandler](RecipeManager.Crusher).recipes
        for (recipe <- crusher) {
            val input = recipe.getItemStackFromString(recipe.input)
            val output = recipe.getItemStackFromString(recipe.output)
            if (input != null && output != null) {
                output.stackSize = recipe.qty
                recipes.add(new JEIGrinderRecipe(input, output))
            } else
                LogHelper.severe("[NeoTech] CrusherRecipe json is corrupt! Please delete and recreate!")
        }
        recipes
    }
}
