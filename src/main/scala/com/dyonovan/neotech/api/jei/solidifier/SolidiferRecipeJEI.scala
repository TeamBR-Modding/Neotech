package com.dyonovan.neotech.api.jei.solidifier

import mezz.jei.api.recipe.BlankRecipeWrapper
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

import java.util.Collections

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
class SolidiferRecipeJEI(fluid: FluidStack, amount: Int, output: ItemStack) extends BlankRecipeWrapper {



    override def getFluidInputs: java.util.List[FluidStack] = Collections.singletonList(fluid)

    override def getOutputs: java.util.List[ItemStack] = Collections.singletonList(output)

    def getMode: Int = amount

}
