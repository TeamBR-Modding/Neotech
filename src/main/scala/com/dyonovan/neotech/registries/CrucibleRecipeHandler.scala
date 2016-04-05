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
  * @since 2/16/2016
  */
class CrucibleRecipeHandler extends AbstractRecipeHandler[CrucibleRecipe, ItemStack, FluidStack] {

    /**
      * Used to get the base name of the files
      *
      * @return
      */
    override def getBaseName: String = "crucible"

    /**
      * This is the current version of the registry, if you update this it will cause the registry to be redone
      *
      * @return
      */
    override def getVersion: Int = 5

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
    override def getTypeToken: TypeToken[util.ArrayList[CrucibleRecipe]] =
        new TypeToken[util.ArrayList[CrucibleRecipe]]() {}

    /**
      * Used to generate the default values
      */
    def generateDefaultRecipes(): Unit = {
        LogHelper.info("Json not found. Creating Dynamic Crucible Recipe List...")

        // Metals
        val iterator = MetalManager.metalRegistry.keySet().iterator()
        while(iterator.hasNext) {
            val metal = MetalManager.metalRegistry.get(iterator.next())
            // Crucible Recipes
            if (FluidRegistry.isFluidRegistered(metal.oreDict)) {
                //Block - 1296mb
                if (metal.block.isDefined)
                    addCrucibleRecipe(null, metal.block.get.getName, new FluidStack(FluidRegistry.getFluid(metal.oreDict), MetalManager.BLOCK_MB))

                //Ore - 432mb
                if (metal.oreBlock.isDefined)
                    addCrucibleRecipe(null, metal.oreBlock.get.getName, new FluidStack(FluidRegistry.getFluid("dirty" + metal.oreDict), MetalManager.ORE_MB))

                //Ingot - 144mb
                if (metal.ingot.isDefined)
                    addCrucibleRecipe(null, metal.ingot.get.getName, new FluidStack(FluidRegistry.getFluid(metal.oreDict), MetalManager.INGOT_MB))

                //Dust - 76mb
                if(metal.dust.isDefined)
                    addCrucibleRecipe(null, metal.dust.get.getName, new FluidStack(FluidRegistry.getFluid(metal.oreDict), MetalManager.DUST_MB))

                //Nugget - 16mb
                if (metal.nugget.isDefined)
                    addCrucibleRecipe(null, metal.nugget.get.getName, new FluidStack(FluidRegistry.getFluid(metal.oreDict), MetalManager.NUGGET_MB))
            }
        }

        // Iron
        addCrucibleRecipe(null, "ingotIron", new FluidStack(FluidRegistry.getFluid("iron"), MetalManager.INGOT_MB))
        addCrucibleRecipe(null, "oreIron", new FluidStack(FluidRegistry.getFluid("dirtyiron"), MetalManager.ORE_MB))
        addCrucibleRecipe(null, "blockIron", new FluidStack(FluidRegistry.getFluid("iron"), MetalManager.BLOCK_MB))

        // Gold
        addCrucibleRecipe(null, "nuggetGold", new FluidStack(FluidRegistry.getFluid("gold"), MetalManager.NUGGET_MB))
        addCrucibleRecipe(null, "ingotGold", new FluidStack(FluidRegistry.getFluid("gold"), MetalManager.INGOT_MB))
        addCrucibleRecipe(null, "oreGold", new FluidStack(FluidRegistry.getFluid("dirtygold"), MetalManager.ORE_MB))
        addCrucibleRecipe(null, "blockGold", new FluidStack(FluidRegistry.getFluid("gold"), MetalManager.BLOCK_MB))

        // Carbon
        addCrucibleRecipe(new ItemStack(Items.coal, 1, 1),  "", new FluidStack(FluidRegistry.getFluid("carbon"), MetalManager.INGOT_MB * 2))
        addCrucibleRecipe(new ItemStack(Items.coal),        "", new FluidStack(FluidRegistry.getFluid("carbon"), MetalManager.INGOT_MB))
        addCrucibleRecipe(new ItemStack(Blocks.coal_block), "", new FluidStack(FluidRegistry.getFluid("carbon"), MetalManager.BLOCK_MB))

        // Obsidian
        addCrucibleRecipe(new ItemStack(Blocks.obsidian),  "", new FluidStack(FluidRegistry.getFluid("obsidian"), 36))

        // Ice/Snowball to Water
        addCrucibleRecipe(new ItemStack(Items.snowball), "", new FluidStack(FluidRegistry.WATER, 144))
        addCrucibleRecipe(new ItemStack(Blocks.ice), "", new FluidStack(FluidRegistry.WATER, 1296))
        addCrucibleRecipe(new ItemStack(Blocks.packed_ice), "", new FluidStack(FluidRegistry.WATER, 1296))

        // Stones to lava
        addCrucibleRecipe(null, "cobblestone", new FluidStack(FluidRegistry.LAVA, 20))
        addCrucibleRecipe(null, "stone", new FluidStack(FluidRegistry.LAVA, 40))

        saveToFile()
        LogHelper.info("Finished adding " + recipes.size + " Crucible Recipes")
    }

    /**
      * Adds the recipe
      *
      * @param input If you set null for the itemstack, it will attempt to create one from ore dict
      * @param fluidStack FluidStack
      */
    def addCrucibleRecipe(input : ItemStack, ore : String, fluidStack: FluidStack) : Unit = {
        var stack : ItemStack = input
        if(input == null && !ore.isEmpty) {
            val stackList = OreDictionary.getOres(ore)
            if(!stackList.isEmpty) {
                stack = stackList.get(0)
            } else {
                LogHelper.severe("Could not add ore dict crucible recipe for " + ore + " as it does not exist in the OreDictionary")
                return
            }
        }
        val recipe = new CrucibleRecipe(getItemStackString(stack), ore, getFluidString(fluidStack))
        addRecipe(recipe)
    }

    /**
      * Get the command to add values to the registry
      *
      * @return A new command
      */
    override def getCommand: CommandBase = {
        new CommandBase {
            override def getCommandName: String = "addCrucibleRecipe"

            override def getRequiredPermissionLevel : Int = 3

            override def getCommandUsage(sender: ICommandSender): String = "commands.addCrucibleRecipe.usage"

            override def execute(server: MinecraftServer, sender: ICommandSender, args: Array[String]): Unit = {
                if(args.length < 2)
                    sender.addChatMessage(new TextComponentString(I18n.translateToLocal("commands.addCrucibleRecipe.usage")))
                else {
                    var inputStack : String = null
                    if(args(0).split(":").nonEmpty) {
                        inputStack = args(0)
                        if(getItemStackFromString(inputStack) != null && getFluidFromString(args(1)) != null) {
                            addRecipe(new CrucibleRecipe(inputStack, "", args(1)))
                            sender.addChatMessage(new TextComponentString(inputStack + " -> " + args(1) + " Added Successfully"))
                            saveToFile()
                        } else
                            sender.addChatMessage(new TextComponentString(inputStack + " -> " + args(1) + " Failed Adding"))
                    } else {
                        if(!OreDictionary.getOres(args(0)).isEmpty) {
                            addRecipe(new CrucibleRecipe(null, args(0), args(1)))
                            sender.addChatMessage(new TextComponentString(args(0) + " -> " + args(1) + " Added Successfully"))
                            saveToFile()
                        } else
                            sender.addChatMessage(new TextComponentString(args(0) + " -> " + args(1) + " Failed Adding"))
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
class CrucibleRecipe(val input : String, val ore : String, val output : String) extends
        AbstractRecipe[ItemStack, FluidStack] {

    /**
      * Used to get the output of this recipe
      *
      * @param itemIn The input object
      * @return The output object
      */
    override def getOutput(itemIn: ItemStack): Option[FluidStack] = {
        if(input == null) //Safety Check
            return None

        if((getItemStackFromString(input) != null &&
                (getItemStackFromString(input).isItemEqual(itemIn) &&
                        getItemStackFromString(input).getItemDamage == itemIn.getItemDamage)) || (
                if(ore != null && OreDictionary.getOreIDs(itemIn) != null)
                    OreDictionary.getOreIDs(itemIn).toList.contains(OreDictionary.getOreID(ore))
                else
                    false))
            return Option(getFluidFromString(output))

        None
    }

    /**
      * Is the input valid for an output
      *
      * @param itemIn The input object
      * @return True if there is an output
      */
    override def isValidInput(itemIn: ItemStack): Boolean = {
        if(itemIn == null) //Safety Check
            return false

        val ourInput = getItemStackFromString(input)
        ourInput.isItemEqual(itemIn) && ourInput.getItemDamage == itemIn.getItemDamage && !itemIn.hasTagCompound
    }
}
