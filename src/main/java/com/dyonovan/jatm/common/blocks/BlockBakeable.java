package com.dyonovan.jatm.common.blocks;

import com.dyonovan.jatm.lib.Constants;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public abstract class BlockBakeable extends BlockContainer {
    public static final PropertyDirection PROPERTY_FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
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
    public ResourceLocation getFrontIcon() {
        return new ResourceLocation(Constants.MODID, "blocks/" + name + "_front");
    }

    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getSide() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Constants.MODID + ":block/" + "machine_side");
    }
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getNormal() {
        return new ModelResourceLocation(Constants.MODID + ":" + name, "normal");
    }

    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getInventory() {
        return new ModelResourceLocation(Constants.MODID + ":" + name, "inventory");
    }

    public List<IBlockState> generateFourDirectionStates() {
        List<IBlockState> states = new ArrayList<>();
        states.add(this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.NORTH));
        states.add(this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.SOUTH));
        states.add(this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.EAST));
        states.add(this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.WEST));
        return states;
    }

    public EnumFacing fourStateToEnum(IBlockState state) {
        if(state == this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.NORTH))
            return EnumFacing.NORTH;
        if(state == this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.SOUTH))
            return EnumFacing.SOUTH;
        if(state == this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.EAST))
            return EnumFacing.EAST;
        if(state == this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.WEST))
            return EnumFacing.WEST;
        return EnumFacing.NORTH;
    }

    @Override
    public int getRenderType() {
        return 3;
    }

    public TileEntity createNewTileEntity(World worldIn, int meta) {
        try {
            return tileClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
