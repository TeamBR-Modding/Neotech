package com.dyonovan.neotech.client.modelfactory.models;

import com.dyonovan.neotech.collections.CubeTextures;
import com.dyonovan.neotech.collections.DummyState;
import com.dyonovan.neotech.common.blocks.BlockBakeable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;
@SuppressWarnings( "deprecation" )
public class ModelBlock implements ISmartBlockModel, ISmartItemModel {
    protected CubeTextures textures;
    protected static FaceBakery faceBakery = new FaceBakery();

    /**
     * The blank state. Sets icons to stone to hold place
     */
    public ModelBlock() {
        textures = new CubeTextures();
    }

    public ModelBlock(DummyState state) {
        textures = state.block.getDefaultTextures().getRotatedTextures(state.blockAccess.getBlockState(state.pos), state.block);
    }

    public ModelBlock(BlockBakeable block) {
        textures = block.getDefaultTextures();
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

        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(16.0F, 0.0F, 16.0F), face, textures.down, EnumFacing.DOWN, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 16.0F, 0.0F), new Vector3f(16.0F, 16.0F, 16.0F), face, textures.up, EnumFacing.UP, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(16.0F, 16.0F, 0.0F), face, textures.north, EnumFacing.NORTH, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 16.0F), new Vector3f(16.0F, 16.0F, 16.0F), face, textures.south, EnumFacing.SOUTH, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(16.0F, 0.0F, 0.0F), new Vector3f(16.0F, 16.0F, 16.0F), face, textures.east, EnumFacing.EAST, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 16.0F, 16.0F), face, textures.west, EnumFacing.WEST, modelRot, null, scale, true));

        return list;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getTexture() {
        return textures.north;
    }

    public static ItemTransformVec3f MovedUp = new ItemTransformVec3f(new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(-0.05F, 0.05F, -0.15F), new Vector3f(-0.5F, -0.5F, -0.5F));
    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return new ItemCameraTransforms(MovedUp, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT);
    }

    @Override
    public IBakedModel handleBlockState(IBlockState state) {
        return state instanceof DummyState ? new ModelBlock((DummyState) state) : null;
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack) {
        return new ModelBlock((BlockBakeable) Block.getBlockFromItem(stack.getItem()));
    }

    // Get the default model resource location for a block state
    // Used to put an entry into the model registry
    public static ModelResourceLocation getModelResourceLocation(IBlockState state) {
        return new ModelResourceLocation((ResourceLocation) Block.blockRegistry.getNameForObject(state.getBlock()), (new DefaultStateMapper()).getPropertyString(state.getProperties()));
    }
}
