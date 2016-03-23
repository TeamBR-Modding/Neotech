package com.dyonovan.neotech.api.jei.solidifier

import java.util
import java.util.Collections

import jei.drawables.GuiComponentItemStackButtonJEI
import com.dyonovan.neotech.managers.MetalManager
import mezz.jei.api.recipe.BlankRecipeWrapper
import net.minecraft.client.Minecraft
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.fluids.FluidStack

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/20/2016
  */
class JEISolidifierRecipe(fluid: FluidStack, output: ItemStack) extends BlankRecipeWrapper {

    override def getFluidInputs: util.List[FluidStack] = Collections.singletonList(fluid)

    override def getOutputs: util.List[ItemStack] = Collections.singletonList(output)

    override def drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int): Unit = {
        var item: Item = Item.getItemFromBlock(Blocks.iron_block)
        fluid.amount match {
            case 1296 => item = Item.getItemFromBlock(Blocks.iron_block)
            case 144 => item = Items.iron_ingot
            case 16 => item = MetalManager.getMetal("iron").get.nugget.get
            case _ =>
        }
        new GuiComponentItemStackButtonJEI(97, 37, new ItemStack(item)).draw(minecraft)
    }

}
