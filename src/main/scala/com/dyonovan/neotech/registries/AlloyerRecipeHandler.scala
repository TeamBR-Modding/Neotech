package com.dyonovan.neotech.registries

import java.util

import com.dyonovan.neotech.NeoTech
import com.google.gson.reflect.TypeToken
import com.teambr.bookshelf.helper.LogHelper
import net.minecraft.command.CommandBase
import net.minecraftforge.fluids.FluidStack

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/20/2016
  */
class AlloyerRecipeHandler extends AbstractRecipeHandler[AlloyerRecipe, (FluidStack, FluidStack), FluidStack] {

    /**
      * Used to get the base name of the files
      *
      * @return
      */
    override def getBaseName: String = "alloyer"

    /**
      * Used to get the default folder location
      *
      * @return
      */
    override def getBaseFolderLocation: String = NeoTech.configFolderLocation

    /**
      * This is the current version of the registry, if you update this it will cause the registry to be redone
      *
      * @return
      */
    override def getVersion: Int = 0

    /**
      * Called when the file is not found, add all default recipes here
      */
    override def generateDefaultRecipes(): Unit = {
        LogHelper.info("Loading Default Alloyer Recipes...")
        addRecipe(new AlloyerRecipe("lava:1000", "water:1000", "iron:1000"))
        saveToFile()
    }

    /**
      * Get the command to add values to the registry
      *
      * @return A new command
      */
    override def getCommand: CommandBase = null

    /**
      * Used to get what type token to read from file (Generics don't handle well)
      *
      * @return
      */
    override def getTypeToken: TypeToken[util.ArrayList[AlloyerRecipe]] =
        new TypeToken[util.ArrayList[AlloyerRecipe]]() {}
}

class AlloyerRecipe(val fluidOne : String, val fluidTwo : String, val fluidOut : String)
    extends AbstractRecipe[(FluidStack, FluidStack), FluidStack] {
    /**
      * Used to get the output of this recipe
      *
      * @param input The input object
      * @return The output object
      */
    override def getOutput(input: (FluidStack, FluidStack)): Option[FluidStack] = {
        if(isValidInput(input)) {
            Option(getFluidFromString(fluidOut))
        }

        None
    }

    /**
      * Is the input valid for an output
      *
      * @param input The input object
      * @return True if there is an output
      */
    override def isValidInput(input: (FluidStack, FluidStack)): Boolean = {
        if(input._1.getFluid == null || input._2.getFluid == null)
            return false
        (fluidOne.equalsIgnoreCase(input._1.getFluid.getName) && fluidTwo.equalsIgnoreCase(input._2.getFluid.getName) &&
                getFluidFromString(fluidOne).amount >= input._1.amount && getFluidFromString(fluidTwo).amount >= input._2.amount) ||
                (fluidOne.equalsIgnoreCase(input._2.getFluid.getName) && fluidTwo.equalsIgnoreCase(input._1.getFluid.getName) &&
                        getFluidFromString(fluidOne).amount >= input._2.amount && getFluidFromString(fluidTwo).amount >= input._1.amount)
    }
}
