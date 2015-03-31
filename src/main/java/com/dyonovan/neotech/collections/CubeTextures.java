package com.dyonovan.neotech.collections;

import com.dyonovan.neotech.common.blocks.BlockBakeable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import scala.actors.threadpool.Arrays;

import java.util.List;

public class CubeTextures {
    public TextureAtlasSprite north;
    public TextureAtlasSprite south;
    public TextureAtlasSprite east;
    public TextureAtlasSprite west;
    public TextureAtlasSprite up;
    public TextureAtlasSprite down;

    public CubeTextures() {
        north = south = east = west = up = down = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/stone");
    }

    /**
     * Set the default state of the block
     * @param n Front Face
     * @param s Back Face
     * @param e Right Face
     * @param w Left Face
     * @param u Top Face
     * @param d Bottom Face
     */
    public CubeTextures(TextureAtlasSprite n, TextureAtlasSprite s, TextureAtlasSprite e, TextureAtlasSprite w, TextureAtlasSprite u, TextureAtlasSprite d) {
        north = n;
        south = s;
        east = e;
        west = w;
        down = d;
        up = u;
    }

    public CubeTextures getRotatedTextures(IBlockState state, BlockBakeable block) {
        CubeTextures rotated = new CubeTextures();
        if(state == block.getDefaultState().withProperty(BlockBakeable.PROPERTY_FACING, EnumFacing.NORTH)) {
            rotated.copy(this);
        }
        else if(state == block.getDefaultState().withProperty(BlockBakeable.PROPERTY_FACING, EnumFacing.SOUTH)) {
            rotated.south = north;
            rotated.north = south;
            rotated.east = west;
            rotated.west = east;
            rotated.up = up;
            rotated.down = down;
        }
        else if(state == block.getDefaultState().withProperty(BlockBakeable.PROPERTY_FACING, EnumFacing.EAST)) {
            rotated.north = west;
            rotated.south = east;
            rotated.east = north;
            rotated.west = south;
            rotated.up = up;
            rotated.down = down;
        }
        else if(state == block.getDefaultState().withProperty(BlockBakeable.PROPERTY_FACING, EnumFacing.WEST)) {
            rotated.north = east;
            rotated.south = west;
            rotated.east = south;
            rotated.west = north;
            rotated.up = up;
            rotated.down = down;
        }
        else if(state == block.getDefaultState().withProperty(BlockBakeable.PROPERTY_FACING, EnumFacing.UP)) {
            rotated.north = down;
            rotated.south = up;
            rotated.east = east;
            rotated.west = west;
            rotated.up = north;
            rotated.down = south;
        }
        else if(state == block.getDefaultState().withProperty(BlockBakeable.PROPERTY_FACING, EnumFacing.DOWN)) {
            rotated.north = down;
            rotated.south = up;
            rotated.east = east;
            rotated.west = west;
            rotated.up = south;
            rotated.down = north;
        }

        return rotated;
    }

    public void copy(CubeTextures cube) {
        this.north = cube.north;
        this.south = cube.south;
        this.east = cube.east;
        this.west = cube.west;
        this.down = cube.down;
        this.up = cube.up;
    }
}
