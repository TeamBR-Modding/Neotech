package com.dyonovan.jatm.common.blocks.storage;

import com.dyonovan.jatm.JATM;
import com.dyonovan.jatm.collections.CubeTextures;
import com.dyonovan.jatm.common.blocks.BlockBakeable;
import com.dyonovan.jatm.common.tileentity.storage.TileRFStorage;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockRFStorage extends BlockBakeable {

    private int guiID;
    public int tier;

    public BlockRFStorage(String name, Class<? extends TileEntity> tileClass, int guiID, int tier) {
        super(Material.iron, name, tileClass);
        this.setUnlocalizedName(Constants.MODID + ":" + name);
        this.setCreativeTab(JATM.tabJATM);
        this.setHardness(1.5F);
        this.guiID = guiID;
        this.tier = tier;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileRFStorage(tier);
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
        return this.getDefaultState().withProperty(PROPERTY_FACING, BlockPistonBase.getFacingFromEntity(worldIn, pos, placer));
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
