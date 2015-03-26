package com.dyonovan.neotech.common.blocks.pipe.energy;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;

public class BlockEliteCable extends BlockBasicCable {
    public BlockEliteCable(Material materialIn, String blockName, Class<? extends TileEntity> tileClass) {
        super(materialIn, blockName, tileClass);
    }

    @Override
    public String getBackgroundTexture() {
        return "minecraft:blocks/gold_block";
    }
}
