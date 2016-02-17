package com.dyonovan.neotech.registries

import java.io.File
import java.util

import com.dyonovan.neotech.NeoTech
import com.google.gson.reflect.TypeToken
import com.teambr.bookshelf.helper.LogHelper
import com.teambr.bookshelf.util.JsonUtils
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

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
object HeaterRecipeRegistry {

    var heaterRecipes = new util.ArrayList[HeaterRecipe]()

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
        heaterRecipes = JsonUtils.readFromJson[util.ArrayList[HeaterRecipe]](new TypeToken[util.ArrayList[HeaterRecipe]]() {
        }, NeoTech.configFolderLocation + File.separator + "Registries" + File.separator + "heaterRecipes.json")
        if (heaterRecipes == null)
            heaterRecipes = new util.ArrayList[HeaterRecipe]()
        !heaterRecipes.isEmpty
    }

    /**
      * Save the current registry to a file
      */
    def saveToFile(): Unit = {
        if (!heaterRecipes.isEmpty) JsonUtils.writeToJson(heaterRecipes, NeoTech.configFolderLocation +
                File.separator + "Registries" + File.separator + "heaterRecipes.json")
    }

    /**
      * Used to generate the default values
      */
    def generateDefaults(): Unit = {
        LogHelper.info("Json not found. Creating Dynamic Heater Recipe List...")
    }

    class HeaterRecipe(val input : ItemStack, val output : FluidStack)
}
