package com.dyonovan.jatm.common.blocks.pipe.energy;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;

public class BlockAdvancedCable extends BlockBasicCable {
    public BlockAdvancedCable(Material materialIn, String blockName, Class<? extends TileEntity> tileClass) {
        super(materialIn, blockName, tileClass);
    }

    @Override
    public String getBackgroundTexture() {
        return "minecraft:blocks/iron_block";
    }
}
