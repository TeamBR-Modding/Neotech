package com.dyonovan.jatm.common.blocks;

import com.dyonovan.jatm.JATM;
import com.dyonovan.jatm.collections.CubeTextures;
import com.dyonovan.jatm.collections.DummyState;
import com.dyonovan.jatm.common.tileentity.machine.TileEntityCrafter;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BlockCrafter extends BlockBakeable {

    public BlockCrafter() {
        super(Material.wood, "crafter", TileEntityCrafter.class);
        this.setUnlocalizedName(Constants.MODID + ":" + name);
        this.setCreativeTab(JATM.tabJATM);
        this.setHardness(1.5F);
    }

    public CubeTextures getDefaultTextures() {
        TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
        return new CubeTextures(
                map.getAtlasSprite("minecraft:blocks/" + "crafting_table_front"),
                map.getAtlasSprite("minecraft:blocks/" + "crafting_table_side"),
                map.getAtlasSprite("minecraft:blocks/" + "bookshelf"),
                map.getAtlasSprite("minecraft:blocks/" + "bookshelf"),
                map.getAtlasSprite("minecraft:blocks/" + "crafting_table_top"),
                map.getAtlasSprite("minecraft:blocks/" + "log_oak_top")
        );
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
    protected BlockState createBlockState() {
        return new BlockState(this, PROPERTY_FACING);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return new DummyState(world, pos, this);
    }
}
