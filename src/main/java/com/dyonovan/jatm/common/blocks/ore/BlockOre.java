package com.dyonovan.jatm.common.blocks.ore;

import com.dyonovan.jatm.JATM;
import com.dyonovan.jatm.collections.CubeTextures;
import com.dyonovan.jatm.common.blocks.BlockBakeable;
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
import net.minecraft.world.World;

public class BlockOre extends BlockBakeable {

    public BlockOre(String name, int miningLevel) {
        super(Material.rock, name, null);
        setCreativeTab(JATM.tabJATM);
        setUnlocalizedName(Constants.MODID + ":" + name);
        setHardness(1.0F);
        setHarvestLevel("pickaxe", miningLevel);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public CubeTextures getDefaultTextures() {
        TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
        return new CubeTextures(
                map.getAtlasSprite(Constants.MODID + ":blocks/" + name + "_front"),
                map.getAtlasSprite(Constants.MODID + ":blocks/" + name + "_front"),
                map.getAtlasSprite(Constants.MODID + ":blocks/" + name + "_front"),
                map.getAtlasSprite(Constants.MODID + ":blocks/" + name + "_front"),
                map.getAtlasSprite(Constants.MODID + ":blocks/" + name + "_front"),
                map.getAtlasSprite(Constants.MODID + ":blocks/" + name + "_front")
        );
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);
        return true;
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, PROPERTY_FACING);
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(PROPERTY_FACING, BlockPistonBase.getFacingFromEntity(worldIn, pos, placer));
    }

    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }
        return this.getDefaultState().withProperty(PROPERTY_FACING, enumfacing);
    }

    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing) state.getValue(PROPERTY_FACING)).getIndex();
    }
}