package com.dyonovan.neotech.api.jei.alloyer

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
  * @since 2/22/2016
  */
class JEIAlloyerRecipeHandler extends IRecipeHandler[JEIAlloyerRecipe]{

    override def getRecipeWrapper(recipe: JEIAlloyerRecipe): IRecipeWrapper = recipe

    override def getRecipeCategoryUid: String = Reference.MOD_ID + ":alloyer"

    override def isRecipeValid(recipe: JEIAlloyerRecipe): Boolean = true

    override def getRecipeClass: Class[JEIAlloyerRecipe] = classOf[JEIAlloyerRecipe]

    override def getRecipeCategoryUid(recipe: JEIAlloyerRecipe): String = NeotechRecipeCategoryUID.ALLOYER
}
