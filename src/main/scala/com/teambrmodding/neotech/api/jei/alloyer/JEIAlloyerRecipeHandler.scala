package com.teambrmodding.neotech.api.jei.alloyer

import com.teambrmodding.neotech.api.jei.NeotechRecipeCategoryUID
import com.teambrmodding.neotech.lib.Reference
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
class JEIAlloyerRecipeHandler extends IRecipeHandler[JEIAlloyerRecipeWrapper]{

    override def getRecipeWrapper(recipe: JEIAlloyerRecipeWrapper): IRecipeWrapper = recipe

    override def getRecipeCategoryUid: String = Reference.MOD_ID + ":alloyer"

    override def isRecipeValid(recipe: JEIAlloyerRecipeWrapper): Boolean = true

    override def getRecipeClass: Class[JEIAlloyerRecipeWrapper] = classOf[JEIAlloyerRecipeWrapper]

    override def getRecipeCategoryUid(recipe: JEIAlloyerRecipeWrapper): String = NeotechRecipeCategoryUID.ALLOYER
}
