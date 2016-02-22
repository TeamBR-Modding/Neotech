package com.dyonovan.neotech.api.jei.grinder

import java.util

import com.dyonovan.neotech.api.jei.crusher.CrusherRecipeJEI
import com.dyonovan.neotech.managers.RecipeManager
import com.dyonovan.neotech.registries.{CrusherRecipes, CrusherRecipeHandler}


/**
  * Created by Dyonovan on 1/16/2016.
  */
object GrinderRecipeMaker  {

    def getRecipes: java.util.List[GrinderRecipeJEI] = {
        val recipes = new util.ArrayList[GrinderRecipeJEI]()
        val crusher = RecipeManager.getHandler[CrusherRecipeHandler](RecipeManager.Crusher).recipes.toArray()
        for (i <- crusher) {
            val recipe = i.asInstanceOf[CrusherRecipes]
            val output = recipe.getItemStackFromString(recipe.output)
            output.stackSize = recipe.qty
            recipes.add(new GrinderRecipeJEI(recipe.getItemStackFromString(recipe.input), output))
        }
        recipes
    }
}
