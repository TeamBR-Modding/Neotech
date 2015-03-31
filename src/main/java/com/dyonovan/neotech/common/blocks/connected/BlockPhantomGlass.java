package com.dyonovan.neotech.common.blocks.connected;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockPhantomGlass extends BlockConnectedTextures {
    public BlockPhantomGlass(Material materialIn, String name, Class<? extends TileEntity> tileClass) {
        super(materialIn, name, tileClass);
        setHardness(1.5F);
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List list, Entity collidingEntity) {
        if (((collidingEntity instanceof EntityPlayer)) && (!collidingEntity.isSneaking())) {
            return;
        }
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    }

    @SideOnly(Side.CLIENT)
    public boolean isTranslucent() {
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
