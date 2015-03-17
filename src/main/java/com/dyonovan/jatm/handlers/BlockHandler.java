package com.dyonovan.jatm.handlers;

import com.dyonovan.jatm.common.blocks.BlockMachine;
import com.dyonovan.jatm.common.tileentity.generator.TileGenerator;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockHandler {

    public static Block blockGenerator;

    public static void init() {

        registerBlock(blockGenerator = new BlockMachine("blockGenerator", TileGenerator.class, GuiHandler.GENERATOR_GUI_ID),
                "blockGenerator", TileGenerator.class);



    }

    public static void registerBlock(Block block, String name, Class<? extends TileEntity> tileEntity) {
        GameRegistry.registerBlock(block, name);
        GameRegistry.registerTileEntity(tileEntity, name);
    }
}
