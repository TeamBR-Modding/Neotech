package com.dyonovan.neotech.registries

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.{FluidRegistry, FluidStack}
import net.minecraftforge.fml.common.registry.GameRegistry

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * Used as the base class for all recipes handler recipes
  *
  * I : Recipe Input
  * O : Recipe Output
  *
  * @author Paul Davis <pauljoda>
  * @since 2/20/2016
  */
abstract class AbstractRecipe[I, O] {

    /**
      * Used to get the output of this recipe
      *
      * @param input The input object
      * @return The output object
      */
    def getOutput(input : I) : Option[O]

    /**
      * Is the input valid for an output
      *
      * @param input The input object
      * @return True if there is an output
      */
    def isValidInput(input : I) : Boolean

    /*******************************************************************************************************************
      ******************************************** Helper Methods ******************************************************
      ******************************************************************************************************************/

    /**
      * Used to get the string form of an ItemStack
      *
      * @param itemStack The stack to translate
      * @return A string version of the stack in format MODID:ITEMID:META
      */
    def getItemStackString(itemStack: ItemStack): String = {
        val id: GameRegistry.UniqueIdentifier = GameRegistry.findUniqueIdentifierFor(itemStack.getItem)
        id.modId + ":" + id.name + ":" + itemStack.getItemDamage
    }

    /**
      * Used to get a stack from a string
      *
      * @param item The item string in format MODID:ITEMID:META
      * @return The stack for the string
      */
    def getItemStackFromString(item: String): ItemStack = {
        val name: Array[String] = item.split(":")
        name.length match {
            case 3 =>
                if (item == "")
                    null
                else
                    new ItemStack(GameRegistry.findItem(name(0), name(1)), 1, Integer.valueOf(name(2)))
            case _ => null
        }
    }

    /**
      * Converts a FluidStack into a string form
      *
      * @param fluidStack The stack
      * @return The string in form FLUID:AMOUNT
      */
    def getFluidString(fluidStack: FluidStack) : String = {
        FluidRegistry.getFluidName(fluidStack) + ":" + fluidStack.amount
    }

    /**
      * Converts the string form of a fluid into a stack
      *
      * @param string The string in format FLUID:AMOUNT
      * @return The FluidStack for the string
      */
    def getFluidFromString(string : String) : FluidStack = {
        FluidRegistry.getFluidStack(string.split(":")(0), string.split(":")(1).toInt)
    }
}
