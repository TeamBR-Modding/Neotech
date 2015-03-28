package com.dyonovan.neotech.handlers;

import com.dyonovan.neotech.common.items.BaseItem;
import com.dyonovan.neotech.lib.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;

public class RenderHandler {

    public static void init() {
        //Items
        register(ItemHandler.dustIron);
        register(ItemHandler.dustGold);
        register(ItemHandler.dustCopper);
        register(ItemHandler.ingotCopper);
        register(ItemHandler.dustTin);
        register(ItemHandler.ingotTin);
        register(ItemHandler.speedProcessor);
        register(ItemHandler.capRam);
        register(ItemHandler.effFan);
        register(ItemHandler.ioPort);
        register(ItemHandler.upgradeMB);
        register(ItemHandler.upgradeMBFull);
    }

    public static void register(Item item) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                .register(item, 0, new ModelResourceLocation(Constants.MODID + ":" +
                        ((BaseItem) item).getName(), "inventory"));
    }
}
