package com.dyonovan.neotech.api.jei.solidifier

import java.awt.Color

import com.dyonovan.neotech.api.jei.NeoTechPlugin
import com.dyonovan.neotech.api.jei.drawables._
import com.dyonovan.neotech.lib.Reference
import mezz.jei.api.gui._
import mezz.jei.api.recipe.{IRecipeCategory, IRecipeWrapper}
import net.minecraft.client.Minecraft
import net.minecraft.util.{ResourceLocation, StatCollector}

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

    val location = new ResourceLocation(Reference.MOD_ID, "textures/gui/jei/jei.png")
    val arrow = new GuiComponentArrowJEI(97, 17)
    val power = new GuiComponentPowerBarJEI(14, 0, 18, 60, new Color(255, 0, 0)) {
        addColor(new Color(255, 150, 0))
        addColor(new Color(255, 255, 0))
    }
    val slotOutput = new SlotDrawable(133, 17)
    val tank = new GuiComponentBox(35, 0, 50, 60)

    override def getBackground: IDrawable = NeoTechPlugin.jeiHelpers.getGuiHelper.createDrawable(location, 0, 0, 170, 60)

    override def setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper): Unit = {

        val fluidStack: IGuiFluidStackGroup = recipeLayout.getFluidStacks
        val itemStack: IGuiItemStackGroup = recipeLayout.getItemStacks
        fluidStack.init(0, true, 36, 0, 48, 60, 2000, false, null)
        itemStack.init(0, false, 133, 17)

        recipeWrapper match {
            case solidifierRecipeWrapper: JEISolidifierRecipe =>
                recipeLayout.getFluidStacks.set(0, solidifierRecipeWrapper.getFluidInputs)
                recipeLayout.getItemStacks.set(0, solidifierRecipeWrapper.getOutputs.get(0))

               /* val tip = new ITooltipCallback[ItemStack] {
                    override def onTooltip(slotIndex: Int, input: Boolean, ingredient: ItemStack, tooltip: util.List[String]): Unit = {
                        if (slotIndex == 0) {
                            var mode: String = ""
                            solidifierRecipeWrapper.getFluidInputs.get(0).amount match {
                                case 1296 => mode = "Block"
                                case 144 => mode = "Ingot"
                                case 16 => mode = "Nugget"
                                case _ =>
                            }
                            tooltip.add(mode + " Mode")
                        }
                    }
                }
                recipeLayout.getItemStacks.addTooltipCallback(tip)*/
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

    override def getTitle: String = StatCollector.translateToLocal("tile.neotech:electricSolidifier.name")

    override def getUid: String = Reference.MOD_ID + ":solidifier"
}
