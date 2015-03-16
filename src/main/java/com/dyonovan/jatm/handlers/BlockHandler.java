package com.dyonovan.jatm.handlers;

import com.dyonovan.jatm.common.blocks.BlockGenerator;
import com.dyonovan.jatm.common.tileentity.generator.TileGenerator;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockHandler {

    public static Block blockGenerator;

    public static void init() {

        blockGenerator = new BlockGenerator();
        GameRegistry.registerTileEntity(TileGenerator.class, "blockGenerator");

    }
}
