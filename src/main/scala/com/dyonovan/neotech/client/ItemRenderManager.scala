package com.dyonovan.neotech.client

import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.ItemManager
import com.dyonovan.neotech.tools.UpgradeItemManager
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader

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
object ItemRenderManager {

    def registerItemRenderer(): Unit = {
        registerItem(ItemManager.upgradeMBFull)
        registerItem(ItemManager.upgradeMBEmpty)
        registerItem(ItemManager.upgradeHardDrive)
        registerItem(ItemManager.upgradeControl)
        registerItem(ItemManager.upgradeProcessor)
        registerItem(ItemManager.upgradeExpansion)
        registerItem(ItemManager.wrench)
        registerItem(ItemManager.trashBag)
        registerItem(ItemManager.spawnerMover)
        registerItem(ItemManager.mobGun)
        registerItem(ItemManager.mobNet)
        registerItem(ItemManager.electroMagnet)
        registerItem(ItemManager.basicRFBattery)
        registerItem(ItemManager.advancedRFBattery)
        registerItem(ItemManager.eliteRFBattery)

        registerItem(ItemManager.electricPickaxe)
        registerItem(UpgradeItemManager.upgradeMiningLevel2)
        registerItem(UpgradeItemManager.upgradeMiningLevel3)
        registerItem(UpgradeItemManager.upgradeMiningLevel4)
        registerItem(UpgradeItemManager.upgradeSilkTouch)
        registerItem(UpgradeItemManager.upgradeFortune)
        registerItem(UpgradeItemManager.upgradeMiningSpeed)
        registerItem(UpgradeItemManager.upgradeAOE)
        registerItem(UpgradeItemManager.upgradeShovel)
        registerItem(UpgradeItemManager.upgradeLighting)

        registerItem(ItemManager.electricSword)
        registerItem(UpgradeItemManager.upgradeSharpness)
        registerItem(UpgradeItemManager.upgradeSmite)
        registerItem(UpgradeItemManager.upgradeBeheading)
        registerItem(UpgradeItemManager.upgradeSpiderBane)
        registerItem(UpgradeItemManager.upgradeLooting)

        registerItem(ItemManager.electricArmorHelmet)
        registerItem(ItemManager.electricArmorChestplate)
        registerItem(ItemManager.electricArmorLeggings)
        registerItem(ItemManager.electricArmorBoots)
        registerItem(UpgradeItemManager.upgradeProtection)
        registerItem(UpgradeItemManager.upgradeJetpack)
        registerItem(UpgradeItemManager.upgradeFallResist)
        registerItem(UpgradeItemManager.upgradeGlide)
        registerItem(UpgradeItemManager.upgradeSprinting)
        registerItem(UpgradeItemManager.upgradeNightVision)
    }

    def registerItem(item: Item): Unit = {
        Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(item, 0,
            new ModelResourceLocation(item.getUnlocalizedName.substring(5), "inventory"))
    }

    def registerBlockModel(block : Block, name : String, variants : String, meta : Int = 0) : Unit = {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block),
            meta, new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, name), variants))
    }

    def registerItemModel(item : Item, name : String, variants : String, meta : Int = 0) : Unit = {
        ModelLoader.setCustomModelResourceLocation(item,
            meta, new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, name), variants))
    }
}
