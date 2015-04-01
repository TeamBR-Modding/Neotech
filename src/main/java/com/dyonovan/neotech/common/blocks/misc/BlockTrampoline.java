package com.dyonovan.neotech.common.blocks.misc;

import com.dyonovan.neotech.NeoTech;
import com.dyonovan.neotech.collections.CubeTextures;
import com.dyonovan.neotech.common.blocks.BlockBakeable;
import com.dyonovan.neotech.handlers.BlockHandler;
import com.dyonovan.neotech.lib.Constants;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class BlockTrampoline extends BlockBakeable {
    public BlockTrampoline(Material materialIn, String name, Class<? extends TileEntity> tileClass) {
        super(materialIn, name, tileClass);
        setCreativeTab(NeoTech.tabNeoTech);
        this.setUnlocalizedName(Constants.MODID + ":" + name);
        setHardness(1.0F);
    }

    @Override
    public RotationMode getRotationMode() {
        return RotationMode.NONE;
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List list, Entity collidingEntity) {
        if(collidingEntity instanceof EntityPlayer) {
            if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) || collidingEntity.isSneaking()) {
                for(int i = 1; i < 10; i++) {
                    if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) &&
                            worldIn.getBlockState(pos.offset(EnumFacing.UP, i)).getBlock() == BlockHandler.trampoline &&
                            worldIn.isAirBlock(pos.offset(EnumFacing.UP, i + 1)) &&
                            worldIn.isAirBlock(pos.offset(EnumFacing.UP, i + 2))) {
                        collidingEntity.setPosition(pos.getX() + 0.5, pos.offset(EnumFacing.UP, i).getY() + 1, pos.getZ() + 0.5);
                    } else if(collidingEntity.isSneaking() &&
                            worldIn.getBlockState(pos.offset(EnumFacing.DOWN, i)).getBlock() == BlockHandler.trampoline &&
                            worldIn.isAirBlock(pos.offset(EnumFacing.UP, i + 1)) &&
                            worldIn.isAirBlock(pos.offset(EnumFacing.UP, i + 2))) {
                        collidingEntity.setPosition(pos.getX() + 0.5,  pos.offset(EnumFacing.DOWN, i).getY() + 1, pos.getZ() + 0.5);
                    }
                }
            }
        }
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side != EnumFacing.DOWN;
    }

    /**
     * Block's chance to react to a living entity falling on it.
     *
     * @param fallDistance The distance the entity has fallen before landing
     */
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance){
        entityIn.fall(fallDistance, 0.0F);
    }
}
