package com.dyonovan.jatm.handlers;

import com.dyonovan.jatm.blocks.BlockGenerator;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;

public class BlockRenderHelper {

    public static void registerBlockRenderer() {
        register(BlockHandler.blockGenerator, ((BlockGenerator) BlockHandler.blockGenerator).getName());
    }

    public static void register(Block block, String blockName) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                .register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(Constants.MODID + ":" + blockName, "inventory"));
    }
}
