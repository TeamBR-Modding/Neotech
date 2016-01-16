package com.dyonovan.neotech.api.jei.grinder

import com.dyonovan.neotech.lib.Reference
import mezz.jei.api.recipe.{IRecipeHandler, IRecipeWrapper}

/**
  * Created by Dyonovan on 1/16/2016.
  */
class GrinderRecipeHandler extends IRecipeHandler[GrinderRecipeJEI] {

    override def getRecipeCategoryUid: String = Reference.MOD_ID + ":grinder"

    override def getRecipeWrapper(recipe: GrinderRecipeJEI): IRecipeWrapper = recipe

    override def isRecipeValid(recipe: GrinderRecipeJEI): Boolean = true

    override def getRecipeClass: Class[GrinderRecipeJEI] = classOf[GrinderRecipeJEI]
}
