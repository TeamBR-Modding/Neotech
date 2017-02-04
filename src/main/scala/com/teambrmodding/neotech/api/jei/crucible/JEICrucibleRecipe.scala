package com.teambrmodding.neotech.api.jei.crucible

import java.util
import java.util.Collections

import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.BlankRecipeWrapper
import net.minecraft.item.ItemStack
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
class JEICrucibleRecipe(input: java.util.List[ItemStack], output: FluidStack) extends BlankRecipeWrapper {

    override def getInputs: java.util.List[ItemStack] = input

    override def getFluidOutputs: java.util.List[FluidStack] = util.Arrays.asList(output)

    override def getIngredients(ingredients: IIngredients): Unit = {}
}
