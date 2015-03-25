package com.dyonovan.jatm.client.modelfactory.models;

import com.dyonovan.jatm.collections.DummyState;
import com.dyonovan.jatm.common.blocks.pipe.BlockPipe;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings( "deprecation" )
public class ModelPipe implements ISmartBlockModel, ISmartItemModel {
    protected TextureAtlasSprite backGround = null;
    protected TextureAtlasSprite foreGround = null;
    static FaceBakery faceBakery = new FaceBakery();
    boolean[] extensions = new boolean[6];
    protected float min;
    protected float max;

    public ModelPipe(DummyState state) {
        for(EnumFacing face : EnumFacing.values()) {
            extensions[face.ordinal()] = ((BlockPipe)state.block).isCableConnected(state.blockAccess, state.pos.offset(face), face);
        }

        backGround = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(((BlockPipe)state.block).getBackgroundTexture());
        foreGround = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Constants.MODID + ":blocks/" + state.block.getName());

        min = 8.0F - ((BlockPipe)state.block).getWidth();
        max = 8.0F +  ((BlockPipe)state.block).getWidth();
    }

    public ModelPipe(BlockPipe pipe) {
        backGround = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(pipe.getBackgroundTexture());
        foreGround = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Constants.MODID + ":blocks/" + pipe.getName());

        min = 8.0F - pipe.getWidth();
        max = 8.0F + pipe.getWidth();
    }

    public List<BakedQuad> getFaceQuads(EnumFacing facing) {
        return new ArrayList<>();
    }

    private void drawPipeConnection(ModelRotation modelRot, List<BakedQuad> list) {
        BlockPartFace face = new BlockPartFace(null, 0, "", new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0));
        boolean scale = true;
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, max, max), new Vector3f(max, max, 16.0F), face, backGround, EnumFacing.UP, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(max, min, 16.0F), face, backGround, EnumFacing.DOWN, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(min, max, 16.0F), face, backGround, EnumFacing.WEST, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(max, min, max), new Vector3f(max, max, 16.0F), face, backGround, EnumFacing.EAST, modelRot, null, scale, true));
    }

    public List<BakedQuad> getGeneralQuads() {
        ArrayList list = new ArrayList();
        BlockFaceUV uv = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
        BlockPartFace face = new BlockPartFace(null, 0, "", uv);

        if(this.extensions[0]) {
            this.drawPipeConnection(ModelRotation.X270_Y0, list);
        }

        if(this.extensions[1]) {
            this.drawPipeConnection(ModelRotation.X90_Y0, list);
        }

        if(this.extensions[2]) {
            this.drawPipeConnection(ModelRotation.X180_Y0, list);
        }

        if(this.extensions[3]) {
            this.drawPipeConnection(ModelRotation.X0_Y0, list);
        }

        if(this.extensions[4]) {
            this.drawPipeConnection(ModelRotation.X0_Y90, list);
        }

        if(this.extensions[5]) {
            this.drawPipeConnection(ModelRotation.X0_Y270, list);
        }

        ModelRotation modelRot = ModelRotation.X0_Y0;
        boolean scale = true;
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, backGround, EnumFacing.DOWN, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, backGround, EnumFacing.UP, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, backGround, EnumFacing.NORTH, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, backGround, EnumFacing.SOUTH, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, backGround, EnumFacing.EAST, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, backGround, EnumFacing.WEST, modelRot, null, scale, true));

        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, foreGround, EnumFacing.DOWN, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, foreGround, EnumFacing.UP, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, foreGround, EnumFacing.NORTH, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, foreGround, EnumFacing.SOUTH, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, foreGround, EnumFacing.EAST, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, foreGround, EnumFacing.WEST, modelRot, null, scale, true));

        return list;
    }

    public boolean isAmbientOcclusion() {
        return false;
    }

    public boolean isGui3d() {
        return true;
    }

    public boolean isBuiltInRenderer() {
        return false;
    }

    public TextureAtlasSprite getTexture() {
        return backGround;
    }

    public static final ItemTransformVec3f LittleBigger = new ItemTransformVec3f(new Vector3f(), new Vector3f(), new Vector3f(1.5F, 1.5F, 1.5F));
    public static final ItemTransformVec3f MovedUp = new ItemTransformVec3f(new Vector3f(), new Vector3f(-0.05F, 0.0F, -0.15F), new Vector3f(1.0F, 1.0F, 1.0F));

    public ItemCameraTransforms getItemCameraTransforms() {
        return new ItemCameraTransforms(MovedUp, LittleBigger, LittleBigger, LittleBigger);
    }

    public IBakedModel handleBlockState(IBlockState state) {
        return state instanceof DummyState ? new ModelPipe((DummyState)state) : null;
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack) {
        return new ModelPipe((BlockPipe) Block.getBlockFromItem(stack.getItem()));
    }
}
