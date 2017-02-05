package com.teambrmodding.neotech.api.jei.crucible

import java.awt.Color

import com.teambrmodding.neotech.api.jei.{NeoTechPlugin, NeotechRecipeCategoryUID}
import com.teambrmodding.neotech.lib.Reference
import com.teambr.bookshelf.api.jei.drawables.{GuiComponentArrowJEI, GuiComponentBox, GuiComponentPowerBarJEI, SlotDrawable}
import mezz.jei.api.gui.{IDrawable, IRecipeLayout}
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
  * @since 2/22/2016u
  */
class JEICrucibleRecipeCategory extends IRecipeCategory[IRecipeWrapper] {

    val location = new ResourceLocation(Reference.MOD_ID, "textures/gui/jei/jei.png")
    val input = new SlotDrawable(56, 17)
    val arrow = new GuiComponentArrowJEI(81, 17, NeoTechPlugin.jeiHelpers)
    val power = new GuiComponentPowerBarJEI(14, 0, 18, 60, new Color(255, 0, 0), NeoTechPlugin.jeiHelpers) {
        addColor(new Color(255, 150, 0))
        addColor(new Color(255, 255, 0))
    }
    val tank = new GuiComponentBox(115, 0, 50, 60)

    override def getBackground: IDrawable = NeoTechPlugin.jeiHelpers.getGuiHelper.createDrawable(location, 0, 0, 170, 60)

    override def setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper): Unit = {
        val fluidStack = recipeLayout.getFluidStacks
        val itemStack = recipeLayout.getItemStacks
        fluidStack.init(0, false, 116, 0, 48, 59, 2000, false, null)
        itemStack.init(0, true, 56, 17)

        recipeWrapper match {
            case crucible: JEICrucibleRecipe =>
                recipeLayout.getFluidStacks.set(0, crucible.getFluidOutputs.get(0))
                recipeLayout.getItemStacks.set(0, crucible.getInputs.get(0))
            case _ =>
        }
    }

    override def drawAnimations(minecraft: Minecraft): Unit = {
        arrow.draw(minecraft, 0, 0)
        power.draw(minecraft, 0, 0)
    }

    override def drawExtras(minecraft: Minecraft): Unit = {
        input.draw(minecraft)
        tank.draw(minecraft)
    }

    override def getTitle: String = I18n.translateToLocal("tile.neotech:electricCrucible.name")

    override def getUid: String = NeotechRecipeCategoryUID.CRUCIBLE

    override def setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper, ingredients: IIngredients): Unit = {
        setRecipe(recipeLayout, recipeWrapper)
    }

    override def getIcon: IDrawable = null
}
