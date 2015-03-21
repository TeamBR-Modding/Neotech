package com.dyonovan.jatm.client.modelfactory.models;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import com.dyonovan.jatm.collections.DummyState;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartBlockModel;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class ModelCable implements ISmartBlockModel {
    static TextureAtlasSprite noEdgeTexture = null;
    static TextureAtlasSprite plusTexture = null;
    static TextureAtlasSprite squareTexture = null;
    static FaceBakery faceBakery = new FaceBakery();
    boolean[] extensions = new boolean[6];

    public ModelCable() {
        noEdgeTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Constants.MODID + ":blocks/basicCableNoEdge");
        plusTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Constants.MODID + ":blocks/basicCablePlus");
        squareTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Constants.MODID + ":blocks/basicCableSquare");
    }

    public ModelCable(DummyState state) {
        EnumFacing[] var2 = EnumFacing.values();
        int var3 = var2.length;
        for(int var4 = 0; var4 < var3; ++var4) {
            EnumFacing facing = var2[var4];
            TileEntity te = state.blockAccess.getTileEntity(state.pos.offset(facing));
            if(te instanceof IEnergyReceiver || te instanceof IEnergyProvider) {
                this.extensions[facing.ordinal()] = true;
            }
        }
    }

    public List<BakedQuad> getFaceQuads(EnumFacing facing) {
        return new ArrayList();
    }

    private void drawCableStump(ModelRotation modelRot, List<BakedQuad> list) {
        BlockPartFace face = new BlockPartFace((EnumFacing)null, 0, "", new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0));
        boolean scale = true;
        list.add(faceBakery.makeBakedQuad(new Vector3f(4.0F, 12.0F, 12.0F), new Vector3f(12.0F, 12.0F, 16.0F), face, plusTexture, EnumFacing.UP, modelRot, (BlockPartRotation) null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(4.0F, 4.0F, 12.0F), new Vector3f(12.0F, 4.0F, 16.0F), face, plusTexture, EnumFacing.DOWN, modelRot, (BlockPartRotation) null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(4.0F, 4.0F, 12.0F), new Vector3f(4.0F, 12.0F, 16.0F), face, plusTexture, EnumFacing.WEST, modelRot, (BlockPartRotation) null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(12.0F, 4.0F, 12.0F), new Vector3f(12.0F, 12.0F, 16.0F), face, plusTexture, EnumFacing.EAST, modelRot, (BlockPartRotation) null, scale, true));
    }

    public List<BakedQuad> getGeneralQuads() {
        ArrayList list = new ArrayList();
        BlockFaceUV uv = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
        BlockPartFace face = new BlockPartFace((EnumFacing)null, 0, "", uv);

        if(this.extensions[0]) {
            this.drawCableStump(ModelRotation.X270_Y0, list);
        }

        if(this.extensions[1]) {
            this.drawCableStump(ModelRotation.X90_Y0, list);
        }

        if(this.extensions[2]) {
            this.drawCableStump(ModelRotation.X180_Y0, list);
        }

        if(this.extensions[3]) {
            this.drawCableStump(ModelRotation.X0_Y0, list);
        }

        if(this.extensions[4]) {
            this.drawCableStump(ModelRotation.X0_Y90, list);
        }

        if(this.extensions[5]) {
            this.drawCableStump(ModelRotation.X0_Y270, list);
        }

        ModelRotation modelRot = ModelRotation.X0_Y0;
        boolean scale = true;
        if(!this.extensions[0]) {
            list.add(faceBakery.makeBakedQuad(new Vector3f(4.0F, 4.0F, 4.0F), new Vector3f(12.0F, 4.0F, 12.0F), face, plusTexture, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation) null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(5.0F, 3.999F, (float) (this.extensions[2] ? 5 : 4)), new Vector3f(11.0F, 3.999F, (float) (this.extensions[3] ? 11 : 12)), face, squareTexture, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation) null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f((float) (this.extensions[4] ? 5 : 4), 3.999F, 5.0F), new Vector3f((float) (this.extensions[5] ? 11 : 12), 3.999F, 11.0F), face, squareTexture, EnumFacing.DOWN, ModelRotation.X0_Y0, (BlockPartRotation) null, scale, true));
        }

        if(!this.extensions[1]) {
            list.add(faceBakery.makeBakedQuad(new Vector3f(4.0F, 12.0F, 4.0F), new Vector3f(12.0F, 12.0F, 12.0F), face, plusTexture, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation) null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(5.0F, 12.001F, (float) (this.extensions[2] ? 5 : 4)), new Vector3f(11.0F, 12.001F, (float) (this.extensions[3] ? 11 : 12)), face, squareTexture, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation) null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f((float) (this.extensions[4] ? 5 : 4), 12.001F, 5.0F), new Vector3f((float) (this.extensions[5] ? 11 : 12), 12.001F, 11.0F), face, squareTexture, EnumFacing.UP, ModelRotation.X0_Y0, (BlockPartRotation) null, scale, true));
        }

        if(!this.extensions[2]) {
            list.add(faceBakery.makeBakedQuad(new Vector3f(4.0F, 4.0F, 4.0F), new Vector3f(12.0F, 12.0F, 4.0F), face, plusTexture, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation) null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f((float) (this.extensions[4] ? 5 : 4), 5.0F, 3.999F), new Vector3f((float) (this.extensions[5] ? 11 : 12), 11.0F, 3.999F), face, squareTexture, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation) null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(5.0F, (float) (this.extensions[0] ? 5 : 4), 3.999F), new Vector3f(11.0F, (float) (this.extensions[1] ? 11 : 12), 3.999F), face, squareTexture, EnumFacing.NORTH, ModelRotation.X0_Y0, (BlockPartRotation) null, scale, true));
        }

        if(!this.extensions[3]) {
            list.add(faceBakery.makeBakedQuad(new Vector3f(4.0F, 4.0F, 12.0F), new Vector3f(12.0F, 12.0F, 12.0F), face, plusTexture, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation) null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f((float) (this.extensions[4] ? 5 : 4), 5.0F, 12.001F), new Vector3f((float) (this.extensions[5] ? 11 : 12), 11.0F, 12.001F), face, squareTexture, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation) null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(5.0F, (float) (this.extensions[0] ? 5 : 4), 12.001F), new Vector3f(11.0F, (float) (this.extensions[1] ? 11 : 12), 12.001F), face, squareTexture, EnumFacing.SOUTH, ModelRotation.X0_Y0, (BlockPartRotation) null, scale, true));
        }

        if(!this.extensions[4]) {
            list.add(faceBakery.makeBakedQuad(new Vector3f(4.0F, 4.0F, 4.0F), new Vector3f(4.0F, 12.0F, 12.0F), face, plusTexture, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation) null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(3.999F, 5.0F, (float) (this.extensions[2] ? 5 : 4)), new Vector3f(3.999F, 11.0F, (float) (this.extensions[3] ? 11 : 12)), face, squareTexture, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation) null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(3.999F, (float) (this.extensions[0] ? 5 : 4), 5.0F), new Vector3f(3.999F, (float) (this.extensions[1] ? 11 : 12), 11.0F), face, squareTexture, EnumFacing.WEST, ModelRotation.X0_Y0, (BlockPartRotation) null, scale, true));
        }

        if(!this.extensions[5]) {
            list.add(faceBakery.makeBakedQuad(new Vector3f(12.0F, 4.0F, 4.0F), new Vector3f(12.0F, 12.0F, 12.0F), face, plusTexture, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation) null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(12.001F, 5.0F, (float) (this.extensions[2] ? 5 : 4)), new Vector3f(12.001F, 11.0F, (float) (this.extensions[3] ? 11 : 12)), face, squareTexture, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation) null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(12.001F, (float) (this.extensions[0] ? 5 : 4), 5.0F), new Vector3f(12.001F, (float) (this.extensions[1] ? 11 : 12), 11.0F), face, squareTexture, EnumFacing.EAST, ModelRotation.X0_Y0, (BlockPartRotation) null, scale, true));
        }
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
        return squareTexture;
    }

    public static final ItemTransformVec3f LittleBigger = new ItemTransformVec3f(new Vector3f(), new Vector3f(), new Vector3f(2.0F, 2.0F, 2.0F));
    public static final ItemTransformVec3f MovedUp = new ItemTransformVec3f(new Vector3f(), new Vector3f(-0.05F, 0.0F, -0.15F), new Vector3f(1.0F, 1.0F, 1.0F));

    public ItemCameraTransforms getItemCameraTransforms() {
        return new ItemCameraTransforms(MovedUp, LittleBigger, LittleBigger, LittleBigger);
    }

    public IBakedModel handleBlockState(IBlockState state) {
        return state instanceof DummyState ?new ModelCable((DummyState)state):null;
    }
}
