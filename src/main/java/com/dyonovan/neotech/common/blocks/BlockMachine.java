package com.dyonovan.neotech.common.blocks;

import com.dyonovan.neotech.NeoTech;
import com.dyonovan.neotech.collections.CubeTextures;
import com.dyonovan.neotech.collections.DummyState;
import com.dyonovan.neotech.common.tileentity.BaseMachine;
import com.dyonovan.neotech.lib.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockMachine extends BlockBakeable {

    private int guiID;
    private static boolean keepInventory;
    private boolean isActive;

    public BlockMachine(boolean active, String name, Class<? extends TileEntity> tileClass, int guiID) {
        super(Material.iron, name, tileClass);
        this.setUnlocalizedName(Constants.MODID + ":" + name);
        if (!active) this.setCreativeTab(active ? null : NeoTech.tabNeoTech);
        this.setHardness(1.5F);
        this.setLightLevel(active ? 1 : 0);
        this.guiID = guiID;
        this.isActive = active;
    }

    @Override
    public CubeTextures getDefaultTextures() {
        TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
        CubeTextures cubeTextures = new CubeTextures(
                map.getAtlasSprite(Constants.MODID + ":blocks/" + name + "_front"),
                map.getAtlasSprite(Constants.MODID + ":blocks/" + "machine_side"),
                map.getAtlasSprite(Constants.MODID + ":blocks/" + "machine_side"),
                map.getAtlasSprite(Constants.MODID + ":blocks/" + "machine_side"),
                map.getAtlasSprite(Constants.MODID + ":blocks/" + "machine_side"),
                map.getAtlasSprite(Constants.MODID + ":blocks/" + "machine_side")
        );
        return cubeTextures;
    }

    public static void setState(World worldIn, BlockPos pos, Block setter)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        keepInventory = true;

        worldIn.setBlockState(pos, setter.getDefaultState().withProperty(BlockBakeable.PROPERTY_FACING, iblockstate.getValue(BlockBakeable.PROPERTY_FACING)), 3);
        worldIn.setBlockState(pos, setter.getDefaultState().withProperty(BlockBakeable.PROPERTY_FACING, iblockstate.getValue(BlockBakeable.PROPERTY_FACING)), 3);

        keepInventory = false;

        if (tileentity != null)
        {
            tileentity.validate();
            worldIn.setTileEntity(pos, tileentity);
        }
        worldIn.markBlockForUpdate(pos);
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if(isActive) {
            EnumFacing enumfacing = (EnumFacing)state.getValue(BlockBakeable.PROPERTY_FACING);
            double d0 = (double)pos.getX() + 0.5D;
            double d1 = (double)pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
            double d2 = (double)pos.getZ() + 0.5D;
            double d3 = 0.52D;
            double d4 = rand.nextDouble() * 0.6D - 0.3D;

            TileEntity machine = worldIn.getTileEntity(pos);
            if(machine != null && machine instanceof BaseMachine) {
                switch (enumfacing) {
                    case WEST:
                        ((BaseMachine) machine).spawnActiveParticles(d0 - d3, d1, d2 + d4);
                        break;
                    case EAST:
                        ((BaseMachine) machine).spawnActiveParticles(d0 + d3, d1, d2 + d4);
                        break;
                    case NORTH:
                        ((BaseMachine) machine).spawnActiveParticles(d0 + d4, d1, d2 - d3);
                        break;
                    case SOUTH:
                        ((BaseMachine) machine).spawnActiveParticles(d0 + d4, d1, d2 + d3);
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);

        if (world.isRemote) return true;
        else {
            TileEntity tile =  world.getTileEntity(pos);
            if (tile != null) {
                player.openGui(NeoTech.instance, guiID, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, PROPERTY_FACING);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return new DummyState(world, pos, this);
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

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if(!keepInventory) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof IExpellable) {
                ((IExpellable) tile).expelItems();
            }
        }
        super.breakBlock(worldIn, pos, state);
    }
}
