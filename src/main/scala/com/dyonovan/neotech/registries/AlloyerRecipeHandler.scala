package com.dyonovan.neotech.registries

import java.util

import com.dyonovan.neotech.NeoTech
import com.google.gson.reflect.TypeToken
import com.teambr.bookshelf.helper.LogHelper
import net.minecraft.command.{CommandBase, ICommandSender}
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.translation.I18n
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
    override def getVersion: Int = 5

    /**
      * Called when the file is not found, add all default recipes here
      */
    override def generateDefaultRecipes(): Unit = {
        LogHelper.info("Loading Default Alloyer Recipes...")

        // Obsidian
        addRecipe(new AlloyerRecipe("water:1000", "lava:1000", "obsidian:288"))

        // Steel
        addRecipe(new AlloyerRecipe("iron:144", "carbon:144", "steel:144"))

        // Bronze
        addRecipe(new AlloyerRecipe("copper:432", "tin:144", "bronze:576"))

        saveToFile()
    }

    /**
      * Get the command to add values to the registry
      *
      * @return A new command
      */
    override def getCommand: CommandBase = {
        new CommandBase {
            override def getCommandName: String = "addAlloyRecipe"

            override def getRequiredPermissionLevel : Int = 3

            override def getCommandUsage(sender: ICommandSender): String = "commands.addAlloyRecipe.usage"

            override def execute(server: MinecraftServer, sender: ICommandSender, args: Array[String]): Unit = {
                if(args.length < 3)
                    sender.addChatMessage(new TextComponentString(I18n.translateToLocal("commands.addAlloyRecipe.usage")))
                else {
                    val input  = args(0)
                    val input2 = args(1)
                    val output = args(2)

                    if(getFluidFromString(input) != null && getFluidFromString(input2) != null && getFluidFromString(output) != null) {
                        addRecipe(new AlloyerRecipe(input, input2, output))
                        sender.addChatMessage(new TextComponentString(input + " " + input2 + " -> " + output + " Added Successfully"))
                        saveToFile()
                    } else
                        sender.addChatMessage(new TextComponentString(input + " " + input2 + " -> " + output  + " Failed Adding"))
                }
            }
        }
    }

    /**
      * Used to get what type token to read from file (Generics don't handle well)
      *
      * @return
      */
    override def getTypeToken: TypeToken[util.ArrayList[AlloyerRecipe]] =
        new TypeToken[util.ArrayList[AlloyerRecipe]]() {}

    /**
      * Used to check if a single fluid is valid for the recipes
      *
      * @param input FluidStack
      * @return Boolean
      */
    def isValidSingle(input : FluidStack) : Boolean = {
        if(input == null)
            return false
        else {
            for(x <- recipes.toArray) {
                if(x.asInstanceOf[AlloyerRecipe].isValidSingle(input))
                    return true
            }
        }
        false
    }
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
            return Option(getFluidFromString(fluidOut))
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
        if(input._1 == null || input._2 == null || input._1.getFluid == null || input._2.getFluid == null)
            return false
        (getFluidFromString(fluidOne).getFluid.getName.equalsIgnoreCase(input._1.getFluid.getName) && getFluidFromString(fluidTwo).getFluid.getName.equalsIgnoreCase(input._2.getFluid.getName) &&
                getFluidFromString(fluidOne).amount <= input._1.amount && getFluidFromString(fluidTwo).amount <= input._2.amount) ||
                (getFluidFromString(fluidOne).getFluid.getName.equalsIgnoreCase(input._2.getFluid.getName) && getFluidFromString(fluidTwo).getFluid.getName.equalsIgnoreCase(input._1.getFluid.getName) &&
                        getFluidFromString(fluidOne).amount <= input._2.amount && getFluidFromString(fluidTwo).amount <= input._1.amount)
    }

    /**
      * Used to check if a single fluid is valid for the recipes
      *
      * @param input FluidStack
      * @return Boolean
      */
    def isValidSingle(input : FluidStack) : Boolean = {
        if(input == null)
            false
        else {
            (getFluidFromString(fluidOne).getFluid.getName.equalsIgnoreCase(input.getFluid.getName) || getFluidFromString(fluidTwo).getFluid.getName.equalsIgnoreCase(input.getFluid.getName)) &&
                    (getFluidFromString(fluidOne).amount <= input.amount || getFluidFromString(fluidTwo).amount <= input.amount)
        }
    }
}
