package com.dyonovan.neotech.api.jei.crusher

import com.dyonovan.neotech.api.jei.NeoTechPlugin
import com.dyonovan.neotech.lib.Reference
import mezz.jei.api.gui.{IDrawable, IGuiItemStackGroup, IRecipeLayout}
import mezz.jei.api.recipe.{IRecipeCategory, IRecipeWrapper}
import net.minecraft.client.Minecraft
import net.minecraft.util.{ResourceLocation, StatCollector}

/**
  * Created by Dyonovan on 1/13/2016.
  */
class CrusherRecipeCategory extends IRecipeCategory {


    var background: IDrawable = NeoTechPlugin.jeiHelpers.getGuiHelper.createDrawable(new ResourceLocation(Reference.MOD_ID, "textures/gui/nei/crusher.png"),
        10, 15, 150, 50)

    override def getBackground: IDrawable = { background }

    override def setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper): Unit = {
        val stacks: IGuiItemStackGroup = recipeLayout.getItemStacks
        stacks.init(0, true, 31, 20)
        stacks.init(1, false, 96, 20)
        stacks.init(2, false, 125, 20)

        recipeWrapper match {
            case crusherRecipeWrapper: CrusherRecipeJEI =>
                recipeLayout.getItemStacks.set(0, crusherRecipeWrapper.getInputs)
                recipeLayout.getItemStacks.set(1, crusherRecipeWrapper.getOutputs.get(0))
                recipeLayout.getItemStacks.set(2, crusherRecipeWrapper.getOutputs.get(1))
            case _ =>
        }
    }

    override def drawAnimations(minecraft: Minecraft): Unit = {

    }

    override def drawExtras(minecraft: Minecraft): Unit = { }

    override def getTitle: String = StatCollector.translateToLocal("tile.neotech:electricCrusher.name")

    override def getUid: String = Reference.MOD_ID + ":electricCrusher"
}
