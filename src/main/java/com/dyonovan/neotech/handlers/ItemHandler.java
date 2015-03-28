package com.dyonovan.neotech.handlers;

import com.dyonovan.neotech.common.items.BaseItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class ItemHandler {

    public static Item dustIron, dustGold, ingotCopper, dustCopper, ingotTin, dustTin, speedProcessor, capRam;
    public static Item effFan, ioPort, upgradeMB;

    public static void PreInit() {

        registerItem(dustCopper = new BaseItem("dustCopper", 64), "dustCopper", "dustCopper");
        registerItem(dustTin = new BaseItem("dustTin", 64), "dustTin", "dustTin");
        registerItem(dustIron = new BaseItem("dustIron", 64), "dustIron", "dustIron");
        registerItem(dustGold = new BaseItem("dustGold", 64), "dustGold", "dustGold");
        registerItem(ingotCopper = new BaseItem("ingotCopper", 64), "ingotCopper", "ingotCopper");
        registerItem(ingotTin = new BaseItem("ingotTin", 64), "ingotTin", "ingotTin");
        registerItem(speedProcessor = new BaseItem("speedProcessor", 4), "speedProcessor", null);
        registerItem(capRam = new BaseItem("capRam", 4), "capRam", null);
        registerItem(effFan = new BaseItem("effFan", 4), "effFan", null);
        registerItem(ioPort = new BaseItem("ioPort", 1), "ioPort", null);
        registerItem(upgradeMB = new BaseItem("upgradeMB", 1), "upgradeMB", null);

    }

    public static void registerItem(Item registerItem, String name, String oreDict) {
        GameRegistry.registerItem(registerItem, name);
        if(oreDict != null)
            OreDictionary.registerOre(oreDict, registerItem);
    }
}
