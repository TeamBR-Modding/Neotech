package com.teambrmodding.neotech.api.jei.centrifuge

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
class JEICentrifugeRecipe(fluidIn: FluidStack, fluidOut1: FluidStack, fluidOut2: FluidStack) extends BlankRecipeWrapper {

    override def getFluidInputs: java.util.List[FluidStack] = Collections.singletonList(fluidIn)

    override def getFluidOutputs: java.util.List[FluidStack] = util.Arrays.asList(fluidOut1, fluidOut2)

}
