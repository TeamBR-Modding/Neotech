package com.dyonovan.neotech.api.jei.solidifier

import com.dyonovan.neotech.api.jei.NeoTechPlugin
import com.dyonovan.neotech.lib.Reference
import mezz.jei.api.gui.{IGuiItemStackGroup, IGuiFluidStackGroup, IDrawable, IRecipeLayout}
import mezz.jei.api.recipe.{IRecipeWrapper, IRecipeCategory}
import net.minecraft.client.Minecraft
import net.minecraft.util.{StatCollector, ResourceLocation}

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
class JEISolidifierRecipeCategory extends IRecipeCategory {

    val location = new ResourceLocation(Reference.MOD_ID, "textures/gui/jei/solidifier.png")

    override def getBackground: IDrawable = NeoTechPlugin.jeiHelpers.getGuiHelper.createDrawable(location, 0, 0, 170, 60)

    override def setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper): Unit = {

        val fluidStack: IGuiFluidStackGroup = recipeLayout.getFluidStacks
        val itemStack: IGuiItemStackGroup = recipeLayout.getItemStacks
        fluidStack.init(0, true, 87, 5, 10, 80, 10000, false, null)
        itemStack.init(0, false, 66, 30)

        recipeWrapper match {
            case solidifierRecipeWrapper: JEISolidifierRecipe =>
                recipeLayout.getFluidStacks.set(0, solidifierRecipeWrapper.getFluidInputs)
                recipeLayout.getItemStacks.set(0, solidifierRecipeWrapper.getOutputs)
        }
    }

    override def drawAnimations(minecraft: Minecraft): Unit = { }

    override def drawExtras(minecraft: Minecraft): Unit = { }

    override def getTitle: String = StatCollector.translateToLocal("tile.neotech:solidifier.name")

    override def getUid: String = Reference.MOD_ID + ":solidifier"
}
