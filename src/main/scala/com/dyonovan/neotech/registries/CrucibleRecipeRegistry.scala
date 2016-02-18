package com.dyonovan.neotech.registries

import java.io.File
import java.util

import com.dyonovan.neotech.NeoTech
import com.google.gson.reflect.TypeToken
import com.teambr.bookshelf.helper.LogHelper
import com.teambr.bookshelf.util.JsonUtils
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.{FluidRegistry, FluidStack}
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
  * @since 2/16/2016
  */
object CrucibleRecipeRegistry {

    var crucibleRecipes = new util.ArrayList[CrucibleRecipe]()

    /**
      * Add the values
      */
    def init(): Unit = {
        if (!loadFromFile)
            generateDefaults()
        else
            LogHelper.info("Crucible Recipes loaded successfully")
    }

    /**
      * Load the values from the file
      *
      * @return True if successful
      */
    def loadFromFile(): Boolean = {
        LogHelper.info("Loading Crucible Recipes...")
        crucibleRecipes = JsonUtils.readFromJson[util.ArrayList[CrucibleRecipe]](new TypeToken[util.ArrayList[CrucibleRecipe]]() {
        }, NeoTech.configFolderLocation + File.separator + "Registries" + File.separator + "crucibleRecipes.json")
        if (crucibleRecipes == null)
            crucibleRecipes = new util.ArrayList[CrucibleRecipe]()
        !crucibleRecipes.isEmpty
    }

    /**
      * Save the current registry to a file
      */
    def saveToFile(): Unit = {
        if (!crucibleRecipes.isEmpty) JsonUtils.writeToJson(crucibleRecipes, NeoTech.configFolderLocation +
                File.separator + "Registries" + File.separator + "crucibleRecipes.json")
    }

    /**
      * Used to generate the default values
      */
    def generateDefaults(): Unit = {
        LogHelper.info("Json not found. Creating Dynamic Crucible Recipe List...")

        saveToFile()
        LogHelper.info("Finished adding " + crucibleRecipes.size + " Crucible Recipes")
    }

    /**
      * Adds the recipe
      *
      * @param input If you set null for the itemstack, it will attempt to create one from ore dict
      * @param fluidStack
      */
    def addCrucibleRecipe(input : ItemStack, ore : String, fluidStack: FluidStack) : Unit = {
        var stack : ItemStack = input
        if(input == null && ore != null) {
            val stackList = OreDictionary.getOres(ore)
            if(!stackList.isEmpty) {
                stack = stackList.get(0)
            } else {
                LogHelper.severe("Could not add ore dict crucible recipe for " + ore + " as it does not exist in the OreDictionary")
                return
            }
        }
        val recipe = new CrucibleRecipe(getItemStackString(stack), ore, getFluidString(fluidStack))
        crucibleRecipes.add(recipe)
        saveToFile()
    }

    /**
      * Get the output of this itemstack
      *
      * @param input The Input
      * @return The FluidStack returned, None if non existent
      */
    def getOutput(input : ItemStack) : Option[FluidStack] = {
        if(input == null) //Safety Check
            return None

        //Check registered
        for(x <- 0 until crucibleRecipes.size()) {
            val recipe = crucibleRecipes.get(x)
            if(getItemStackFromString(recipe.input) != null &&
                    getItemStackFromString(recipe.input).getIsItemStackEqual(input) || (
                    if(recipe.ore != null && OreDictionary.getOreIDs(input) != null)
                        OreDictionary.getOreIDs(input).toList.contains(OreDictionary.getOreID(recipe.ore))
                    else
                        false))
                return Option(getFluidFromString(recipe.output))
        }

        None
    }

    /**
      * Helper class for holding recipes
      *
      */
    class CrucibleRecipe(val input : String, val ore : String, val output : String) {}

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
