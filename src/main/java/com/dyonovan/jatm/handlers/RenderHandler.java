package com.dyonovan.jatm.handlers;

import com.dyonovan.jatm.common.blocks.BlockGenerator;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;

public class RenderHandler {

    public static void init() {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

        //Blocks
        renderItem.getItemModelMesher().register(Item.getItemFromBlock(BlockHandler.blockGenerator), 0,
                new ModelResourceLocation(Constants.MODID + ":" + ((BlockGenerator) BlockHandler.blockGenerator).getName(),
                        "inventory"));
    }
}
