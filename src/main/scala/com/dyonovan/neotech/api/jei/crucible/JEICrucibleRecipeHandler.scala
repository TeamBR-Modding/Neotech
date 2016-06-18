package com.dyonovan.neotech.api.jei.crucible

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
class JEICrucibleRecipeHandler extends IRecipeHandler[JEICrucibleRecipe]{

    override def getRecipeWrapper(recipe: JEICrucibleRecipe): IRecipeWrapper = recipe

    override def getRecipeCategoryUid: String = Reference.MOD_ID + ":crucible"

    override def isRecipeValid(recipe: JEICrucibleRecipe): Boolean = true

    override def getRecipeClass: Class[JEICrucibleRecipe] = classOf[JEICrucibleRecipe]

    override def getRecipeCategoryUid(recipe: JEICrucibleRecipe): String = NeotechRecipeCategoryUID.CRUCIBLE
}
