package com.teambrmodding.neotech.api.jei.solidifier

import java.awt.Color

import com.teambrmodding.neotech.api.jei.{NeoTechPlugin, NeotechRecipeCategoryUID}
import com.teambrmodding.neotech.lib.Reference
import com.teambr.bookshelf.api.jei.drawables.{GuiComponentArrowJEI, GuiComponentBox, GuiComponentPowerBarJEI, SlotDrawable}
import mezz.jei.api.gui._
import mezz.jei.api.ingredients.IIngredients
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
  * @since 2/21/2016
  */
class JEISolidifierRecipeCategory extends IRecipeCategory[IRecipeWrapper] {

    val location = new ResourceLocation(Reference.MOD_ID, "textures/gui/jei/jei.png")
    val arrow = new GuiComponentArrowJEI(97, 17, NeoTechPlugin.jeiHelpers)
    val power = new GuiComponentPowerBarJEI(14, 0, 18, 60, new Color(255, 0, 0), NeoTechPlugin.jeiHelpers) {
        addColor(new Color(255, 150, 0))
        addColor(new Color(255, 255, 0))
    }
    val slotOutput = new SlotDrawable(133, 17)
    val tank = new GuiComponentBox(35, 0, 50, 60)

    override def getBackground: IDrawable = NeoTechPlugin.jeiHelpers.getGuiHelper.createDrawable(location, 0, 0, 170, 60)

    override def setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper): Unit = {

        val fluidStack: IGuiFluidStackGroup = recipeLayout.getFluidStacks
        val itemStack: IGuiItemStackGroup = recipeLayout.getItemStacks
        fluidStack.init(0, true, 36, 0, 48, 59, 2000, false, null)
        itemStack.init(0, false, 133, 17)

        recipeWrapper match {
            case solidifierRecipeWrapper: JEISolidifierRecipe =>
                recipeLayout.getFluidStacks.set(0, solidifierRecipeWrapper.getFluidInputs.get(0))
                recipeLayout.getItemStacks.set(0, solidifierRecipeWrapper.getOutputs.get(0))
            case _ =>
        }
    }

    override def drawAnimations(minecraft: Minecraft): Unit = {
        arrow.draw(minecraft, 0, 0)
        power.draw(minecraft, 0, 0)
    }

    override def drawExtras(minecraft: Minecraft): Unit = {
        slotOutput.draw(minecraft)
        tank.draw(minecraft)
    }

    override def getTitle: String = I18n.translateToLocal("tile.neotech:electricSolidifier.name")

    override def getUid: String = NeotechRecipeCategoryUID.SOLIDIFIER

    override def setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper, ingredients: IIngredients): Unit = {}

    override def getIcon: IDrawable = null
}
