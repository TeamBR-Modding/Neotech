package com.dyonovan.neotech.managers

import com.dyonovan.neotech.common.items._
import com.dyonovan.neotech.tools.tools.ElectricPickaxe
import net.minecraft.item.{ItemStack, Item}
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
 * @since August 12, 2015
 */
object ItemManager {

    //Upgrade System
    val upgradeMBEmpty = new BaseUpgradeItem("upgradeMBEmpty", 1, true)
    val upgradeMBFull = new BaseUpgradeItem("upgradeMBFull", 1, false)
    val upgradeHardDrive = new BaseUpgradeItem("upgradeHardDrive", 8, true)
    val upgradeControl = new BaseUpgradeItem("upgradeControl", 1, true)
    val upgradeProcessor = new BaseUpgradeItem("upgradeProcessor", 8, true)
    val upgradeExpansion = new BaseUpgradeItem("upgradeExpansion", 1, true)

    //Utils
    val wrench = new ItemWrench
    val trashBag = new ItemTrashBag
    val spawnerMover = new ItemSpawnerMover
    val mobGun = new ItemMobGun
    val mobNet = new ItemMobNet
    val electroMagnet = new ItemElectromagnet

    val basicRFBattery = new RFBattery("basicRFBattery", 1)
    val advancedRFBattery = new RFBattery("advancedRFBattery", 2)
    val eliteRFBattery = new RFBattery("eliteRFBattery", 3)

    //Electric Tools
    val electricPickaxe = new ElectricPickaxe

    def preInit(): Unit = {
        registerItem(upgradeMBEmpty, "upgradeMBEmpty")
        registerItem(upgradeMBFull, "upgradeMBFull")
        registerItem(upgradeHardDrive, "upgradeHardDrive")
        registerItem(upgradeControl, "upgradeControl")
        registerItem(upgradeProcessor, "upgradeProcessor")
        registerItem(upgradeExpansion, "upgradeExpansion")

        registerItem(wrench, "wrench")
        registerItem(trashBag, "trashBag")
        registerItem(spawnerMover, "spawnerMover")
        registerItem(mobGun, "mobGun")
        registerItem(mobNet, "mobNet")
        registerItem(electroMagnet, "electroMagnet")

        registerItem(basicRFBattery, "basicRFBattery", "rfBattery", OreDictionary.WILDCARD_VALUE)
        registerItem(advancedRFBattery, "advancedRFBattery", "rfBattery", OreDictionary.WILDCARD_VALUE)
        registerItem(eliteRFBattery, "eliteRFBattery", "rfBattery", OreDictionary.WILDCARD_VALUE)

        registerItem(electricPickaxe, "electricPickaxe")
    }
    /**
     * Helper method to register items
     *
     * @param item The item to register
     * @param name The name of the item
     * @param oreDict The ore dict tag
     */
    def registerItem(item: Item, name: String, oreDict: String) : Item = {
        GameRegistry.registerItem(item, name)
        if (oreDict != null) OreDictionary.registerOre(oreDict, item)
        item
    }

    def registerItem(item: Item, name: String) : Item = {
        registerItem(item, name, null)
    }

    def registerItem(item: Item, name: String, oreDict: String, itemDamage: Int) : Item = {
        GameRegistry.registerItem(item, name)
        if (oreDict != null) OreDictionary.registerOre(oreDict, new ItemStack(item, 1, itemDamage))
        item
    }
}
