package com.dyonovan.neotech.api.jei.grinder

import java.util

import com.dyonovan.neotech.registries.CrusherRecipeRegistry

/**
  * Created by Dyonovan on 1/16/2016.
  */
object GrinderRecipeMaker  {

    def getRecipes: java.util.List[GrinderRecipeJEI] = {
        val recipes = new util.ArrayList[GrinderRecipeJEI]()
        val crusher = CrusherRecipeRegistry.getRecipes

        for (i <- 0 until crusher.size()) {
            recipes.add(new GrinderRecipeJEI(crusher.get(i).input, crusher.get(i).output))
        }
        recipes
    }
}
