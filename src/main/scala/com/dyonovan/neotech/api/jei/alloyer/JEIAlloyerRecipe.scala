package com.dyonovan.neotech.api.jei.alloyer

import java.util
import java.util.Collections

import mezz.jei.api.recipe.BlankRecipeWrapper
import net.minecraftforge.fluids.FluidStack

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
class JEIAlloyerRecipe(fluidIn1: FluidStack, fluidIn2: FluidStack, fluidOut: FluidStack) extends BlankRecipeWrapper {

    override def getFluidInputs: java.util.List[FluidStack] = util.Arrays.asList(fluidIn1, fluidIn2)

    override def getFluidOutputs: java.util.List[FluidStack] = Collections.singletonList(fluidOut)
}
