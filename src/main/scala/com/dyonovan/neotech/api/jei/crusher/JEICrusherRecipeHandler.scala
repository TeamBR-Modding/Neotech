package com.dyonovan.neotech.api.jei.crusher

import com.dyonovan.neotech.lib.Reference
import mezz.jei.api.recipe.{IRecipeWrapper, IRecipeHandler}

/**
  * Created by Dyonovan on 1/13/2016.
  */
class JEICrusherRecipeHandler extends IRecipeHandler[JEICrusherRecipe]{

    override def getRecipeWrapper(recipe: JEICrusherRecipe): IRecipeWrapper = recipe

    override def getRecipeCategoryUid: String = Reference.MOD_ID + ":electricCrusher"

    override def isRecipeValid(recipe: JEICrusherRecipe): Boolean = true

    override def getRecipeClass: Class[JEICrusherRecipe] = classOf[JEICrusherRecipe]
}
