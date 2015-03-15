package com.dyonovan.jatm.handlers;

import com.dyonovan.jatm.blocks.BlockGenerator;
import net.minecraft.block.Block;

public class BlockHandler {

    public static Block blockGenerator;

    public static void init() {

        blockGenerator = new BlockGenerator();

    }
}
