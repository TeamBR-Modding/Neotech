package com.dyonovan.jatm.client.modelfactory.models;

import com.dyonovan.jatm.collections.DummyState;
import com.dyonovan.jatm.common.tileentity.notmachines.TileTank;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartBlockModel;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class ModelTank implements ISmartBlockModel {
    protected static TextureAtlasSprite iron = null;
    protected static TextureAtlasSprite glass = null;
    protected static FaceBakery faceBakery = new FaceBakery();
    protected float fluidHeight;

    public ModelTank() {
        iron = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/iron_block");
        glass = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/glass");
    }

    public ModelTank(DummyState state) {
        fluidHeight = ((TileTank)state.blockAccess.getTileEntity(state.pos)).getFluidLevelScaled();
    }

    @Override
    public List getFaceQuads(EnumFacing p_177551_1_) {
        return new ArrayList<>();
    }

    @Override
    public List getGeneralQuads() {
        ArrayList list = new ArrayList();
        BlockFaceUV uv = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
        BlockPartFace face = new BlockPartFace(null, 0, "", uv);

        ModelRotation modelRot = ModelRotation.X0_Y0;
        boolean scale = true;

        //Top and Bottom (Iron)
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(16.0F, 0.0F, 16.0F), face, iron, EnumFacing.DOWN, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 16.0F, 0.0F), new Vector3f(16.0F, 16.0F, 16.0F), face, iron, EnumFacing.UP, modelRot, null, scale, true));

        //Sides (Glass)
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(16.0F, 16.0F, 0.0F), face, glass, EnumFacing.NORTH, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 16.0F), new Vector3f(16.0F, 16.0F, 16.0F), face, glass, EnumFacing.SOUTH, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 16.0F, 16.0F), face, glass, EnumFacing.WEST, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(16.0F, 0.0F, 0.0F), new Vector3f(16.0F, 16.0F, 16.0F), face, glass, EnumFacing.EAST, modelRot, null, scale, true));

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
        return glass;
    }

    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    public IBakedModel handleBlockState(IBlockState state) {
        return state instanceof DummyState ? new ModelTank((DummyState)state) : null;
    }
}
