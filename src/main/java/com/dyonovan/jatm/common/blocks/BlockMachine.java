package com.dyonovan.jatm.common.blocks;

import com.dyonovan.jatm.JATM;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BlockMachine extends BlockBakeable {

    private int guiID;

    public BlockMachine(String name, Class<? extends TileEntity> tileClass, int guiID) {
        super(Material.iron, name, tileClass);
        this.setUnlocalizedName(Constants.MODID + ":" + name);
        this.setCreativeTab(JATM.tabJATM);
        this.setHardness(1.5F);

        this.guiID = guiID;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);

        if (world.isRemote) return true;
        else {
            TileEntity tile =  world.getTileEntity(pos);
            if (tile != null) {
                player.openGui(JATM.instance, guiID, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    protected BlockState createBlockState()
    {
        return new BlockState(this, PROPERTY_FACING);
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        int playerFacingDirection = (placer == null) ? 0 : MathHelper.floor_double((placer.rotationYaw / 90.0F) + 0.5D) & 3;
        EnumFacing enumfacing = EnumFacing.getHorizontal(playerFacingDirection).getOpposite();
        return this.getDefaultState().withProperty(PROPERTY_FACING, enumfacing);
    }

    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }
        return this.getDefaultState().withProperty(PROPERTY_FACING, enumfacing);
    }

    public int getMetaFromState(IBlockState state)
    {
        return ((EnumFacing)state.getValue(PROPERTY_FACING)).getIndex();
    }
}
