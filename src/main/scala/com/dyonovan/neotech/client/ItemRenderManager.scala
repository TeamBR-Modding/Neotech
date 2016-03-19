package com.dyonovan.neotech.client

import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.ItemManager
import net.minecraft.block.Block
import net.minecraft.client.renderer.block.model.ModelResourceLocation
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
        registerItem(ItemManager.wrench)
        registerItem(ItemManager.trashBag)
        registerItem(ItemManager.spawnerMover)
        registerItem(ItemManager.mobGun)
        registerItem(ItemManager.mobNet)
        registerItem(ItemManager.electroMagnet)
        registerItem(ItemManager.electricArmorHelmet)
        registerItem(ItemManager.electricArmorChestplate)
        registerItem(ItemManager.electricArmorLeggings)
        registerItem(ItemManager.electricArmorBoots)
    }

    def registerItem(item: Item): Unit = {
        ModelLoader.setCustomModelResourceLocation(item, 0,
            new ModelResourceLocation(new ResourceLocation(item.getUnlocalizedName.substring(5)), "inventory"))
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
