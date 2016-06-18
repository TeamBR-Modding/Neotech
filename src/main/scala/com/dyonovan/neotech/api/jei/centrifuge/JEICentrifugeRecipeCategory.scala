package com.dyonovan.neotech.api.jei.centrifuge

import java.awt.Color

import com.dyonovan.neotech.api.jei.{NeoTechPlugin, NeotechRecipeCategoryUID}
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
class JEICentrifugeRecipeCategory extends IRecipeCategory[IRecipeWrapper] {

    val location = new ResourceLocation(Reference.MOD_ID, "textures/gui/jei/jei.png")
    val arrow = new GuiComponentArrowJEI(94, 17, NeoTechPlugin.jeiHelpers)
    val power = new GuiComponentPowerBarJEI(14, 0, 18, 60, new Color(255, 0, 0), NeoTechPlugin.jeiHelpers) {
        addColor(new Color(255, 150, 0))
        addColor(new Color(255, 255, 0))
    }
    val tank1 = new GuiComponentBox(38, 0, 50, 60)
    val tank2 = new GuiComponentBox(125, 0, 18, 60)
    val tank3 = new GuiComponentBox(147, 0, 18, 60)

    override def getBackground: IDrawable = NeoTechPlugin.jeiHelpers.getGuiHelper.createDrawable(location, 0, 0, 170, 60)

    override def setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper): Unit = {
        val fluidStacks = recipeLayout.getFluidStacks
        fluidStacks.init(0, true, 39, 0, 48, 59, 2000, false, null)
        fluidStacks.init(1, false, 126, 0, 16, 59, 2000, false, null)
        fluidStacks.init(2, false, 148, 0, 16, 59, 2000, false, null)

        recipeWrapper match {
            case centrifuge: JEICentrifugeRecipe =>
                recipeLayout.getFluidStacks.set(0, centrifuge.getFluidInputs)
                recipeLayout.getFluidStacks.set(1, centrifuge.getFluidOutputs.get(0))
                recipeLayout.getFluidStacks.set(2, centrifuge.getFluidOutputs.get(1))
            case _ =>
        }
    }

    override def drawAnimations(minecraft: Minecraft): Unit = {
        arrow.draw(minecraft, 0, 0)
        power.draw(minecraft, 0, 0)
    }

    override def drawExtras(minecraft: Minecraft): Unit = {
        tank1.draw(minecraft)
        tank2.draw(minecraft)
        tank3.draw(minecraft)
    }

    override def getTitle: String = I18n.translateToLocal("tile.neotech:centrifuge.name")

    override def getUid: String = NeotechRecipeCategoryUID.CENTRIFUGE
}
