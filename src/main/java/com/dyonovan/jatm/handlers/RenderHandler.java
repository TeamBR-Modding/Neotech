package com.dyonovan.jatm.handlers;

import com.dyonovan.jatm.common.items.BaseItem;
import com.dyonovan.jatm.lib.Constants;
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
    }

    public static void register(Item item) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                .register(item, 0, new ModelResourceLocation(Constants.MODID + ":" +
                        ((BaseItem) item).getName(), "inventory"));
    }
}
