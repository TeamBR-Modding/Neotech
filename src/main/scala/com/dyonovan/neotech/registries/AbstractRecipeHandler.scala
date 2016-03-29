package com.dyonovan.neotech.registries

import java.io.File
import java.util

import com.google.gson.reflect.TypeToken
import com.teambr.bookshelf.helper.LogHelper
import com.teambr.bookshelf.util.JsonUtils
import net.minecraft.command.CommandBase
import net.minecraft.item.ItemStack
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fluids.{FluidRegistry, FluidStack}
import net.minecraftforge.fml.common.registry.{GameData, GameRegistry}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * R : Recipe Object, this should be what holds all the information for recipes you need
  * I : Input Object, the input for this recipe handler
  * O : Output Object, the output for this recipe handler
  *
  * @author Paul Davis <pauljoda>
  * @since 2/20/2016
  */
abstract class AbstractRecipeHandler[R <: AbstractRecipe[I, O], I, O] {

    /**
      * Holds all recipes added
      */
    var recipes = new util.ArrayList[R]()

    /**
      * Used to get the base name of the files
      *
      * @return
      */
    def getBaseName : String

    /**
      * This is the current version of the registry, if you update this it will cause the registry to be redone
      *
      * @return
      */
    def getVersion : Int

    /**
      * Used to get the default folder location
      *
      * @return
      */
    def getBaseFolderLocation : String

    /**
      * Used to get what type token to read from file (Generics don't handle well)
      *
      * @return
      */
    def getTypeToken : TypeToken[util.ArrayList[R]]

    /**
      * Get the command to add values to the registry
      *
      * @return A new command
      */
    def getCommand : CommandBase

    /**
      * Called when the file is not found, add all default recipes here
      */
    def generateDefaultRecipes() : Unit

    /**
      * Called to load the handler, it will try to load the file and if not call generateDefaultRecipes
      */
    def loadHandler() : AbstractRecipeHandler[R, I, O] = {
        checkConfig()
        if(!loadFromFile)
            generateDefaultRecipes()
        else
            LogHelper.info(getBaseName + " Recipes loaded successfully")
        this
    }

    /**
      * Load the values from the file
      *
      * @return True if successful
      */
    def loadFromFile(): Boolean = {
        LogHelper.info("Loading " + getBaseName + " Recipes...")
        recipes = JsonUtils.readFromJson[util.ArrayList[R]](getTypeToken,
            getBaseFolderLocation + File.separator + "Registries" + File.separator + getBaseName + "Recipes.json")
        if (recipes == null)
            recipes = new util.ArrayList[R]()
        !recipes.isEmpty
    }

    /**
      * Save the current registry to a file
      */
    def saveToFile(): Unit = {
        if (!recipes.isEmpty) JsonUtils.writeToJson(recipes, getBaseFolderLocation +
                File.separator + "Registries" + File.separator + getBaseName + "Recipes.json")
    }

    /**
      * Used to add a recipe to the handler
      *
      * @param recipe The Recipe object
      */
    def addRecipe(recipe : R) : Unit = recipes.add(recipe)

    /**
      * Used to get the output for the handler
      *
      * @param input Input
      * @return Output option
      */
    def getOutput(input : I) : Option[O] = {
        if(input == null) //Safety Check
            return None

        //Check registered
        for(x <- 0 until recipes.size()) {
            val recipe = recipes.get(x)
            if(recipe.getOutput(input).isDefined)
                return Option(recipe.getOutput(input).get)
        }

        None
    }

    /**
      * Used to get the recipe object for an input
      *
      * @param input The input
      * @return The recipe object
      */
    def getRecipe(input : I) : Option[R] = {
        if(input == null)
            return None

        for(x <- 0 until recipes.size()) {
            val recipe = recipes.get(x)
            if(recipe.getOutput(input).isDefined)
                return Option(recipe)
        }

        None
    }

    /**
      * Used to check if the input is valid
      *
      * @param input The input
      * @return True if valid by a recipe
      */
    def isValidInput(input : I) : Boolean = {
        if(input == null) //Safety Check
            return false

        //Check if registered
        for(x <- recipes.toArray) {
            val recipe = x.asInstanceOf[R]
            if(recipe.isValidInput(input))
                return true
        }

        false
    }

    /*******************************************************************************************************************
      ******************************************** Config Methods ******************************************************
      ******************************************************************************************************************/

    var config =
        new Configuration(new File(getBaseFolderLocation + File.separator + "Registries" + File.separator + getBaseName + "Recipes.cfg"))

    def checkConfig() : Unit = {
        config.load()

        val autoUpdate =
            config.getBoolean("autoupdate", "recipeconfig", true, "Set to false to disable auto updating")

        val lastVersion =
            config.getInt("version", "recipeconfig", getVersion, 0, Integer.MAX_VALUE, "This is the last checked version number")

        config.save() //Write values
        config.load()

        if(autoUpdate && lastVersion != getVersion) {
            val oldFile = new File( getBaseFolderLocation +
                    File.separator + "Registries" + File.separator + getBaseName + "Recipes.json")

            val newFile = new File( getBaseFolderLocation +
                    File.separator + "Registries" + File.separator + getBaseName + "RecipesOLD-" + lastVersion +".json")

            if(!newFile.exists()) {
                oldFile.renameTo(newFile)

                config.getCategory("recipeconfig").get("version").set(getVersion)
            }
        }

        config.save()
    }


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
        itemStack.getItem.getRegistryName + ":" + itemStack.getItemDamage
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
