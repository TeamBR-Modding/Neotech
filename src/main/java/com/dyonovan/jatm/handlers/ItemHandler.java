package com.dyonovan.jatm.handlers;

import com.dyonovan.jatm.common.items.BaseItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class ItemHandler {

    public static Item dustIron, dustGold;

    public static void PreInit() {

        registerItem(dustIron = new BaseItem("dustIron", 64), "dustIron", "dustIron");
        registerItem(dustGold = new BaseItem("dustGold", 64), "dustGold", "dustGold");

    }

    public static void registerItem(Item registerItem, String name, String oreDict) {
        GameRegistry.registerItem(registerItem, name);
        if(oreDict != null)
            OreDictionary.registerOre(oreDict, registerItem);
    }
}
