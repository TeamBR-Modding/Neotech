package com.dyonovan.jatm.common.blocks;

import com.dyonovan.jatm.collections.CubeTextures;
import com.dyonovan.jatm.collections.DummyState;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BlockBakeable extends BlockContainer {
    public static final PropertyDirection PROPERTY_FACING = PropertyDirection.create("facing", Arrays.asList(EnumFacing.values()));
    protected String name;
    protected Class<? extends TileEntity> tileClass;

    protected BlockBakeable(Material materialIn, String name, Class<? extends TileEntity> tileClass) {
        super(materialIn);
        this.name = name;
        this.tileClass = tileClass;
    }

    public String getName() {
        return name;
    }

    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getNormal() {
        return new ModelResourceLocation(Constants.MODID + ":" + name, "normal");
    }

    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getInventory() {
        return new ModelResourceLocation(Constants.MODID + ":" + name, "inventory");
    }

    public CubeTextures getDefaultTextures() {
        TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
        CubeTextures cubeTextures = new CubeTextures();
        return cubeTextures;
    }

    public ResourceLocation[] registerIcons() {
        return new ResourceLocation[] {new ResourceLocation(Constants.MODID, "blocks/" + name + "_front"), new ResourceLocation(Constants.MODID, "blocks/machine_side")};
    }

    public List<IBlockState> generateRotatableStates() {
        List<IBlockState> states = new ArrayList<>();
        states.add(this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.NORTH));
        states.add(this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.SOUTH));
        states.add(this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.EAST));
        states.add(this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.WEST));
        states.add(this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.UP));
        states.add(this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.DOWN));
        return states;
    }

    @Override
    public int getRenderType() {
        return 3;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        try {
            return tileClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return new DummyState(world, pos, this);
    }
}
