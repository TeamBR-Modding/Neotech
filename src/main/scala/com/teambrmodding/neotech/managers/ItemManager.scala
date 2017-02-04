package com.teambrmodding.neotech.managers

import com.teambrmodding.neotech.common.items._
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem
import gnu.trove.map.hash.THashMap
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
 * @since August 12, 2015
 */
object ItemManager {

    lazy val itemRegistry = new THashMap[Class[_ <: Item], Item]()

    //Upgrade System
    val processorSingleCore = new UpgradeItem(IUpgradeItem.CPU_SINGLE_CORE, IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU, 1, 2)
    val processorDualCore   = new UpgradeItem(IUpgradeItem.CPU_DUAL_CORE,  IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU, 1, 4)
    val processorQuadCore   = new UpgradeItem(IUpgradeItem.CPU_QUAD_CORE,  IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU, 1, 8)
    val processorOctCore    = new UpgradeItem(IUpgradeItem.CPU_OCT_CORE,   IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU, 1, 16)

    val memoryDDR1 = new UpgradeItem(IUpgradeItem.MEMORY_DDR1, IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY, 1, 2,  true)
    val memoryDDR2 = new UpgradeItem(IUpgradeItem.MEMORY_DDR2, IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY, 1, 4,  true)
    val memoryDDR3 = new UpgradeItem(IUpgradeItem.MEMORY_DDR3, IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY, 1, 8,  true)
    val memoryDDR4 = new UpgradeItem(IUpgradeItem.MEMORY_DDR4, IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY, 1, 16, true)

    val hardDrive64G  = new UpgradeItem(IUpgradeItem.HDD_64G,  IUpgradeItem.ENUM_UPGRADE_CATEGORY.HDD, 1, 2)
    val hardDrive254G = new UpgradeItem(IUpgradeItem.HDD_256G, IUpgradeItem.ENUM_UPGRADE_CATEGORY.HDD, 1, 4)
    val hardDrive512G = new UpgradeItem(IUpgradeItem.HDD_512G, IUpgradeItem.ENUM_UPGRADE_CATEGORY.HDD, 1, 8)
    val hardDrive1T   = new UpgradeItem(IUpgradeItem.HDD_1T,   IUpgradeItem.ENUM_UPGRADE_CATEGORY.HDD, 1, 16)

    val psu250W = new UpgradeItem(IUpgradeItem.PSU_250W, IUpgradeItem.ENUM_UPGRADE_CATEGORY.PSU, 1, 2)
    val psu500W = new UpgradeItem(IUpgradeItem.PSU_500W, IUpgradeItem.ENUM_UPGRADE_CATEGORY.PSU, 1, 4)
    val psu750W = new UpgradeItem(IUpgradeItem.PSU_750W, IUpgradeItem.ENUM_UPGRADE_CATEGORY.PSU, 1, 8)
    val psu960W = new UpgradeItem(IUpgradeItem.PSU_960W, IUpgradeItem.ENUM_UPGRADE_CATEGORY.PSU, 1, 16)

    val transformer = new UpgradeItem(IUpgradeItem.TRANSFORMER, IUpgradeItem.ENUM_UPGRADE_CATEGORY.MISC, 4, 1)

    val expansion = new UpgradeItem(IUpgradeItem.EXPANSION_CARD, IUpgradeItem.ENUM_UPGRADE_CATEGORY.MISC, 1, 1)
    val redstoneControl = new UpgradeItem(IUpgradeItem.REDSTONE_CIRCUIT, IUpgradeItem.ENUM_UPGRADE_CATEGORY.MISC, 1, 1)
    val networkCard = new UpgradeItem(IUpgradeItem.NETWORK_CARD, IUpgradeItem.ENUM_UPGRADE_CATEGORY.MISC, 1, 1)

    //Utils
    val wrench = new ItemWrench

    def preInit(): Unit = {
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

        registerItem(expansion, "expansion")
        registerItem(redstoneControl, "redstoneControl")
        registerItem(networkCard, "networkCard")

        registerItem(wrench, "wrench")
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
