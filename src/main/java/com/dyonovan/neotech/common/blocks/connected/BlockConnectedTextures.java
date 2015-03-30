package com.dyonovan.neotech.common.blocks.connected;

import com.dyonovan.neotech.NeoTech;
import com.dyonovan.neotech.collections.ConnectedTextures;
import com.dyonovan.neotech.common.blocks.BlockBakeable;
import com.dyonovan.neotech.lib.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockConnectedTextures extends BlockBakeable {
    public BlockConnectedTextures(Material materialIn, String name, Class<? extends TileEntity> tileClass) {
        super(materialIn, name, tileClass);
        setCreativeTab(NeoTech.tabNeoTechDeco);
        setUnlocalizedName(Constants.MODID + ":" + name);
    }

    /**
     * Need to make sure the generator stitches all our textures to the map
     */
    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation[] registerIcons() {
        return  new ResourceLocation[] {
                new ResourceLocation(String.format("%s:blocks/%s", Constants.MODID, name)),
                new ResourceLocation(String.format("%s:blocks/%s_anti_corners", Constants.MODID, name)),
                new ResourceLocation(String.format("%s:blocks/%s_corners", Constants.MODID, name)),
                new ResourceLocation(String.format("%s:blocks/%s_horizontal", Constants.MODID, name)),
                new ResourceLocation(String.format("%s:blocks/%s_vertical", Constants.MODID, name))
        };
    }

    /**
     * Build the connected texture object
     * @return {@link com.dyonovan.neotech.collections.ConnectedTextures} with appropriate textures
     */
    @SideOnly(Side.CLIENT)
    public ConnectedTextures getConnectedTextures() {
        TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
        return new ConnectedTextures(
                map.getAtlasSprite(String.format("%s:blocks/%s", Constants.MODID, name)),
                map.getAtlasSprite(String.format("%s:blocks/%s_anti_corners", Constants.MODID, name)),
                map.getAtlasSprite(String.format("%s:blocks/%s_corners", Constants.MODID, name)),
                map.getAtlasSprite(String.format("%s:blocks/%s_horizontal", Constants.MODID, name)),
                map.getAtlasSprite(String.format("%s:blocks/%s_vertical", Constants.MODID, name))
        );
    }

    /**
     * Used to tell if we should render a connection to the block
     *
     * Overwrite this in descendants if you want more control. Defaults to just testing if it is itself
     */
    public boolean canBlockConnect(Block block) {
        return block == this;
    }

    /**
     * Get the default state of the block
     *
     * For our purposes, we want the instance where no connections occur. In that situation, all corners will be rendered
     * which is why we return the corner texture
     *
     * @return Default State Texture
     */
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getDefaultStateIcon() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(String.format("%s:blocks/%s_corners", Constants.MODID, name));
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean isTranslucent() {
        return true;
    }

    public boolean isFullCube() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

}
