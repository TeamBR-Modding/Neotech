package com.dyonovan.neotech.registries

import java.util

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.managers.MetalManager
import com.google.gson.reflect.TypeToken
import com.teambr.bookshelf.helper.LogHelper
import net.minecraft.command.{ICommandSender, CommandBase}
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.ItemStack
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.translation.I18n
import net.minecraftforge.fluids.{FluidRegistry, FluidStack}
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
class SolidifierRecipeHandler extends AbstractRecipeHandler[SolidifierRecipe, FluidStack, ItemStack] {

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
    override def getVersion: Int = 4

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
            if (FluidRegistry.isFluidRegistered(metal.oreDict)) {

                //Block
                if(metal.block.isDefined)
                    addSolidifierRecipe(new FluidStack(FluidRegistry.getFluid(metal.oreDict), MetalManager.BLOCK_MB), null, metal.block.get.getName)

                //Ingot
                if(metal.ingot.isDefined)
                    addSolidifierRecipe(new FluidStack(FluidRegistry.getFluid(metal.oreDict), MetalManager.INGOT_MB), null, metal.ingot.get.getName)

                //Nugget
                if(metal.nugget.isDefined)
                    addSolidifierRecipe(new FluidStack(FluidRegistry.getFluid(metal.oreDict), MetalManager.NUGGET_MB), null, metal.nugget.get.getName)
            }
        }

        // Extend Vanilla

        // Iron
        addSolidifierRecipe(new FluidStack(FluidRegistry.getFluid("iron"), MetalManager.BLOCK_MB),
            new ItemStack(Blocks.iron_block), "blockIron")
        addSolidifierRecipe(new FluidStack(FluidRegistry.getFluid("iron"), MetalManager.INGOT_MB),
            new ItemStack(Items.iron_ingot), "ingotIron")

        // Gold
        addSolidifierRecipe(new FluidStack(FluidRegistry.getFluid("gold"), MetalManager.BLOCK_MB),
            new ItemStack(Blocks.gold_block), "blockGold")
        addSolidifierRecipe(new FluidStack(FluidRegistry.getFluid("gold"), MetalManager.INGOT_MB),
            new ItemStack(Items.gold_ingot), "ingotGold")
        addSolidifierRecipe(new FluidStack(FluidRegistry.getFluid("gold"), MetalManager.NUGGET_MB),
            new ItemStack(Items.gold_nugget), "nuggetGold")

        // Carbon
        addSolidifierRecipe(new FluidStack(FluidRegistry.getFluid("carbon"), MetalManager.INGOT_MB),
            new ItemStack(Items.coal), "")
        addSolidifierRecipe(new FluidStack(FluidRegistry.getFluid("carbon"), MetalManager.BLOCK_MB),
            new ItemStack(Blocks.coal_block), "")

        // Obsidian
        addSolidifierRecipe(new FluidStack(FluidRegistry.getFluid("obsidian"), 36),
            new ItemStack(Blocks.obsidian), "")

        saveToFile()
        LogHelper.info("Finished adding " + recipes.size + " Solidifier Recipes")
    }

    /**
      * Adds the recipe
      *
      * @param output If you set null for the itemstack, it will attempt to create one from ore dict
      * @param fluidStack FluidStack
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

    /**
      * Get the command to add values to the registry
      *
      * @return A new command
      */
    override def getCommand: CommandBase = {
        new CommandBase {
            override def getCommandName: String = "addSolidifierRecipe"

            override def getRequiredPermissionLevel : Int = 3

            override def getCommandUsage(sender: ICommandSender): String = "commands.addSolidifierRecipe.usage"

            override def execute(server: MinecraftServer, sender: ICommandSender, args: Array[String]): Unit = {
                if(args.length < 2)
                    sender.addChatMessage(new TextComponentString(I18n.translateToLocal("commands.addSolidifierRecipe.usage")))
                else {
                    var outputStack : String = null
                    if(args(1).split(":").nonEmpty) {
                        outputStack = args(1)
                        if(getItemStackFromString(outputStack) != null && getFluidFromString(args(0)) != null) {
                            addRecipe(new SolidifierRecipe(args(0), "", outputStack))
                            sender.addChatMessage(new TextComponentString(args(0) + " -> " + outputStack + " Added Successfully"))
                            saveToFile()
                        } else
                            sender.addChatMessage(new TextComponentString(args(0) + " -> " + outputStack + " Failed to be added"))
                    } else {
                        if(!OreDictionary.getOres(args(1)).isEmpty && getFluidFromString(args(0)) != null) {
                            addRecipe(new SolidifierRecipe(args(0), args(1), ""))
                            sender.addChatMessage(new TextComponentString(args(0) + " -> " + args(1) + " Added Successfully"))
                            saveToFile()
                        } else
                            sender.addChatMessage(new TextComponentString(args(0) + " -> " + outputStack + " Failed to be added"))
                    }
                }
            }
        }
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
        if(getFluidFromString(input).isFluidEqual(fluidIn) && getFluidFromString(input).amount == fluidIn.amount) {
            if(getItemStackFromString(output) != null)
                return Option(getItemStackFromString(output))
            else {
                if(ore != null && !OreDictionary.getOres(ore).isEmpty) {
                    return Option(OreDictionary.getOres(ore).get(0))
                }
            }
        }
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