package com.dyonovan.neotech.common.blocks;

import com.dyonovan.neotech.lib.Constants;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import scala.actors.threadpool.Arrays;

import java.util.List;

public class BlockConnectedTextures extends BlockBakeable {
    public BlockConnectedTextures(Material materialIn, String name, Class<? extends TileEntity> tileClass) {
        super(materialIn, name, tileClass);
    }

    @Override
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
     * Get the default state of the block
     *
     * For our purposes, we want the instance where no connections occur. In that situation, all corners will be rendered
     * which is why we return the corner texture
     *
     * @return Default State Texture
     */
    public TextureAtlasSprite getDefaultStateIcon() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(String.format("%s:blocks/%s_corners", Constants.MODID, name));
    }
}
