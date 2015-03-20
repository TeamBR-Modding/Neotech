package com.dyonovan.jatm.client.modelfactory;

import com.dyonovan.jatm.common.blocks.BlockBakeable;
import com.google.common.primitives.Ints;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomModel implements IBakedModel, ISmartBlockModel, ISmartItemModel
{
    private final TextureAtlasSprite base, overlay;
    private boolean hasStateSet = false;
    private final IExtendedBlockState state;

    public CustomModel(TextureAtlasSprite base, TextureAtlasSprite overlay)
    {
        this(base, overlay, null);
    }

    public CustomModel(TextureAtlasSprite base, TextureAtlasSprite overlay, IExtendedBlockState state)
    {
        this.base = base;
        this.overlay = overlay;
        this.state = state;
    }

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing side)
    {
        return Collections.emptyList();
    }

    private int[] vertexToInts(float x, float y, float z, int color, TextureAtlasSprite texture, float u, float v)
    {
        return new int[] {
                Float.floatToRawIntBits(x),
                Float.floatToRawIntBits(y),
                Float.floatToRawIntBits(z),
                color,
                Float.floatToRawIntBits(texture.getInterpolatedU(u)),
                Float.floatToRawIntBits(texture.getInterpolatedV(v)),
                0
        };
    }

    private BakedQuad createSidedBakedQuad(float x1, float x2, float z1, float z2, float y, TextureAtlasSprite texture, EnumFacing side)
    {
        Vec3 v1 = rotate(new Vec3(x1 - .5, y - .5, z1 - .5), side).addVector(.5, .5, .5);
        Vec3 v2 = rotate(new Vec3(x1 - .5, y - .5, z2 - .5), side).addVector(.5, .5, .5);
        Vec3 v3 = rotate(new Vec3(x2 - .5, y - .5, z2 - .5), side).addVector(.5, .5, .5);
        Vec3 v4 = rotate(new Vec3(x2 - .5, y - .5, z1 - .5), side).addVector(.5, .5, .5);
        return new BakedQuad(Ints.concat(
                vertexToInts((float) v1.xCoord, (float) v1.yCoord, (float) v1.zCoord, -1, texture, 0, 0),
                vertexToInts((float) v2.xCoord, (float) v2.yCoord, (float) v2.zCoord, -1, texture, 0, 16),
                vertexToInts((float) v3.xCoord, (float) v3.yCoord, (float) v3.zCoord, -1, texture, 16, 16),
                vertexToInts((float) v4.xCoord, (float) v4.yCoord, (float) v4.zCoord, -1, texture, 16, 0)
        ), -1, side);
    }

    @Override
    public List<BakedQuad> getGeneralQuads()
    {
        int len = cubeSize * 5 + 1;
        List<BakedQuad> ret = new ArrayList<BakedQuad>();
        for(EnumFacing f : EnumFacing.values())
        {
            ret.add(createSidedBakedQuad(0, 1, 0, 1, 1, base, f));
            for(int i = 0; i < cubeSize; i++)
            {
                for(int j = 0; j < cubeSize; j++)
                {
                    if(state != null)
                    {
                        Integer value = (Integer)state.getValue(properties[f.ordinal()]);
                        if(value != null && (value & (1 << (i * cubeSize + j))) != 0)
                        {
                            ret.add(createSidedBakedQuad((float)(1 + i * 5) / len, (float)(5 + i * 5) / len, (float)(1 + j * 5) / len, (float)(5 + j * 5) / len, 1.0001f, overlay, f));
                        }
                    }
                }
            }
        }
        return ret;
    }

    @Override
    public boolean isGui3d() { return true; }

    @Override
    public boolean isAmbientOcclusion() { return true; }

    @Override
    public boolean isBuiltInRenderer() { return false; }

    @Override
    public TextureAtlasSprite getTexture() { return this.base; }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public IBakedModel handleBlockState(IBlockState state)
    {
        return new CustomModel(base, overlay, (IExtendedBlockState)state);
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack)
    {
        return new CustomModel(base, overlay, null);
    }

    private static Vec3 rotate(Vec3 vec, EnumFacing side)
    {
        switch(side)
        {
            case DOWN:  return new Vec3( vec.xCoord, -vec.yCoord, -vec.zCoord);
            case UP:    return new Vec3( vec.xCoord,  vec.yCoord,  vec.zCoord);
            case NORTH: return new Vec3( vec.xCoord,  vec.zCoord, -vec.yCoord);
            case SOUTH: return new Vec3( vec.xCoord, -vec.zCoord,  vec.yCoord);
            case WEST:  return new Vec3(-vec.yCoord,  vec.xCoord,  vec.zCoord);
            case EAST:  return new Vec3( vec.yCoord, -vec.xCoord,  vec.zCoord);
        }
        return null;
    }

    private static Vec3 revRotate(Vec3 vec, EnumFacing side)
    {
        switch(side)
        {
            case DOWN:  return new Vec3( vec.xCoord, -vec.yCoord, -vec.zCoord);
            case UP:    return new Vec3( vec.xCoord,  vec.yCoord,  vec.zCoord);
            case NORTH: return new Vec3( vec.xCoord, -vec.zCoord,  vec.yCoord);
            case SOUTH: return new Vec3( vec.xCoord,  vec.zCoord, -vec.yCoord);
            case WEST:  return new Vec3( vec.yCoord, -vec.xCoord,  vec.zCoord);
            case EAST:  return new Vec3(-vec.yCoord,  vec.xCoord,  vec.zCoord);
        }
        return null;
    }

    public static final IUnlistedProperty<Integer>[] properties = new IUnlistedProperty[6];

    static {
        for(EnumFacing f : EnumFacing.values()) {
            properties[f.ordinal()] = Properties.toUnlisted(PropertyInteger.create(f.getName(), 0, 511));
        }
    }
    public static final int cubeSize = 3;
}
