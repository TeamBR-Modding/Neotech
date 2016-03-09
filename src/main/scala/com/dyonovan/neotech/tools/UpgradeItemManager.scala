package com.dyonovan.neotech.tools

import com.dyonovan.neotech.managers.ItemManager
import com.dyonovan.neotech.tools.modifier.ModifierAOE.ItemModifierAOE
import com.dyonovan.neotech.tools.modifier.ModifierBaneOfArthropods.ItemModifierBaneOfArthropods
import com.dyonovan.neotech.tools.modifier.ModifierBeheading.ItemModifierBeheading
import com.dyonovan.neotech.tools.modifier.ModifierFallResist.ItemModifierFallResist
import com.dyonovan.neotech.tools.modifier.ModifierFortune.ItemModifierFortune
import com.dyonovan.neotech.tools.modifier.ModifierGlide.ItemModifierGlide
import com.dyonovan.neotech.tools.modifier.ModifierHover.ItemModifierHover
import com.dyonovan.neotech.tools.modifier.ModifierJetpack.ItemModifierJetpack
import com.dyonovan.neotech.tools.modifier.ModifierLighting.ItemModifierLighting
import com.dyonovan.neotech.tools.modifier.ModifierLooting.ItemModifierLooting
import com.dyonovan.neotech.tools.modifier.ModifierMiningLevel.ItemModifierMiningLevel
import com.dyonovan.neotech.tools.modifier.ModifierMiningSpeed.ItemModifierMiningSpeed
import com.dyonovan.neotech.tools.modifier.ModifierNightVision.ItemModifierNightVision
import com.dyonovan.neotech.tools.modifier.ModifierProtection.ItemModifierProtection
import com.dyonovan.neotech.tools.modifier.ModifierSharpness.ItemModifierSharpness
import com.dyonovan.neotech.tools.modifier.ModifierShovel.ItemModifierShovel
import com.dyonovan.neotech.tools.modifier.ModifierSilkTouch.ItemModifierSilkTouch
import com.dyonovan.neotech.tools.modifier.ModifierSmite.ItemModifierSmite
import com.dyonovan.neotech.tools.modifier.ModifierSprinting.ItemModifierSprinting
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
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierSilkTouch])), ItemManager.upgradeMBEmpty,
            Items.string, Items.writable_book)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierFortune])), ItemManager.upgradeMBEmpty,
            Items.emerald)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeMiningLevel2), ItemManager.upgradeMBEmpty,
            Items.iron_ingot)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeMiningLevel3), ItemManager.upgradeMBEmpty,
            Items.diamond)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierMiningSpeed])), ItemManager.upgradeMBEmpty,
            Items.feather, Blocks.redstone_block)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierAOE])), ItemManager.upgradeMBEmpty,
            Blocks.piston, Blocks.piston)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierShovel])), ItemManager.upgradeMBEmpty,
            Items.iron_shovel)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierSharpness])), ItemManager.upgradeMBEmpty,
            Items.flint, Items.iron_sword)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierSmite])), ItemManager.upgradeMBEmpty,
            Items.rotten_flesh)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierBeheading])), ItemManager.upgradeMBEmpty,
            new ItemStack(Items.skull, 1, OreDictionary.WILDCARD_VALUE))
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierBaneOfArthropods])), ItemManager.upgradeMBEmpty,
            Items.spider_eye)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierLighting])), ItemManager.upgradeMBEmpty,
            Items.blaze_rod, Items.blaze_rod)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierLooting])), ItemManager.upgradeMBEmpty,
            Items.spider_eye, Items.blaze_powder, Items.rotten_flesh, Items.bone, Items.gunpowder)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierJetpack])), ItemManager.upgradeMBEmpty,
            Items.feather, Items.gunpowder)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierFallResist])), ItemManager.upgradeMBEmpty,
            Blocks.slime_block)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierGlide])), ItemManager.upgradeMBEmpty,
            Items.leather, Items.feather)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierNightVision])), ItemManager.upgradeMBEmpty,
            Items.glowstone_dust, Items.glowstone_dust)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierSprinting])), ItemManager.upgradeMBEmpty,
            Items.feather, Blocks.piston)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierProtection])), ItemManager.upgradeMBEmpty,
            Blocks.iron_block, Blocks.iron_block, Blocks.iron_block, Blocks.iron_block)
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.itemRegistry.get(classOf[ItemModifierHover])), ItemManager.upgradeMBEmpty,
            Items.gunpowder, Items.gunpowder, Blocks.glass)
        if(Loader.isModLoaded("tconstruct")) {
            GameRegistry.addShapelessRecipe(new ItemStack(upgradeMiningLevel4), ItemManager.upgradeMBEmpty,
                Blocks.obsidian)
        }
    }

    def init() : Unit = {

    }

    /**
      * Used to add a crafting recipe to the upgrade, defaults to motherboard plus what is passed, can be any number of objects, just
      * better be less than 9
      */
    def addUpgradeRecipe(item : Item, craftingComponent : Object*) : Unit = {
        GameRegistry.addShapelessRecipe(new ItemStack(item), ItemManager.upgradeMBEmpty, craftingComponent.toArray)
    }
}
