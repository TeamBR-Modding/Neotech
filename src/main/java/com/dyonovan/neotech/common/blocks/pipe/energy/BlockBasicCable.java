package com.dyonovan.neotech.common.blocks.pipe.energy;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import com.dyonovan.neotech.common.blocks.pipe.BlockPipe;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;

public class BlockBasicCable extends BlockPipe {

    public BlockBasicCable(Material materialIn, String blockName, Class<? extends TileEntity> tileClass) {
        super(materialIn, blockName, tileClass);
        setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
        setHardness(1.0F);
    }


    public boolean isCableConnected(IBlockAccess blockaccess, BlockPos pos, EnumFacing face) {
        TileEntity te = blockaccess.getTileEntity(pos);
        return ((te instanceof IEnergyProvider && ((IEnergyProvider)te).canConnectEnergy(face)) || (te instanceof IEnergyReceiver && ((IEnergyReceiver)te).canConnectEnergy(face)));
    }

    @Override
    public float getWidth() {
        return 3.0F;
    }

    @Override
    public String getBackgroundTexture() {
        return "minecraft:blocks/wool_colored_gray";
    }
}
