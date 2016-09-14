package com.teambrmodding.neotech.tools

import com.teambrmodding.neotech.managers.ItemManager
import com.teambrmodding.neotech.tools.modifier.ModifierAOE.ItemModifierAOE
import com.teambrmodding.neotech.tools.modifier.ModifierBaneOfArthropods.ItemModifierBaneOfArthropods
import com.teambrmodding.neotech.tools.modifier.ModifierBeheading.ItemModifierBeheading
import com.teambrmodding.neotech.tools.modifier.ModifierFallResist.ItemModifierFallResist
import com.teambrmodding.neotech.tools.modifier.ModifierFortune.ItemModifierFortune
import com.teambrmodding.neotech.tools.modifier.ModifierGlide.ItemModifierGlide
import com.teambrmodding.neotech.tools.modifier.ModifierHover.ItemModifierHover
import com.teambrmodding.neotech.tools.modifier.ModifierJetpack.ItemModifierJetpack
import com.teambrmodding.neotech.tools.modifier.ModifierLighting.ItemModifierLighting
import com.teambrmodding.neotech.tools.modifier.ModifierLooting.ItemModifierLooting
import com.teambrmodding.neotech.tools.modifier.ModifierMiningLevel.ItemModifierMiningLevel
import com.teambrmodding.neotech.tools.modifier.ModifierMiningSpeed.ItemModifierMiningSpeed
import com.teambrmodding.neotech.tools.modifier.ModifierNightVision.ItemModifierNightVision
import com.teambrmodding.neotech.tools.modifier.ModifierProtection.ItemModifierProtection
import com.teambrmodding.neotech.tools.modifier.ModifierSharpness.ItemModifierSharpness
import com.teambrmodding.neotech.tools.modifier.ModifierShovel.ItemModifierShovel
import com.teambrmodding.neotech.tools.modifier.ModifierSilkTouch.ItemModifierSilkTouch
import com.teambrmodding.neotech.tools.modifier.ModifierSmite.ItemModifierSmite
import com.teambrmodding.neotech.tools.modifier.ModifierSprinting.ItemModifierSprinting
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.OreDictionary

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/23/2016
  */
object UpgradeItemManager {
    val upgradeMiningLevel2 = new ItemModifierMiningLevel(2)
    val upgradeMiningLevel3 = new ItemModifierMiningLevel(3)
    val upgradeMiningLevel4 = new ItemModifierMiningLevel(4)

    def preInit(): Unit = {
        ItemManager.registerItem(upgradeMiningLevel2, upgradeMiningLevel2.getUpgradeName)
        ItemManager.registerItem(upgradeMiningLevel3, upgradeMiningLevel3.getUpgradeName)

        if(Loader.isModLoaded("tconstruct")) {
            ItemManager.registerItem(upgradeMiningLevel4, upgradeMiningLevel4.getUpgradeName)
        }
    }

    def registerRecipes() : Unit = {
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierSilkTouch])), ItemManager.processorSingleCore,
            Items.STRING, Items.WRITABLE_BOOK)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierFortune])), ItemManager.processorSingleCore,
            Items.EMERALD)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeMiningLevel2), ItemManager.processorSingleCore,
            Items.IRON_INGOT)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeMiningLevel3), ItemManager.processorSingleCore,
            Items.DIAMOND)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierMiningSpeed])), ItemManager.processorSingleCore,
            Items.FEATHER, Blocks.REDSTONE_BLOCK)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierAOE])), ItemManager.processorSingleCore,
            Blocks.PISTON, Blocks.PISTON)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierShovel])), ItemManager.processorSingleCore,
            Items.IRON_SHOVEL)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierSharpness])), ItemManager.processorSingleCore,
            Items.FLINT, Items.IRON_SWORD)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierSmite])), ItemManager.processorSingleCore,
            Items.ROTTEN_FLESH)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierBeheading])), ItemManager.processorSingleCore,
            new ItemStack(Items.SKULL, 1, OreDictionary.WILDCARD_VALUE))
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierBaneOfArthropods])), ItemManager.processorSingleCore,
            Items.SPIDER_EYE)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierLighting])), ItemManager.processorSingleCore,
            Items.BLAZE_ROD, Items.BLAZE_ROD)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierLooting])), ItemManager.processorSingleCore,
            Items.SPIDER_EYE, Items.BLAZE_POWDER, Items.ROTTEN_FLESH, Items.BONE, Items.GUNPOWDER)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierJetpack])), ItemManager.processorSingleCore,
            Items.FEATHER, Items.GUNPOWDER)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierFallResist])), ItemManager.processorSingleCore,
            Blocks.SLIME_BLOCK)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierGlide])), ItemManager.processorSingleCore,
            Items.LEATHER, Items.FEATHER)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierNightVision])), ItemManager.processorSingleCore,
            Items.GLOWSTONE_DUST, Items.GLOWSTONE_DUST)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierSprinting])), ItemManager.processorSingleCore,
            Items.FEATHER, Blocks.PISTON)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierProtection])), ItemManager.processorSingleCore,
            Blocks.IRON_BLOCK, Blocks.IRON_BLOCK, Blocks.IRON_BLOCK, Blocks.IRON_BLOCK)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierHover])), ItemManager.processorSingleCore,
            Items.GUNPOWDER, Items.GUNPOWDER, Blocks.GLASS)
        if(Loader.isModLoaded("tconstruct")) {
            GameRegistry.addShapelessRecipe(new ItemStack(upgradeMiningLevel4), ItemManager.processorSingleCore,
                Blocks.OBSIDIAN)
        }
    }

    def init() : Unit = {

    }

    /**
      * Used to add a crafting recipe to the upgrade, defaults to motherboard plus what is passed, can be any number of objects, just
      * better be less than 9
      */
    def addUpgradeRecipe(item : Item, craftingComponent : Object*) : Unit = {
        GameRegistry.addShapelessRecipe(new ItemStack(item), ItemManager.processorSingleCore, craftingComponent.toArray)
    }
}
