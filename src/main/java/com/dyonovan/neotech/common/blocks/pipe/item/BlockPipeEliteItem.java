package com.dyonovan.neotech.common.blocks.pipe.item;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;

public class BlockPipeEliteItem extends BlockPipeBasicItem {
    public BlockPipeEliteItem(Material materialIn, String name, Class<? extends TileEntity> tileClass) {
        super(materialIn, name, tileClass);
    }

    @Override
    public String getBackgroundTexture() {
        return "minecraft:blocks/gold_block";
    }
}
