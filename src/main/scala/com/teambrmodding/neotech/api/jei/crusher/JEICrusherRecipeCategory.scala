package com.teambrmodding.neotech.api.jei.crusher

import java.awt.Color

import com.teambrmodding.neotech.api.jei.{NeoTechPlugin, NeotechRecipeCategoryUID}
import com.teambrmodding.neotech.lib.Reference
import com.teambr.bookshelf.api.jei.drawables.{GuiComponentArrowJEI, GuiComponentPowerBarJEI, SlotDrawable}
import mezz.jei.api.gui._
import mezz.jei.api.recipe.{IRecipeCategory, IRecipeWrapper}
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.translation.I18n

/**
  * Created by Dyonovan on 1/13/2016.
  */
class JEICrusherRecipeCategory extends IRecipeCategory[IRecipeWrapper] {

    val location = new ResourceLocation(Reference.MOD_ID, "textures/gui/jei/jei.png")
    val arrow = new GuiComponentArrowJEI(59, 21, NeoTechPlugin.jeiHelpers)
    val power = new GuiComponentPowerBarJEI(3, 0, 18, 55, new Color(255, 0, 0), NeoTechPlugin.jeiHelpers) {
        addColor(new Color(255, 150, 0))
        addColor(new Color(255, 255, 0))
    }
    val slotInput = new SlotDrawable(31, 20)
    val slotOutput = new SlotDrawable(96, 20, isLarge = true)
    val slotOutput2 = new SlotDrawable(125, 20, isLarge = true)

    val background = NeoTechPlugin.jeiHelpers.getGuiHelper.createDrawable(location, 10, 15, 150, 60)

    override def getBackground: IDrawable = background

    override def setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper): Unit = {
        val stacks: IGuiItemStackGroup = recipeLayout.getItemStacks
        stacks.init(0, true, 31, 20)
        stacks.init(1, false, 96, 20)
        stacks.init(2, false, 125, 20)

        recipeWrapper match {
            case crusherRecipeWrapper: JEICrusherRecipe =>
                recipeLayout.getItemStacks.set(0, crusherRecipeWrapper.getInputs)
                recipeLayout.getItemStacks.set(1, crusherRecipeWrapper.getOutputs.get(0))
                recipeLayout.getItemStacks.set(2, crusherRecipeWrapper.getOutputs.get(1))
                val tip = new ITooltipCallback[ItemStack] {
                    override def onTooltip(slotIndex: Int, input: Boolean, ingredient: ItemStack, tooltip: java.util.List[String]): Unit = {
                        if (slotIndex == 2 && crusherRecipeWrapper.secPercent > 0) {
                            tooltip.add(crusherRecipeWrapper.secPercent.toString + "% chance")
                        }
                    }
                }
                recipeLayout.getItemStacks.addTooltipCallback(tip)
            case _ =>
        }
    }

    override def drawAnimations(minecraft: Minecraft): Unit = {
        arrow.draw(minecraft, 0, 0)
        power.draw(minecraft, 0, 0)
    }

    override def drawExtras(minecraft: Minecraft): Unit = {
        slotInput.draw(minecraft)
        slotOutput.draw(minecraft)
        slotOutput2.draw(minecraft)
    }

    override def getTitle: String = I18n.translateToLocal("tile.neotech:electricCrusher.name")

    override def getUid: String = NeotechRecipeCategoryUID.CRUSHER
}
