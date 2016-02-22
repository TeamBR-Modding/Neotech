package com.dyonovan.neotech.api.jei.crusher

import java.util

import com.dyonovan.neotech.managers.RecipeManager
import com.dyonovan.neotech.registries.{CrusherRecipes, CrusherRecipeHandler}


/**
  * Created by Dyonovan on 1/15/2016.
  */
object JEICrusherRecipeMaker {

    def getRecipes: java.util.List[JEICrusherRecipe] = {
        val recipesJEI = new util.ArrayList[JEICrusherRecipe]()
        val crusher = RecipeManager.getHandler[CrusherRecipeHandler](RecipeManager.Crusher).recipes.toArray()
        for (i <- crusher) {
            val recipe = i.asInstanceOf[CrusherRecipes]
            val output = recipe.getItemStackFromString(recipe.output)
            output.stackSize = recipe.qty
            recipesJEI.add(new JEICrusherRecipe(recipe.getItemStackFromString(recipe.input), output,
                recipe.getItemStackFromString(recipe.outputSecondary), recipe.percentChance))
        }
        recipesJEI
    }
}
