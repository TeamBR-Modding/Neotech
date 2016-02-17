package com.dyonovan.neotech.registries

import java.io.File
import java.util

import com.dyonovan.neotech.NeoTech
import com.google.gson.reflect.TypeToken
import com.teambr.bookshelf.helper.LogHelper
import com.teambr.bookshelf.util.JsonUtils
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
            LogHelper.info("Heater Recipes loaded successfully")
    }

    /**
      * Load the values from the file
      *
      * @return True if successful
      */
    def loadFromFile(): Boolean = {
        LogHelper.info("Loading Heater Recipes...")
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
        LogHelper.info("Json not found. Creating Dynamic Heater Recipe List...")
    }

    /**
      * Get the output of this itemstack
      * @param input The Input
      * @return The FluidStack returned, None if non existent
      */
    def getOutput(input : ItemStack) : Option[FluidStack] = {
        //Check registered
        for(recipe : CrucibleRecipe <- crucibleRecipes.toArray.asInstanceOf[Array[CrucibleRecipe]]) {
            if(recipe.input._1.getIsItemStackEqual(input) || (
                    if(recipe.input._2 != null && OreDictionary.getOreIDs(input) != null)
                        OreDictionary.getOreIDs(input).toList.contains(OreDictionary.getOreID(recipe.input._2))
                    else
                        false))
                return Some(recipe.output)
        }

        None
    }

    /**
      * Helper class for holding recipes
      * @param input The input tuple, first is stack in second is or dict tag (can be null)
      * @param output The Fluidstack returned
      */
    class CrucibleRecipe(val input : (ItemStack, String), val output : FluidStack)
}
