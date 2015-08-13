package com.dyonovan.neotech.managers

import com.dyonovan.neotech.common.items.BaseItem
import net.minecraft.init.Items
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

    var dustGold = new BaseItem("dustGold", 64)
    var dustIron = new BaseItem("dustIron", 64)

    def preInit(): Unit = {
        registerItem(dustGold, "dustGold", "dustGold")
        GameRegistry.addSmelting(dustIron, new ItemStack(Items.iron_ingot), 1.0F)
        registerItem(dustIron, "dustIron", "dustIron")
        GameRegistry.addSmelting(dustGold, new ItemStack(Items.gold_ingot), 2.0F)
    }
    /**
     * Helper method to register items
     * @param item The item to register
     * @param name The name of the item
     * @param oreDict The ore dict tag
     */
    private def registerItem(item: Item, name: String, oreDict: String) {
        GameRegistry.registerItem(item, name)
        if (oreDict != null) OreDictionary.registerOre(oreDict, item)
    }

    private def registerItem(item: Item, name: String) {
        registerItem(item, name, null)
    }
}
