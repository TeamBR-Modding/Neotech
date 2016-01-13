package com.dyonovan.neotech.api.jei.crusher

import com.dyonovan.neotech.lib.Reference
import mezz.jei.api.recipe.{IRecipeWrapper, IRecipeHandler}

/**
  * Created by Dyonovan on 1/13/2016.
  */
class CrusherRecipeHandler extends IRecipeHandler[CrusherRecipeJEI]{

    override def getRecipeWrapper(recipe: CrusherRecipeJEI): IRecipeWrapper = recipe

    override def getRecipeCategoryUid: String = Reference.MOD_ID + ":electricCrusher"

    override def isRecipeValid(recipe: CrusherRecipeJEI): Boolean = true

    override def getRecipeClass: Class[CrusherRecipeJEI] = classOf[CrusherRecipeJEI]
}
