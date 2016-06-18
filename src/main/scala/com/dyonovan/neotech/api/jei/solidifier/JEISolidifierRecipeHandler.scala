package com.dyonovan.neotech.api.jei.solidifier

import com.dyonovan.neotech.api.jei.NeotechRecipeCategoryUID
import com.dyonovan.neotech.lib.Reference
import mezz.jei.api.recipe.{IRecipeHandler, IRecipeWrapper}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/21/2016
  */
class JEISolidifierRecipeHandler extends IRecipeHandler[JEISolidifierRecipe] {

    override def getRecipeWrapper(recipe: JEISolidifierRecipe): IRecipeWrapper = recipe

    override def getRecipeCategoryUid: String = Reference.MOD_ID + ":solidifier"

    override def isRecipeValid(recipe: JEISolidifierRecipe): Boolean = true

    override def getRecipeClass: Class[JEISolidifierRecipe] = classOf[JEISolidifierRecipe]

    override def getRecipeCategoryUid(recipe: JEISolidifierRecipe): String = NeotechRecipeCategoryUID.SOLIDIFIER
}
