package com.dyonovan.neotech.api.jei.alloyer

import java.awt.Color

import com.dyonovan.neotech.api.jei.NeoTechPlugin
import com.dyonovan.neotech.lib.Reference
import com.teambr.bookshelf.api.jei.drawables.{GuiComponentArrowJEI, GuiComponentBox, GuiComponentPowerBarJEI}
import mezz.jei.api.gui.{IDrawable, IRecipeLayout}
import mezz.jei.api.recipe.{IRecipeCategory, IRecipeWrapper}
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.translation.I18n

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
class JEIAlloyerRecipeCategory extends IRecipeCategory {

    val location = new ResourceLocation(Reference.MOD_ID, "textures/gui/jei/jei.png")
    val arrow = new GuiComponentArrowJEI(81, 17, NeoTechPlugin.jeiHelpers)
    val power = new GuiComponentPowerBarJEI(14, 0, 18, 60, new Color(255, 0, 0), NeoTechPlugin.jeiHelpers) {
        addColor(new Color(255, 150, 0))
        addColor(new Color(255, 255, 0))
    }
    val tankIn1 = new GuiComponentBox(38, 0, 18, 60)
    val tankIn2 = new GuiComponentBox(60, 0, 18, 60)
    val tankOut = new GuiComponentBox(115, 0, 50, 60)

    override def getBackground: IDrawable = NeoTechPlugin.jeiHelpers.getGuiHelper.createDrawable(location, 0, 0, 170, 60)

    override def setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper): Unit = {
        val fluids = recipeLayout.getFluidStacks
        fluids.init(0, true, 39, 0, 16, 59, 2000, false, null)
        fluids.init(1, true, 61, 0, 16, 59, 2000, false, null)
        fluids.init(2, false, 116, 0, 48, 59, 2000, false, null)

        recipeWrapper match {
            case alloyer: JEIAlloyerRecipe =>
                recipeLayout.getFluidStacks.set(0, alloyer.getFluidInputs.get(0))
                recipeLayout.getFluidStacks.set(1, alloyer.getFluidInputs.get(1))
                recipeLayout.getFluidStacks.set(2, alloyer.getFluidOutputs)
        }
    }

    override def drawAnimations(minecraft: Minecraft): Unit = {
        arrow.draw(minecraft, 0, 0)
        power.draw(minecraft, 0, 0)
    }

    override def drawExtras(minecraft: Minecraft): Unit = {
        tankIn1.draw(minecraft)
        tankIn2.draw(minecraft)
        tankOut.draw(minecraft)
    }

    override def getTitle: String = I18n.translateToLocal("tile.neotech:alloyer.name")

    override def getUid: String = Reference.MOD_ID + ":alloyer"
}
