package com.dyonovan.jatm.common.blocks.cable;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import com.dyonovan.jatm.JATM;
import com.dyonovan.jatm.collections.DummyState;
import com.dyonovan.jatm.common.blocks.BlockBakeable;
import com.dyonovan.jatm.common.tileentity.cable.TileBasicCable;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockBasicCable extends BlockBakeable {

    public BlockBasicCable(Material materialIn, String blockName) {
        super(materialIn, blockName, TileBasicCable.class);
        setCreativeTab(JATM.tabJATM);
        setUnlocalizedName(Constants.MODID + ":" + name);
        setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
        setHardness(1.0F);
    }

    public String getName() {
        return name;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        float x1 = 0.25F;
        float x2 = 1.0F - x1;
        float y1 = 0.25F;
        float y2 = 1.0F - y1;
        float z1 = 0.25F;
        float z2 = 1.0F - z1;
        if(isCableConnected(worldIn, pos.west())) {
            x1 = 0.0F;
        }

        if(isCableConnected(worldIn, pos.east())) {
            x2 = 1.0F;
        }

        if(isCableConnected(worldIn, pos.north())) {
            z1 = 0.0F;
        }

        if(isCableConnected(worldIn, pos.south())) {
            z2 = 1.0F;
        }

        if(isCableConnected(worldIn, pos.down())) {
            y1 = 0.0F;
        }

        if(isCableConnected(worldIn, pos.up())) {
            y2 = 1.0F;
        }

        this.setBlockBounds(x1, y1, z1, x2, y2, z2);
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List list, Entity collidingEntity) {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    }

    public static boolean isCableConnected(IBlockAccess blockaccess, BlockPos pos) {
        TileEntity te = blockaccess.getTileEntity(pos);
        return te instanceof IEnergyProvider || te instanceof IEnergyReceiver;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return new DummyState(world, pos, (BlockBakeable) world.getBlockState(pos).getBlock());
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isFullBlock() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean isTranslucent() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }
}
