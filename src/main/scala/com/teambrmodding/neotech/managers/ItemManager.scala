package com.teambrmodding.neotech.managers

import com.teambrmodding.neotech.NeoTech
import com.teambrmodding.neotech.common.items._
import com.teambrmodding.neotech.tools.UpgradeItemManager
import com.teambrmodding.neotech.tools.armor.ItemElectricArmor
import com.teambrmodding.neotech.tools.tools.{ElectricPickaxe, ElectricSword}
import com.teambr.bookshelf.Bookshelf
import com.teambr.bookshelf.helper.LogHelper
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem
import gnu.trove.map.hash.THashMap
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.fml.common.Loader
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
object ItemManager {

    lazy val itemRegistry = new THashMap[Class[_ <: Item], Item]()

    //Upgrade System
    val upgradeMBEmpty = new MotherBoardItem("upgradeMBEmpty", 1, true)
    val upgradeMBFull = new MotherBoardItem("upgradeMBFull", 1, false)
    val upgradeHardDrive = new MotherBoardUpgradeItem("upgradeHardDrive", 8, NeoTech.tabNeoTech)
    val upgradeControl = new MotherBoardUpgradeItem("upgradeControl", 1, NeoTech.tabNeoTech)
    val upgradeProcessor = new MotherBoardUpgradeItem("upgradeProcessor", 8, NeoTech.tabNeoTech)
    val upgradeExpansion = new MotherBoardUpgradeItem("upgradeExpansion", 1, NeoTech.tabNeoTech)

    val processorSingleCore = new UpgradeItem(IUpgradeItem.CPU_SINGLE_CORE, IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU, 8)
    val processorDualCore = new UpgradeItem(IUpgradeItem.CPU_DUAL_CORE, IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU, 8)
    val processorQuadCore = new UpgradeItem(IUpgradeItem.CPU_QUAD_CORE, IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU, 8)
    val processorOctCore = new UpgradeItem(IUpgradeItem.CPU_OCT_CORE, IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU, 8)

    val memoryDDR1 = new UpgradeItem(IUpgradeItem.MEMORY_DDR1, IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY, 4)
    val memoryDDR2 = new UpgradeItem(IUpgradeItem.MEMORY_DDR2, IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY, 4)
    val memoryDDR3 = new UpgradeItem(IUpgradeItem.MEMORY_DDR3, IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY, 4)
    val memoryDDR4 = new UpgradeItem(IUpgradeItem.MEMORY_DDR4, IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY, 4)

    val hardDrive64G = new UpgradeItem(IUpgradeItem.HDD_64G, IUpgradeItem.ENUM_UPGRADE_CATEGORY.HDD, 1)
    val hardDrive254G = new UpgradeItem(IUpgradeItem.HDD_256G, IUpgradeItem.ENUM_UPGRADE_CATEGORY.HDD, 1)
    val hardDrive512G = new UpgradeItem(IUpgradeItem.HDD_512G, IUpgradeItem.ENUM_UPGRADE_CATEGORY.HDD, 1)
    val hardDrive1T = new UpgradeItem(IUpgradeItem.HDD_1T, IUpgradeItem.ENUM_UPGRADE_CATEGORY.HDD, 1)

    val psu250W = new UpgradeItem(IUpgradeItem.PSU_250W, IUpgradeItem.ENUM_UPGRADE_CATEGORY.PSU, 1)
    val psu500W = new UpgradeItem(IUpgradeItem.PSU_500W, IUpgradeItem.ENUM_UPGRADE_CATEGORY.PSU, 1)
    val psu750W = new UpgradeItem(IUpgradeItem.PSU_750W, IUpgradeItem.ENUM_UPGRADE_CATEGORY.PSU, 1)
    val psu960W = new UpgradeItem(IUpgradeItem.PSU_960W, IUpgradeItem.ENUM_UPGRADE_CATEGORY.PSU, 1)

    val transformer = new UpgradeItem(IUpgradeItem.TRANSFORMER, IUpgradeItem.ENUM_UPGRADE_CATEGORY.MISC, 4)

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
    val electricSword = new ElectricSword

    //Electric Armor
    val electricArmorHelmet = new ItemElectricArmor("electricArmorHelmet", 1, EntityEquipmentSlot.HEAD)
    val electricArmorChestplate = new ItemElectricArmor("electricArmorChestplate", 1, EntityEquipmentSlot.CHEST)
    val electricArmorLeggings = new ItemElectricArmor("electricArmorLeggings", 2, EntityEquipmentSlot.LEGS)
    val electricArmorBoots = new ItemElectricArmor("electricArmorBoots", 1, EntityEquipmentSlot.FEET)

    def preInit(): Unit = {
        registerItem(upgradeMBEmpty, "upgradeMBEmpty")
        registerItem(upgradeMBFull, "upgradeMBFull")
        registerItem(upgradeHardDrive, "upgradeHardDrive")
        registerItem(upgradeControl, "upgradeControl")
        registerItem(upgradeProcessor, "upgradeProcessor")
        registerItem(upgradeExpansion, "upgradeExpansion")

        registerItem(processorSingleCore, "processorSingleCore")
        registerItem(processorDualCore, "processorDualCore")
        registerItem(processorQuadCore, "processorQuadCore")
        registerItem(processorOctCore, "processorOctCore")

        registerItem(memoryDDR1, "memoryDDR1")
        registerItem(memoryDDR2, "memoryDDR2")
        registerItem(memoryDDR3, "memoryDDR3")
        registerItem(memoryDDR4, "memoryDDR4")

        registerItem(hardDrive64G, "hardDrive64G")
        registerItem(hardDrive254G, "hardDrive254G")
        registerItem(hardDrive512G, "hardDrive512G")
        registerItem(hardDrive1T, "hardDrive1T")

        registerItem(psu250W, "psu250W")
        registerItem(psu500W, "psu500W")
        registerItem(psu750W, "psu750W")
        registerItem(psu960W, "psu960W")

        // Check for IC2
        if(Loader.isModLoaded("IC2"))
            registerItem(transformer, "transformer")

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
        registerItem(electricSword, "electricSword")

        registerItem(electricArmorHelmet, "electricArmorHelmet")
        registerItem(electricArmorChestplate, "electricArmorChestplate")
        registerItem(electricArmorLeggings, "electricArmorLeggings")
        registerItem(electricArmorBoots, "electricArmorBoots")

        for (data <- Bookshelf.itemsToRegister) {
            if (data.getAnnotationInfo.get("modid") != null &&
                    data.getAnnotationInfo.get("modid").equals("neotech")) {
                try {
                    val asmClass = Class.forName(data.getClassName)
                    val itemClass = asmClass.asSubclass(classOf[Item])

                    val modItem = itemClass.newInstance()

                    GameRegistry.registerItem(modItem, modItem.getUnlocalizedName.split(":")(1))
                    itemRegistry.put(itemClass, modItem)
                } catch {
                    case e: Exception =>
                        LogHelper.severe(String.format("Could not register item class %s", data.getClassName))
                }
            }
        }

        UpgradeItemManager.preInit()
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
