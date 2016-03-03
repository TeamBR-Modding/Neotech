package com.dyonovan.neotech.tools

import com.dyonovan.neotech.managers.ItemManager
import com.dyonovan.neotech.tools.modifier.ModifierAOE
import com.dyonovan.neotech.tools.upgradeitems._
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
    val upgradeSilkTouch   = new ItemModifierSilkTouch
    val upgradeFortune     = new ItemModifierFortune
    val upgradeMiningLevel2 = new ItemModifierMiningLevel(2)
    val upgradeMiningLevel3 = new ItemModifierMiningLevel(3)
    val upgradeMiningLevel4 = new ItemModifierMiningLevel(4)
    val upgradeMiningSpeed = new ItemModifierMiningSpeed
    val upgradeAOE         = new ModifierAOE.ItemModifierAOE
    val upgradeShovel      = new ItemModifierShovel
    val upgradeLighting    = new ItemModifierLighting

    val upgradeSharpness   = new ItemModifierSharpness
    val upgradeSmite       = new ItemModifierSmite
    val upgradeBeheading   = new ItemModifierBeheading
    val upgradeSpiderBane  = new ItemModifierBaneOfArthropods
    val upgradeLooting     = new ItemModifierLooting

    def preInit(): Unit = {
        ItemManager.registerItem(upgradeSilkTouch, upgradeSilkTouch.getUpgradeName)
        ItemManager.registerItem(upgradeFortune, upgradeFortune.getUpgradeName)
        ItemManager.registerItem(upgradeMiningLevel2, upgradeMiningLevel2.getUpgradeName)
        ItemManager.registerItem(upgradeMiningLevel3, upgradeMiningLevel3.getUpgradeName)
        ItemManager.registerItem(upgradeMiningSpeed, upgradeMiningSpeed.getUpgradeName)
        ItemManager.registerItem(upgradeSharpness, upgradeSharpness.getUpgradeName)
        ItemManager.registerItem(upgradeSmite, upgradeSmite.getUpgradeName)
        ItemManager.registerItem(upgradeAOE, upgradeAOE.getUpgradeName)
        ItemManager.registerItem(upgradeBeheading, upgradeBeheading.getUpgradeName)
        ItemManager.registerItem(upgradeSpiderBane, upgradeSpiderBane.getUpgradeName)
        ItemManager.registerItem(upgradeShovel, upgradeShovel.getUpgradeName)
        ItemManager.registerItem(upgradeLighting, upgradeLighting.getUpgradeName)
        ItemManager.registerItem(upgradeLooting, upgradeLooting.getUpgradeName)

        if(Loader.isModLoaded("tconstruct")) {
            ItemManager.registerItem(upgradeMiningLevel4, upgradeMiningLevel4.getUpgradeName)
        }
    }

    def registerRecipes() : Unit = {
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeSilkTouch), ItemManager.upgradeMBEmpty,
            Items.string, Items.writable_book)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeFortune), ItemManager.upgradeMBEmpty,
            Items.emerald)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeMiningLevel2), ItemManager.upgradeMBEmpty,
            Items.iron_ingot)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeMiningLevel3), ItemManager.upgradeMBEmpty,
            Items.diamond)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeMiningSpeed), ItemManager.upgradeMBEmpty,
            Items.feather, Blocks.redstone_block)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeAOE), ItemManager.upgradeMBEmpty,
            Blocks.piston, Blocks.piston)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeShovel), ItemManager.upgradeMBEmpty,
            Items.iron_shovel)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeSharpness), ItemManager.upgradeMBEmpty,
            Items.flint, Items.iron_sword)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeSmite), ItemManager.upgradeMBEmpty,
            Items.rotten_flesh)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeBeheading), ItemManager.upgradeMBEmpty,
            new ItemStack(Items.skull, 1, OreDictionary.WILDCARD_VALUE))
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeSpiderBane), ItemManager.upgradeMBEmpty,
            Items.spider_eye)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeLighting), ItemManager.upgradeMBEmpty,
            Items.blaze_rod, Items.blaze_rod)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeLooting), ItemManager.upgradeMBEmpty,
            Items.spider_eye, Items.blaze_powder, Items.rotten_flesh, Items.bone, Items.gunpowder)

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
