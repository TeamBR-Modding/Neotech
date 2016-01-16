package com.dyonovan.neotech.api.jei.grinder

import com.dyonovan.neotech.api.jei.NeoTechPlugin
import com.dyonovan.neotech.lib.Reference
import mezz.jei.api.gui.{IDrawable, IGuiItemStackGroup, IRecipeLayout}
import mezz.jei.api.recipe.{IRecipeCategory, IRecipeWrapper}
import net.minecraft.client.Minecraft
import net.minecraft.util.{ResourceLocation, StatCollector}

/**
  * Created by Dyonovan on 1/16/2016.
  */
class GrinderRecipeCategory extends IRecipeCategory {

    val location = new ResourceLocation(Reference.MOD_ID, "textures/gui/nei/grinder.png")

    override def getBackground: IDrawable = NeoTechPlugin.jeiHelpers.getGuiHelper.createDrawable(location, 0, 0, 170, 60)

    override def setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper): Unit = {

        val stacks: IGuiItemStackGroup = recipeLayout.getItemStacks
        stacks.init(0, true, 87, 5)
        stacks.init(1, false, 66, 30)

        recipeWrapper match {
            case grinderRecipeWrapper: GrinderRecipeJEI =>
                recipeLayout.getItemStacks.set(0, grinderRecipeWrapper.getInputs)
                recipeLayout.getItemStacks.set(1, grinderRecipeWrapper.getOutputs)
            case _ =>
        }
    }

    override def drawAnimations(minecraft: Minecraft): Unit = {}

    override def drawExtras(minecraft: Minecraft): Unit = {}

    override def getTitle: String = StatCollector.translateToLocal("tile.neotech:grinder.name")

    override def getUid: String = Reference.MOD_ID + ":grinder"
}
