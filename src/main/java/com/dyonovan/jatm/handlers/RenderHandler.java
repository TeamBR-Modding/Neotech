package com.dyonovan.jatm.handlers;

import com.dyonovan.jatm.common.blocks.BlockMachine;
import com.dyonovan.jatm.common.items.BaseItem;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;

public class RenderHandler {

    public static void init() {
        //Items
        register(ItemHandler.dustIron);
        register(ItemHandler.dustGold);
    }

    public static void register(Item item) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                .register(item, 0, new ModelResourceLocation(Constants.MODID + ":" +
                        ((BaseItem) item).getName(), "inventory"));
    }
}
