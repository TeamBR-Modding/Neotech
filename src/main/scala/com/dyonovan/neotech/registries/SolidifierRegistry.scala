package com.dyonovan.neotech.registries

import java.util

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.managers.MetalManager
import com.google.gson.reflect.TypeToken
import com.teambr.bookshelf.helper.LogHelper
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.oredict.OreDictionary

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/18/2016
  */
class SolidifierRegistry extends AbstractRecipeHandler[SolidifierRecipe, FluidStack, ItemStack] {

    /**
      * Used to get the base name of the files
      *
      * @return
      */
    override def getBaseName: String = "solidifier"

    /**
      * This is the current version of the registry, if you update this it will cause the registry to be redone
      *
      * @return
      */
    override def getVersion: Int = 1

    /**
      * Used to get the default folder location
      *
      * @return
      */
    override def getBaseFolderLocation: String = NeoTech.configFolderLocation

    /**
      * Used to get what type token to read from file (Generics don't handle well)
      *
      * @return
      */
    override def getTypeToken: TypeToken[util.ArrayList[SolidifierRecipe]] =
        new TypeToken[util.ArrayList[SolidifierRecipe]]() {}

    /**
      * Used to generate the default values
      */
    override def generateDefaultRecipes(): Unit = {
        LogHelper.info("Json not found. Creating Dynamic Solidifier Recipe List...")

        // Metals
        val iterator = MetalManager.metalRegistry.keySet().iterator()
        while(iterator.hasNext) {
            val metal = MetalManager.metalRegistry.get(iterator.next())
            if (metal.fluid.isDefined) {
                //Block
                if(metal.block.isDefined)
                    addSolidifierRecipe(new FluidStack(metal.fluid.get, MetalManager.BLOCK_MB), null, metal.block.get.getName)

                //Ingot
                if(metal.ingot.isDefined)
                    addSolidifierRecipe(new FluidStack(metal.fluid.get, MetalManager.INGOT_MB), null, metal.ingot.get.getName)

                //Nugget
                if(metal.nugget.isDefined)
                    addSolidifierRecipe(new FluidStack(metal.fluid.get, MetalManager.NUGGET_MB), null, metal.nugget.get.getName)
            }
        }

        // Extend Vanilla

        // Iron
        addSolidifierRecipe(new FluidStack(MetalManager.getMetal("iron").get.fluid.get, MetalManager.BLOCK_MB),
            new ItemStack(Blocks.iron_block), "blockIron")
        addSolidifierRecipe(new FluidStack(MetalManager.getMetal("iron").get.fluid.get, MetalManager.INGOT_MB),
            new ItemStack(Items.iron_ingot), "ingotIron")

        // Gold
        addSolidifierRecipe(new FluidStack(MetalManager.getMetal("gold").get.fluid.get, MetalManager.BLOCK_MB),
            new ItemStack(Blocks.gold_block), "blockGold")
        addSolidifierRecipe(new FluidStack(MetalManager.getMetal("gold").get.fluid.get, MetalManager.INGOT_MB),
            new ItemStack(Items.gold_ingot), "ingotGold")
        addSolidifierRecipe(new FluidStack(MetalManager.getMetal("gold").get.fluid.get, MetalManager.NUGGET_MB),
            new ItemStack(Items.gold_nugget), "nuggetGold")

        saveToFile()
        LogHelper.info("Finished adding " + recipes.size + " Solidifier Recipes")
    }

    /**
      * Adds the recipe
      *
      * @param output If you set null for the itemstack, it will attempt to create one from ore dict
      * @param fluidStack
      */
    def addSolidifierRecipe(fluidStack: FluidStack, output : ItemStack, ore : String) : Unit = {
        var stack : ItemStack = output
        if(output == null && !ore.isEmpty) {
            val stackList = OreDictionary.getOres(ore)
            if(!stackList.isEmpty) {
                stack = stackList.get(0)
            } else {
                LogHelper.severe("Could not add ore dict solidifier recipe for " + ore + " as it does not exist in the OreDictionary")
                return
            }
        }
        val recipe = new SolidifierRecipe(getFluidString(fluidStack), ore, getItemStackString(stack))
        addRecipe(recipe)
    }
}


/**
  * Helper class for holding recipes
  *
  */
class SolidifierRecipe(val input : String, val ore : String, val output : String)
    extends AbstractRecipe[FluidStack, ItemStack] {
    /**
      * Used to get the output of this recipe
      *
      * @param fluidIn The input object
      * @return The output object
      */
    override def getOutput(fluidIn: FluidStack): Option[ItemStack] = {
        if(fluidIn == null)
            return None
        if(getFluidFromString(input).isFluidEqual(fluidIn) && getFluidFromString(input).amount == fluidIn.amount)
            return Option(getItemStackFromString(output))
        None
    }

    /**
      * Is the input valid for an output
      *
      * @param fluidIn The input object
      * @return True if there is an output
      */
    override def isValidInput(fluidIn: FluidStack): Boolean = {
        if(fluidIn == null || fluidIn.getFluid == null)
            return false
        getFluidFromString(input).getFluid == fluidIn.getFluid
    }
}