package com.dyonovan.neotech.common.blocks;

import com.dyonovan.neotech.collections.CubeTextures;
import com.dyonovan.neotech.collections.DummyState;
import com.dyonovan.neotech.lib.Constants;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
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

    public enum RotationMode {
        FOUR_STATE,
        SIX_STATE,
        NONE
    }

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
        return null;
    }

    public abstract RotationMode getRotationMode();

    public List<IBlockState> generateRotatableStates() {
        List<IBlockState> states = new ArrayList<>();
        switch (getRotationMode()) {
            case SIX_STATE:
                states.add(this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.UP));
                states.add(this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.DOWN));
            case FOUR_STATE:
                states.add(this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.NORTH));
                states.add(this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.SOUTH));
                states.add(this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.EAST));
                states.add(this.getDefaultState().withProperty(PROPERTY_FACING, EnumFacing.WEST));
            case NONE:
            default:
                states.add(this.getDefaultState());
        }
        return states;
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        switch (getRotationMode()) {
            case SIX_STATE:
                return this.getDefaultState().withProperty(PROPERTY_FACING, BlockPistonBase.getFacingFromEntity(worldIn, pos, placer));
            case FOUR_STATE:
                int playerFacingDirection = (placer == null) ? 0 : MathHelper.floor_double((placer.rotationYaw / 90.0F) + 0.5D) & 3;
                EnumFacing enumfacing = EnumFacing.getHorizontal(playerFacingDirection).getOpposite();
                return this.getDefaultState().withProperty(PROPERTY_FACING, enumfacing);
            case NONE:
            default:
                return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
        }
    }

    @Override
    public int getRenderType() {
        return 3;
    }

    @Override
    protected BlockState createBlockState() {
        switch (getRotationMode()) {
            case FOUR_STATE:
            case SIX_STATE:
                return new BlockState(this, PROPERTY_FACING);
            case NONE:
            default:
                return new BlockState(this);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if(tileClass != null) {
            try {
                return tileClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return new DummyState(world, pos, this);
    }

    public void addToolTip(List<String> toolTip) {}
}
