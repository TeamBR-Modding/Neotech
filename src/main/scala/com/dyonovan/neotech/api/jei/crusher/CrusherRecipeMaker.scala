package com.dyonovan.neotech.api.jei.crusher

import java.util

import com.dyonovan.neotech.registries.CrusherRecipeRegistry

/**
  * Created by Dyonovan on 1/15/2016.
  */
object CrusherRecipeMaker {

    def getRecipes: java.util.List[CrusherRecipeJEI] = {
        val recipes = new util.ArrayList[CrusherRecipeJEI]()
        val crusher = CrusherRecipeRegistry.getRecipes

        for (i <- 0 until crusher.size()) {
            recipes.add(new CrusherRecipeJEI(crusher.get(i).input, crusher.get(i).output, crusher.get(i).secondary,
                crusher.get(i).secPercent))
        }

        recipes
    }
}
