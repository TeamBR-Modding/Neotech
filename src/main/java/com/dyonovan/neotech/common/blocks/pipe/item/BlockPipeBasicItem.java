package com.dyonovan.neotech.common.blocks.pipe.item;

import com.dyonovan.neotech.common.blocks.pipe.BlockPipe;
import com.dyonovan.neotech.common.pipe.item.PipeBasicItem;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockPipeBasicItem extends BlockPipe {
    public BlockPipeBasicItem(Material materialIn, String name, Class<? extends TileEntity> tileClass) {
        super(materialIn, name, tileClass);
        setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
        setHardness(1.0F);
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        PipeBasicItem pipe = (PipeBasicItem)worldIn.getTileEntity(pos);

    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);

        PipeBasicItem pipe = (PipeBasicItem) world.getTileEntity(pos);
            pipe.toggleExtractMode();

        return true;
    }

    @Override
    public boolean isCableConnected(IBlockAccess blockaccess, BlockPos pos, EnumFacing face) {
        return blockaccess.getTileEntity(pos) instanceof IInventory || blockaccess.getTileEntity(pos) instanceof PipeBasicItem;
    }

    @Override
    public float getWidth() {
        return 3.0F;
    }

    @Override
    public String getBackgroundTexture() {
        return "minecraft:blocks/iron_block";
    }
}
