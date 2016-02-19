package com.dyonovan.neotech.registries

import java.io.File
import java.util

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.managers.MetalManager
import com.google.gson.reflect.TypeToken
import com.teambr.bookshelf.helper.LogHelper
import com.teambr.bookshelf.util.JsonUtils
import net.minecraft.init.{Items, Blocks}
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.{Fluid, FluidRegistry, FluidStack}
import net.minecraftforge.fml.common.registry.GameRegistry
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
object SolidifierRegistry {

    var solidifierRecipes = new util.ArrayList[SolidifierRecipe]()

    /**
      * Add the values
      */
    def init(): Unit = {
        if (!loadFromFile)
            generateDefaults()
        else
            LogHelper.info("Solidifier Recipes loaded successfully")
    }

    /**
      * Load the values from the file
      *
      * @return True if successful
      */
    def loadFromFile(): Boolean = {
        LogHelper.info("Loading Solidifier Recipes...")
        solidifierRecipes = JsonUtils.readFromJson[util.ArrayList[SolidifierRecipe]](new TypeToken[util.ArrayList[SolidifierRecipe]]() {
        }, NeoTech.configFolderLocation + File.separator + "Registries" + File.separator + "solidifierRecipes.json")
        if (solidifierRecipes == null)
            solidifierRecipes = new util.ArrayList[SolidifierRecipe]()
        !solidifierRecipes.isEmpty
    }

    /**
      * Save the current registry to a file
      */
    def saveToFile(): Unit = {
        if (!solidifierRecipes.isEmpty) JsonUtils.writeToJson(solidifierRecipes, NeoTech.configFolderLocation +
                File.separator + "Registries" + File.separator + "solidifierRecipes.json")
    }

    /**
      * Used to generate the default values
      */
    def generateDefaults(): Unit = {
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
        LogHelper.info("Finished adding " + solidifierRecipes.size + " Solidifier Recipes")
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
        solidifierRecipes.add(recipe)
    }

    /**
      * Get the output of this itemstack
      *
      * @param input The Input
      * @return The FluidStack returned, None if non existent
      */
    def getOutput(input : FluidStack) : Option[ItemStack] = {
        if(input == null) //Safety Check
            return None

        //Check registered
        for(x <- 0 until solidifierRecipes.size()) {
            val recipe = solidifierRecipes.get(x)
            if(getFluidFromString(recipe.input).isFluidEqual(input) && getFluidFromString(recipe.input).amount == input.amount)
                return Option(getItemStackFromString(recipe.output))
        }

        None
    }

    def isFluidValid(fluid : Fluid) : Boolean = {
        if(fluid == null) //Safety Check
            return false

        //Check registered
        for(x <- 0 until solidifierRecipes.size()) {
            val recipe = solidifierRecipes.get(x)
            if(getFluidFromString(recipe.input).getFluid == fluid)
                return true
        }

        false
    }

    /**
      * Helper class for holding recipes
      *
      */
    class SolidifierRecipe(val input : String, val ore : String, val output : String) {}

    def getItemStackString(itemStack: ItemStack): String = {
        val id: GameRegistry.UniqueIdentifier = GameRegistry.findUniqueIdentifierFor(itemStack.getItem)
        id.modId + ":" + id.name + ":" + itemStack.getItemDamage
    }

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

    def getFluidString(fluidStack: FluidStack) : String = {
        FluidRegistry.getFluidName(fluidStack) + ":" + fluidStack.amount
    }

    def getFluidFromString(string : String) : FluidStack = {
        FluidRegistry.getFluidStack(string.split(":")(0), string.split(":")(1).toInt)
    }
}