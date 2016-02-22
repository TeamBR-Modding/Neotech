package com.dyonovan.neotech.api.jei.grinder

import java.util

import com.dyonovan.neotech.managers.RecipeManager
import com.dyonovan.neotech.registries.{CrusherRecipeHandler, CrusherRecipes}


/**
  * Created by Dyonovan on 1/16/2016.
  */
object JEIGrinderRecipeMaker  {

    def getRecipes: java.util.List[JEIGrinderRecipe] = {
        val recipes = new util.ArrayList[JEIGrinderRecipe]()
        val crusher = RecipeManager.getHandler[CrusherRecipeHandler](RecipeManager.Crusher).recipes.toArray()
        for (i <- crusher) {
            val recipe = i.asInstanceOf[CrusherRecipes]
            val output = recipe.getItemStackFromString(recipe.output)
            output.stackSize = recipe.qty
            recipes.add(new JEIGrinderRecipe(recipe.getItemStackFromString(recipe.input), output))
        }
        recipes
    }
}
