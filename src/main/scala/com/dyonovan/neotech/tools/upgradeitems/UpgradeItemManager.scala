package com.dyonovan.neotech.tools.upgradeitems

import com.dyonovan.neotech.managers.ItemManager
import net.minecraft.init.{Items, Blocks}
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.fml.common.registry.GameRegistry

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
    val upgradeMiningLevel = new ItemModifierMiningLevel
    val upgradeMiningSpeed = new ItemModifierMiningSpeed
    val upgradeAOE         = new ItemModifierAOE

    val upgradeSharpness   = new ItemModifierSharpness
    val upgradeSmite       = new ItemModifierSmite
    val upgradeBeheading   = new ItemModifierBeheading

    def preInit(): Unit = {
        ItemManager.registerItem(upgradeSilkTouch, upgradeSilkTouch.getUpgradeName)
        ItemManager.registerItem(upgradeFortune, upgradeFortune.getUpgradeName)
        ItemManager.registerItem(upgradeMiningLevel, upgradeMiningLevel.getUpgradeName)
        ItemManager.registerItem(upgradeMiningSpeed, upgradeMiningSpeed.getUpgradeName)
        ItemManager.registerItem(upgradeSharpness, upgradeSharpness.getUpgradeName)
        ItemManager.registerItem(upgradeSmite, upgradeSmite.getUpgradeName)
        ItemManager.registerItem(upgradeAOE, upgradeAOE.getUpgradeName)
        ItemManager.registerItem(upgradeBeheading, upgradeBeheading.getUpgradeName)
    }

    def registerRecipes() : Unit = {
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeSilkTouch), ItemManager.upgradeMBEmpty,
            Items.string, Items.writable_book)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeFortune), ItemManager.upgradeMBEmpty,
            Items.emerald)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeMiningLevel), ItemManager.upgradeMBEmpty,
            Items.iron_pickaxe)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeMiningSpeed), ItemManager.upgradeMBEmpty,
            Items.feather, Blocks.redstone_block)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeAOE), ItemManager.upgradeMBEmpty,
            Blocks.piston, Blocks.piston)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeSharpness), ItemManager.upgradeMBEmpty,
            Items.flint, Items.iron_sword)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeSmite), ItemManager.upgradeMBEmpty,
            Items.rotten_flesh)
        GameRegistry.addShapelessRecipe(new ItemStack(upgradeBeheading), ItemManager.upgradeMBEmpty,
            Items.skull)
    }

    /**
      * Used to add a crafting recipe to the upgrade, defaults to motherboard plus what is passed, can be any number of objects, just
      * better be less than 9
      */
    def addUpgradeRecipe(item : Item, craftingComponent : Object*) : Unit = {
        GameRegistry.addShapelessRecipe(new ItemStack(item), ItemManager.upgradeMBEmpty, craftingComponent.toArray)
    }
}
