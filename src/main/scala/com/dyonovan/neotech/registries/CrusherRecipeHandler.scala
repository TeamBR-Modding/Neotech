package com.dyonovan.neotech.registries

import java.util

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.managers.RecipeManager
import com.google.gson.reflect.TypeToken
import com.teambr.bookshelf.helper.LogHelper
import net.minecraft.command.CommandBase
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.OreDictionary

/**
  * This file was created for Bookshelf API
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/21/2016
  */
class CrusherRecipeHandler extends AbstractRecipeHandler[CrusherRecipes, ItemStack, (ItemStack, ItemStack, Int)] {
    /**
      * Used to get the base name of the files
      *
      * @return
      */
    override def getBaseName: String = "crusher"

    /**
      * Called when the file is not found, add all default recipes here
      */
    override def generateDefaultRecipes(): Unit = {
        LogHelper.info("Json not found. Creating Dynamic Crusher Recipe List...")

        addCrusherRecipes("oreRedstone",
            getItemStackString(new ItemStack(Items.redstone)), 12, getItemStackString(new ItemStack(Items.redstone)), 20)
        addCrusherRecipes("oreLapis",
            getItemStackString(new ItemStack(Items.dye, 1, 4)), 8, getItemStackString(new ItemStack(Items.dye, 1, 4)), 20)
        addCrusherRecipes(getOreDict(new ItemStack(Items.blaze_rod)),
            getItemStackString(new ItemStack(Items.blaze_powder)), 4, getItemStackString(new ItemStack(Items.blaze_powder)), 15)
        addCrusherRecipes("cobblestone",
            getItemStackString(new ItemStack(Blocks.sand)), 1, getItemStackString(new ItemStack(Blocks.gravel)), 10)
        addCrusherRecipes(getOreDict(new ItemStack(Items.bone)),
            getItemStackString(new ItemStack(Items.dye, 1, 15)), 8, getItemStackString(new ItemStack(Items.dye, 1, 15)), 10)
        addCrusherRecipes("oreQuartz",
            getItemStackString(new ItemStack(Items.quartz)), 3, getItemStackString(new ItemStack(Items.quartz)), 50)
        addCrusherRecipes(getOreDict(new ItemStack(Blocks.clay)),
            getItemStackString(new ItemStack(Items.clay_ball)), 4, "", 0)
        addCrusherRecipes("oreDiamond",
            getItemStackString(new ItemStack(Items.diamond)), 2, getItemStackString(new ItemStack(Items.diamond)), 5)
        addCrusherRecipes("oreEmerald",
            getItemStackString(new ItemStack(Items.emerald)), 2, getItemStackString(new ItemStack(Items.emerald)), 5)
        addCrusherRecipes("glowstone",
            getItemStackString(new ItemStack(Items.glowstone_dust)), 4, getItemStackString(new ItemStack(Items.glowstone_dust)), 5)
        addCrusherRecipes("oreCoal",
            getItemStackString(new ItemStack(Items.coal, 1, 0)), 3, getItemStackString(new ItemStack(Items.diamond, 1, 0)), 1)
        addCrusherRecipes("minecraft:wool:" + OreDictionary.WILDCARD_VALUE ,
            getItemStackString(new ItemStack(Items.string)), 4, "", 0)
        addCrusherRecipes("blockGlass",
            getItemStackString(new ItemStack(Blocks.sand)), 1, "", 0)
        addCrusherRecipes(getOreDict(new ItemStack(Blocks.gravel)),
            getItemStackString(new ItemStack(Items.flint)), 2, getItemStackString(new ItemStack(Items.flint)), 10)

        val oreDict = OreDictionary.getOreNames

        for (i <- oreDict) {
            if (i.startsWith("dust")) {
                val oreList = OreDictionary.getOres(i.replaceFirst("dust", "ore"))
                if (!oreList.isEmpty) {
                    val itemList = OreDictionary.getOres(i)
                    if (itemList.size() > 0 && !doesExist(i.replaceFirst("dust", "ore")))
                        addCrusherRecipes(i.replaceFirst("dust", "ore"),
                            getItemStackString(new ItemStack(itemList.get(0).getItem, 1,
                                itemList.get(0).getItemDamage)), 2, "", 0)
                }
            } else if (i.startsWith("ingot")) {
                val oreList = OreDictionary.getOres(i.replaceFirst("ingot", "dust"))
                if (!oreList.isEmpty && !doesExist(i.replaceFirst("ingot", "dust"))) {
                    val itemList = OreDictionary.getOres(i.replaceFirst("ingot", "dust"))
                    if (itemList.size() > 0) {
                        addCrusherRecipes(i, getItemStackString(
                            new ItemStack(itemList.get(0).getItem, 1, itemList.get(0).getItemDamage)), 1, "", 0)
                    }
                }
            }
        }
        saveToFile()
        LogHelper.info("Finished adding " + recipes.size() + " Crusher Recipes")
    }

    def addCrusherRecipes(s1: String, s2: String, i1: Int, s3: String, i2: Int) : Unit = {
        addRecipe(new CrusherRecipes(s1, s2, i1, s3, i2))
    }

    /**
      * Get the command to add values to the registry
      *
      * @return A new command
      */
    override def getCommand: CommandBase = { null }

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
      * Used to get what type token to read from file (Generics don't handle well)
      *
      * @return
      */
    override def getTypeToken: TypeToken[util.ArrayList[CrusherRecipes]] = new TypeToken[util.ArrayList[CrusherRecipes]]() {}

    /**
      * Get the oreDict tag for an item
      *
      * @param itemstack The stack to try
      * @return The string for this stack or OreDict name
      */
    private def getOreDict(itemstack: ItemStack): String = {
        val registered: Array[Int] = OreDictionary.getOreIDs(itemstack)
        if (registered.length > 0)
            OreDictionary.getOreName(registered(0))
        else {
            getItemStackString(itemstack)
        }
    }

    def doesExist(stack: String): Boolean = {
        for (i <- recipes.toArray()) {
            val recipe = i.asInstanceOf[CrusherRecipes]
            if (stack.equalsIgnoreCase(recipe.input)) return true
        }
        false
    }
}

class CrusherRecipes(val input: String, val output: String, val qty: Int, val outputSecondary: String, val percentChance: Int)
        extends AbstractRecipe[ItemStack, (ItemStack, ItemStack, Int)] {
    
    /**
      * Used to get the output of this recipe
      *
      * @param input The input object
      * @return The output object
      */
    override def getOutput(input: ItemStack): Option[(ItemStack, ItemStack, Int)] = {
        if (input != null && input.getItem != null) {
            val crusherRecipes = RecipeManager.getHandler[CrusherRecipeHandler](RecipeManager.Crusher).recipes.toArray()
            for (recipe <- crusherRecipes) {
                val i = recipe.asInstanceOf[CrusherRecipes]
                val name = i.input.split(":")
                val stackOut = getItemStackFromString(i.output)
                val stackExtra = getItemStackFromString(i.outputSecondary)
                name.length match {
                    case 3 =>
                        val stackIn = getItemStackFromString(i.input)
                        if (stackIn != null && input.isItemEqual(stackIn)) {
                            return Some((new ItemStack(stackOut.getItem, i.qty, stackOut.getItemDamage), stackExtra, i.percentChance))
                        } else if (stackIn != null && stackIn.getItemDamage == OreDictionary.WILDCARD_VALUE) {
                            if (input.getItem == stackIn.getItem)
                                return Some((new ItemStack(stackOut.getItem, i.qty, stackOut.getItemDamage), stackExtra, i.percentChance))
                        }
                    case 1 =>
                        if (checkOreDict(i.input, input))
                            return Some((new ItemStack(stackOut.getItem, i.qty, stackOut.getItemDamage), stackExtra, i.percentChance))
                }
            }
        }
        None
    }

    /**
      * Is the input valid for an output
      *
      * @param input The input object
      * @return True if there is an output
      */
    override def isValidInput(input: ItemStack): Boolean = getOutput(input).isDefined

    /**
      * Get the oreDict tag for an item
      *
      * @param itemStack The stack to find
      * @return The string for this stack or OreDict name
      */
    def checkOreDict(oreDict: String, itemStack: ItemStack): Boolean = {
        val oreList = OreDictionary.getOres(oreDict).toArray()
        for (j <- oreList) {
            val i = j.asInstanceOf[ItemStack]
            if (i.getItemDamage == OreDictionary.WILDCARD_VALUE) {
                if (i.getItem == itemStack.getItem)
                    return true
            } else if (i.isItemEqual(itemStack)) {
                return true
            }
        }
        false
    }

    override def getItemStackFromString(item: String): ItemStack = {
        if(item == null || item == "")
            return null
        val name: Array[String] = item.split(":")
        name.length match {
            case 3 =>
                new ItemStack(GameRegistry.findItem(name(0), name(1)), 1, Integer.valueOf(name(2)))
            case 1 =>
                val itemList = OreDictionary.getOres(name(0), false)
                itemList.get(0)
            case _ => null
        }
    }
}
