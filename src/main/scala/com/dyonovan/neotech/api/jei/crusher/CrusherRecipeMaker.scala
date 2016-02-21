package com.dyonovan.neotech.api.jei.crusher

import java.util

import com.dyonovan.neotech.managers.RecipeManager
import com.dyonovan.neotech.registries.CrusherRecipes


/**
  * Created by Dyonovan on 1/15/2016.
  */
object CrusherRecipeMaker {

    def getRecipes: java.util.List[CrusherRecipeJEI] = {
        val recipes = new util.ArrayList[CrusherRecipeJEI]()
        val crusher = RecipeManager.getHandler[CrusherRecipeHandler](RecipeManager.Crusher).
            val j = i.asInstanceOf[CrusherRecipes]
            j.
            recipes.add(new CrusherRecipeJEI(crusher.get(i).input, crusher.get(i).output, crusher.get(i).secondary,
                crusher.get(i).secPercent))
        }

        recipes
    }
}
