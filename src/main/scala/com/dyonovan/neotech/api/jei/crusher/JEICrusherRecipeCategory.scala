package com.dyonovan.neotech.api.jei.crusher

import com.dyonovan.neotech.api.jei.NeoTechPlugin
import com.dyonovan.neotech.lib.Reference
import mezz.jei.api.gui._
import mezz.jei.api.recipe.{IRecipeCategory, IRecipeWrapper}
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraft.util.{ResourceLocation, StatCollector}

/**
  * Created by Dyonovan on 1/13/2016.
  */
class JEICrusherRecipeCategory extends IRecipeCategory {

    val location = new ResourceLocation(Reference.MOD_ID, "textures/gui/jei/crusher.png")
    val arrow = NeoTechPlugin.jeiHelpers.getGuiHelper.createAnimatedDrawable(
        NeoTechPlugin.jeiHelpers.getGuiHelper.createDrawable(location, 176, 14, 24, 17), 75, IDrawableAnimated.StartDirection.LEFT, false)
    val power = NeoTechPlugin.jeiHelpers.getGuiHelper.createAnimatedDrawable(
        NeoTechPlugin.jeiHelpers.getGuiHelper.createDrawable(location, 175, 31, 12, 45), 75, IDrawableAnimated.StartDirection.TOP, true)
    val background = NeoTechPlugin.jeiHelpers.getGuiHelper.createDrawable(location, 10, 15, 150, 50)

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
        arrow.draw(minecraft, 59, 21)
        power.draw(minecraft, 5, 4)

    }

    override def drawExtras(minecraft: Minecraft): Unit = { }

    override def getTitle: String = StatCollector.translateToLocal("tile.neotech:electricCrusher.name")

    override def getUid: String = Reference.MOD_ID + ":electricCrusher"
}
