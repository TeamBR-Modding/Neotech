package com.dyonovan.neotech.registries

import java.util

import com.dyonovan.neotech.NeoTech
import com.google.gson.reflect.TypeToken
import com.teambr.bookshelf.helper.LogHelper
import net.minecraft.command.{CommandBase, ICommandSender}
import net.minecraft.util.{StatCollector, ChatComponentText}
import net.minecraftforge.fluids.Fluid

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
class FluidFuelRecipeHandler extends AbstractRecipeHandler[FluidFuelRecipe, Fluid, Integer] {

    /**
      * Used to get the base name of the files
      *
      * @return
      */
    override def getBaseName: String = "fluidFuelValues"

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
    override def getVersion: Int = 1

    /**
      * Called when the file is not found, add all default recipes here
      */
    override def generateDefaultRecipes(): Unit = {
        addRecipe(new FluidFuelRecipe("lava", 6400))
        LogHelper.info("Fluid Fuel Values Generated!")
        saveToFile()
    }

    /**
      * Get the command to add values to the registry
      *
      * @return A new command
      */
    override def getCommand: CommandBase = {
        new CommandBase {
            override def getCommandName: String = "addFluidFuelValue"

            override def getRequiredPermissionLevel : Int = 3

            override def getCommandUsage(sender: ICommandSender): String = "commands.addFluidFuelValue.usage"

            override def processCommand(sender: ICommandSender, args: Array[String]): Unit = {
                if(args.length < 2)
                    sender.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("commands.addFluidFuelValue.usage")))
                else {
                    addRecipe(new FluidFuelRecipe(args(0), args(1).toInt))
                    sender.addChatMessage(new ChatComponentText(args(0) + " -> " + args(1) + " Added Successfully"))
                    saveToFile()
                }
            }
        }
    }

    /**
      * Used to get what type token to read from file (Generics don't handle well)
      *
      * @return
      */
    override def getTypeToken: TypeToken[util.ArrayList[FluidFuelRecipe]] =
        new TypeToken[util.ArrayList[FluidFuelRecipe]]() {}
}

class FluidFuelRecipe(val fluid : String, val burnTime : Int)
        extends AbstractRecipe[Fluid, Integer] {
    /**
      * Used to get the output of this recipe
      *
      * @param input The input object
      * @return The output object
      */
    override def getOutput(input: Fluid): Option[Integer] = {
        if(input.getName.equalsIgnoreCase(fluid))
            Option(burnTime)
        else
            None
    }

    /**
      * Is the input valid for an output
      *
      * @param input The input object
      * @return True if there is an output
      */
    override def isValidInput(input: Fluid): Boolean = input.getName.equalsIgnoreCase(fluid)
}
