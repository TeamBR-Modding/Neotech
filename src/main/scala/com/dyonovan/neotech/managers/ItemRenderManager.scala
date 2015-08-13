package com.dyonovan.neotech.managers

import net.minecraft.client.Minecraft
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.item.Item

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
        registerItem(ItemManager.dustGold)
        registerItem(ItemManager.dustIron)
    }

    def registerItem(item: Item): Unit = {
        Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(item, 0,
            new ModelResourceLocation(item.getUnlocalizedName.substring(5), "inventory"))
    }
}
