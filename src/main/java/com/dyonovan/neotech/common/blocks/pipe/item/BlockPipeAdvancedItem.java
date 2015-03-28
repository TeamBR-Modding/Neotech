package com.dyonovan.neotech.common.blocks.pipe.item;

import com.dyonovan.neotech.lib.Constants;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;

public class BlockPipeAdvancedItem extends BlockPipeBasicItem {
    public BlockPipeAdvancedItem(Material materialIn, String name, Class<? extends TileEntity> tileClass) {
        super(materialIn, name, tileClass);
    }

    @Override
    public String getBackgroundTexture() {
        return "minecraft:blocks/iron_block";
    }
}
