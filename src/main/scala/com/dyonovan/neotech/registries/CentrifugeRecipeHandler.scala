package com.dyonovan.neotech.registries

import java.util

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.managers.MetalManager
import com.google.gson.reflect.TypeToken
import com.teambr.bookshelf.helper.LogHelper
import net.minecraft.command.{ICommandSender, CommandBase}
import net.minecraft.util.{StatCollector, ChatComponentText}
import net.minecraftforge.fluids.FluidStack

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/21/2016
  */
class CentrifugeRecipeHandler
        extends AbstractRecipeHandler[CentrifugeRecipe, FluidStack, (FluidStack, FluidStack)] {

    /**
      * Used to get the base name of the files
      *
      * @return
      */
    override def getBaseName: String = "centrifuge"

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
        LogHelper.info("Loading Default Alloyer Recipes...")

        // Obsidian
        addRecipe(new CentrifugeRecipe("obsidian:1296", "water:1000", "lava:1000"))

        // Steel
        addRecipe(new CentrifugeRecipe("steel:144", "iron:144", "carbon:144"))

        // Bronze
        addRecipe(new CentrifugeRecipe("bronze:576", "copper:432", "tin:144"))

        // Metals
        val iterator = MetalManager.metalRegistry.keySet().iterator()
        while(iterator.hasNext) {
            val metal = MetalManager.metalRegistry.get(iterator.next())

            if(metal.fluid.isDefined) {
                val dirtyName = "dirty" + metal.oreDict
                if(MetalManager.getMetal(dirtyName).isDefined) {
                    addRecipe(new CentrifugeRecipe(dirtyName + ":144", metal.oreDict + ":144", "lava:16"))
                }
            }
        }

        saveToFile()
    }

    /**
      * Get the command to add values to the registry
      *
      * @return A new command
      */
    override def getCommand: CommandBase = {
        new CommandBase {
            override def getCommandName: String = "addCentrifugeRecipe"

            override def getRequiredPermissionLevel : Int = 3

            override def getCommandUsage(sender: ICommandSender): String = "commands.addCentrifugeRecipe.usage"

            override def processCommand(sender: ICommandSender, args: Array[String]): Unit = {
                if(args.length < 3)
                    sender.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("commands.addCentrifugeRecipe.usage")))
                else {
                    val input  = args(0)
                    val output1 = args(1)
                    val output2 = args(2)

                    if(getFluidFromString(input) != null && getFluidFromString(output1) != null && getFluidFromString(output2) != null) {
                        addRecipe(new CentrifugeRecipe(input, output1, output2))
                        sender.addChatMessage(new ChatComponentText(input + " -> " + output1 + " " + output2 + " Added Successfully"))
                        saveToFile()
                    } else
                        sender.addChatMessage(new ChatComponentText(input + " -> " + output1 + " " + output2  + " Failed Adding"))
                }
            }
        }
    }


    /**
      * Used to get what type token to read from file (Generics don't handle well)
      *
      * @return
      */
    override def getTypeToken: TypeToken[util.ArrayList[CentrifugeRecipe]] =
        new TypeToken[util.ArrayList[CentrifugeRecipe]]() {}
}

class CentrifugeRecipe(val fluidIn : String, val fluidOne : String, val fluidTwo : String)
        extends AbstractRecipe[FluidStack, (FluidStack, FluidStack)] {
    /**
      * Used to get the output of this recipe
      *
      * @param input The input object
      * @return The output object
      */
    override def getOutput(input: FluidStack): Option[(FluidStack, FluidStack)] = {
        if(isValidInput(input))
            Option((getFluidFromString(fluidOne), getFluidFromString(fluidTwo)))
        else
            None
    }

    /**
      * Is the input valid for an output
      *
      * @param input The input object
      * @return True if there is an output
      */
    override def isValidInput(input: FluidStack): Boolean = {
        if(input == null || input.getFluid == null)
            false
        else {
            getFluidFromString(fluidIn).getFluid.getName.equalsIgnoreCase(input.getFluid.getName) &&
                    input.amount >= getFluidFromString(fluidIn).amount
        }
    }
}
