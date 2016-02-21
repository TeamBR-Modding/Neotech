package com.dyonovan.neotech.api.jei.crusher

import java.util

import com.dyonovan.neotech.managers.RecipeManager
import com.dyonovan.neotech.registries.{CrusherRecipeHandler, CrusherRecipes}


/**
  * Created by Dyonovan on 1/15/2016.
  */
object CrusherRecipeMaker {

    def getRecipes: java.util.List[CrusherRecipeJEI] = {
        val recipes = new util.ArrayList[CrusherRecipeJEI]()
        val crusher = RecipeManager.getHandler[CrusherRecipeHandler](RecipeManager.Crusher).getCrusherRecipes
        for (i <- 0 until crusher.size()) {
            val recipe = crusher.get(i)
            //recipe.
        }

        recipes
    }
}
