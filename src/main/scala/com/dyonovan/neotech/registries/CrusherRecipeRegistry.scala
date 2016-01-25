package com.dyonovan.neotech.registries

import java.io.File
import java.util

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.collections.CrusherRecipes
import com.dyonovan.neotech.managers.ItemManager
import com.google.gson.reflect.TypeToken
import com.teambr.bookshelf.helper.LogHelper
import com.teambr.bookshelf.util.JsonUtils
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.OreDictionary

import scala.collection.JavaConversions._

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 12, 2015
 */
object CrusherRecipeRegistry {

    var crusherRecipes = new util.ArrayList[CrusherRecipes]()

    /**
     * Add the values
     */
    def init(): Unit = {
        if (!loadFromFile)
            generateDefaults()
        else
            LogHelper.info("Crusher Recipes loaded successfully")
    }

    /**
     * Load the values from the file
      *
      * @return True if successful
     */
    def loadFromFile(): Boolean = {
        LogHelper.info("Loading Crusher Recipes...")
        crusherRecipes = JsonUtils.readFromJson[util.ArrayList[CrusherRecipes]](new TypeToken[util.ArrayList[CrusherRecipes]]() {
        }, NeoTech.configFolderLocation + File.separator + "Registries" + File.separator + "crusherRecipes.json")
        if (crusherRecipes == null)
            crusherRecipes = new util.ArrayList[CrusherRecipes]()
        crusherRecipes.nonEmpty
    }

    /**
     * Save the current registry to a file
     */
    def saveToFile(): Unit = {
        if (crusherRecipes.nonEmpty) JsonUtils.writeToJson(crusherRecipes, NeoTech.configFolderLocation +
                File.separator + "Registries" + File.separator + "crusherRecipes.json")
    }

    /**
     * Used to generate the default values
     */
    def generateDefaults(): Unit = {
        LogHelper.info("Json not found. Creating Dynamic Crusher Recipe List...")

        crusherRecipes.add(new CrusherRecipes("oreRedstone",
            getItemStackString(new ItemStack(Items.redstone)), 12, getItemStackString(new ItemStack(Items.redstone)), 20))
        crusherRecipes.add(new CrusherRecipes("oreLapis",
            getItemStackString(new ItemStack(Items.dye, 1, 4)), 8, getItemStackString(new ItemStack(Items.dye, 1, 4)), 20))
        crusherRecipes.add(new CrusherRecipes(getOreDict(new ItemStack(Items.blaze_rod)),
            getItemStackString(new ItemStack(Items.blaze_powder)), 4, getItemStackString(new ItemStack(Items.blaze_powder)), 15))
        crusherRecipes.add(new CrusherRecipes("cobblestone",
            getItemStackString(new ItemStack(Blocks.sand)), 1, getItemStackString(new ItemStack(Blocks.gravel)), 10))
        crusherRecipes.add(new CrusherRecipes(getOreDict(new ItemStack(Items.bone)),
            getItemStackString(new ItemStack(Items.dye, 1, 15)), 8, getItemStackString(new ItemStack(Items.dye, 1, 15)), 10))
        crusherRecipes.add(new CrusherRecipes("oreQuartz",
            getItemStackString(new ItemStack(Items.quartz)), 3, getItemStackString(new ItemStack(Items.quartz)), 50))
        crusherRecipes.add(new CrusherRecipes(getOreDict(new ItemStack(Blocks.clay)),
            getItemStackString(new ItemStack(Items.clay_ball)), 4, "", 0))
        crusherRecipes.add(new CrusherRecipes("oreDiamond",
            getItemStackString(new ItemStack(Items.diamond)), 2, getItemStackString(new ItemStack(Items.diamond)), 5))
        crusherRecipes.add(new CrusherRecipes("oreEmerald",
            getItemStackString(new ItemStack(Items.emerald)), 2, getItemStackString(new ItemStack(Items.emerald)), 5))
        crusherRecipes.add(new CrusherRecipes("glowstone",
            getItemStackString(new ItemStack(Items.glowstone_dust)), 4, getItemStackString(new ItemStack(Items.glowstone_dust)), 5))
        crusherRecipes.add(new CrusherRecipes("oreCoal",
            getItemStackString(new ItemStack(Items.coal, 1, 0)), 3, getItemStackString(new ItemStack(Items.diamond, 1, 0)), 1))
        crusherRecipes.add(new CrusherRecipes("minecraft:wool:" + OreDictionary.WILDCARD_VALUE ,
            getItemStackString(new ItemStack(Items.string)), 4, "", 0))
        crusherRecipes.add(new CrusherRecipes("blockGlass",
            getItemStackString(new ItemStack(Blocks.sand)), 1, "", 0))
        crusherRecipes.add(new CrusherRecipes(getOreDict(new ItemStack(Blocks.gravel)),
            getItemStackString(new ItemStack(Items.flint)), 2, getItemStackString(new ItemStack(Items.flint)), 10))

        val oreDict = OreDictionary.getOreNames

        for (i <- oreDict) {
            if (i.startsWith("dust")) {
                val oreList = OreDictionary.getOres(i.replaceFirst("dust", "ore"))
                if (!oreList.isEmpty) {
                    val itemList = OreDictionary.getOres(i)
                    if (itemList.size() > 0 && !doesExist(i.replaceFirst("dust", "ore")))
                        crusherRecipes.add(new CrusherRecipes(i.replaceFirst("dust", "ore"),
                            getItemStackString(new ItemStack(itemList.get(0).getItem, 1,
                                itemList.get(0).getItemDamage)), 2, "", 0))
                }
            } else if (i.startsWith("ingot")) {
                val oreList = OreDictionary.getOres(i.replaceFirst("ingot", "dust"))
                if (!oreList.isEmpty && !doesExist(i.replaceFirst("ingot", "dust"))) {
                    val itemList = OreDictionary.getOres(i.replaceFirst("ingot", "dust"))
                    if (itemList.size() > 0) {
                        crusherRecipes.add(new CrusherRecipes(i, getItemStackString(
                            new ItemStack(itemList.get(0).getItem, 1, itemList.get(0).getItemDamage)), 1, "", 0))
                    }
                }
            }
        }
        for (i <- crusherRecipes) {
            i.input match {
                case "oreGold" =>
                    i.outputSecondary = getItemStackString(new ItemStack(ItemManager.dustCopper, 1))
                    i.percentChance = 15
                case "oreIron" =>
                    i.outputSecondary = getItemStackString(new ItemStack(ItemManager.dustTin, 1))
                    i.percentChance = 15
                case "oreCopper" =>
                    i.outputSecondary = getItemStackString(new ItemStack(ItemManager.dustGold, 1))
                    i.percentChance = 15
                case "oreTin" =>
                    i.outputSecondary = getItemStackString(new ItemStack(ItemManager.dustIron, 1))
                    i.percentChance = 15
                case _ =>
            }
        }

        saveToFile()
        LogHelper.info("Finished adding " + crusherRecipes.size + " Crusher Recipes")
    }

    private def doesExist(stack: String): Boolean = {
        for (i <- crusherRecipes) {
            if (stack == i.input) return true
        }
        false
    }

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

    /**
     * Get the output for an input
     */
    def getOutput(itemStack: ItemStack): Option[(ItemStack, ItemStack)] = {
        if (itemStack != null && itemStack.getItem != null) {
            for (i <- crusherRecipes) {
                val name = i.input.split(":")
                val stackOut = getItemStackFromString(i.output)
                val stackExtra = getItemStackFromString(i.outputSecondary)
                name.length match {
                    case 3 =>
                        val stackIn = getItemStackFromString(i.input)
                        if (stackIn != null && itemStack.isItemEqual(stackIn)) {
                            return Some((new ItemStack(stackOut.getItem, i.qty, stackOut.getItemDamage), stackExtra))
                        } else if (stackIn != null && stackIn.getItemDamage == OreDictionary.WILDCARD_VALUE) {
                            if (itemStack.getItem == stackIn.getItem)
                                return Some((new ItemStack(stackOut.getItem, i.qty, stackOut.getItemDamage), stackExtra))
                        }
                    case 1 =>
                        if (checkOreDict(i.input, itemStack))
                            return Some((new ItemStack(stackOut.getItem, i.qty, stackOut.getItemDamage), stackExtra))
                }
            }
        }
        None
    }

    /**
     * Checks if the item is a valid item for this registry
      *
      * @param itemStack The stack to test
     * @return True if an output exists
     */
    def isItemValid(itemStack: ItemStack): Boolean = {
        getOutput(itemStack).isDefined
    }

    /**
     * Get the oreDict tag for an item
      *
      * @param itemStack The stack to find
     * @return The string for this stack or OreDict name
     */
    def checkOreDict(oreDict: String, itemStack: ItemStack): Boolean = {
        val oreList = OreDictionary.getOres(oreDict)
        for (i <- oreList) {
            if (i.getItemDamage == OreDictionary.WILDCARD_VALUE) {
                if (i.getItem == itemStack.getItem)
                    return true
            } else if (i.isItemEqual(itemStack)) {
                return true
            }
        }
        false
    }

    private def getItemStackString(itemStack: ItemStack): String = {
        val id: GameRegistry.UniqueIdentifier = GameRegistry.findUniqueIdentifierFor(itemStack.getItem)
        id.modId + ":" + id.name + ":" + itemStack.getItemDamage
    }

    private def getItemStackFromString(item: String): ItemStack = {
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

    def getRecipes: util.ArrayList[CrusherRecipesStack] = {
        val list = new util.ArrayList[CrusherRecipesStack]
        for (i <- crusherRecipes) {
            val outStack = getItemStackFromString(i.output)
            outStack.stackSize = i.qty
            if (getItemStackFromString(i.input) == null) {
                for (j <- OreDictionary.getOres(i.input)) {

                    list.add(new CrusherRecipesStack(j, outStack, getItemStackFromString(i.outputSecondary), i.percentChance))
                }
            } else list.add(new CrusherRecipesStack(getItemStackFromString(i.input), outStack, getItemStackFromString(i.outputSecondary), i.percentChance))
        }
        list
    }

    class CrusherRecipesStack(i: ItemStack, o: ItemStack, s: ItemStack, p: Int) {
        var input = i
        var output = o
        var secondary = s
        var secPercent = p
    }

}
