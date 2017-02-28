package com.teambrmodding.neotech.client.models;

import com.google.common.collect.ImmutableMap;
import com.teambrmodding.neotech.common.blocks.storage.ItemBlockFluidStorage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/27/2017
 */
public class ModelItemFluidStorage implements IBakedModel, IPerspectiveAwareModel {
    // The face bakery
    protected static final FaceBakery faceBakery = new FaceBakery();

    private static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
        return new TRSRTransformation(
                new javax.vecmath.Vector3f(tx / 16, ty / 16, tz / 16),
                TRSRTransformation.quatFromXYZDegrees(new javax.vecmath.Vector3f(ax, ay, az)),
                new javax.vecmath.Vector3f(s, s, s),
                null);
    }

    private static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms =
            ImmutableMap.<ItemCameraTransforms.TransformType, TRSRTransformation>builder()
            .put(ItemCameraTransforms.TransformType.GUI,                         get(0, 0, 0, 30, 45, 0, 0.625f))
            .put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,     get(0, 2.5f, 0, 75, 45, 0, 0.375f))
            .put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,      get(0, 2.5f, 0, 75, 45, 0, 0.375f))
            .put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND,     get(0, 0, 0, 0, 45, 0, 0.4f))
            .put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND,      get(0, 0, 0, 0, 225, 0, 0.4f))
            .put(ItemCameraTransforms.TransformType.GROUND,                      get(0, 2, 0, 0, 0, 0, 0.25f))
            .put(ItemCameraTransforms.TransformType.HEAD,                        get(0, 0, 0, 0, 0, 0, 1))
            .put(ItemCameraTransforms.TransformType.FIXED,                       get(0, 0, 0, 0, 0, 0, 1)).build();

    // Variables
    private IBakedModel parentModel;
    private float fluidHeight;
    private Fluid renderFluid;

    // Stub constructor
    public ModelItemFluidStorage() {}

    /**
     * Wraps the default model
     * @param parentModel The defined model
     */
    public ModelItemFluidStorage(IBakedModel parentModel) {
        this.parentModel = parentModel;
    }

    /*******************************************************************************************************************
     * IPerspectiveAwareModel                                                                                          *
     *******************************************************************************************************************/

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return Pair.of(this, transforms.get(cameraTransformType).getMatrix());
    }

    /*******************************************************************************************************************
     * IBakedModel                                                                                                     *
     *******************************************************************************************************************/

    // Held Variables for creating quads
    private static final BlockFaceUV     uv       = new BlockFaceUV(new float[] {0.0F, 0.0F, 16.0F, 16.0F}, 0);
    private static final BlockPartFace   face     = new BlockPartFace(null, 0, "", uv);
    private static final ModelRotation   modelRot = ModelRotation.X0_Y0;
    private static final boolean         scale    = true;
    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quadList = new ArrayList<>();

        // Add parent quads
        quadList.addAll(parentModel.getQuads(state, side, rand));

        // Render Fluid
        if(renderFluid != null) {
            TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(renderFluid.getStill().toString());
            // Top and Bottom
            quadList.add(faceBakery.makeBakedQuad(new Vector3f(2.01F, fluidHeight, 2.01F),
                    new Vector3f(13.99F, fluidHeight, 13.99F), face, texture, EnumFacing.UP, modelRot,
                    null, scale, true));
            quadList.add(faceBakery.makeBakedQuad(new Vector3f(2.01F, 0.001F, 2.01F),
                    new Vector3f(13.999F, 0.001F, 13.999F), face, texture, EnumFacing.DOWN, modelRot,
                    null, scale, true));

            // Sides
            quadList.add(faceBakery.makeBakedQuad(new Vector3f(2.01F, 2.01F, 2.01F),
                    new Vector3f(13.99F, fluidHeight, 2.01F), face, texture, EnumFacing.NORTH, modelRot,
                    null, scale, true));
            quadList.add(faceBakery.makeBakedQuad(new Vector3f(2.01F, 2.01F, 13.99F),
                    new Vector3f(13.99F, fluidHeight, 13.99F), face, texture, EnumFacing.SOUTH, modelRot,
                    null, scale, true));
            quadList.add(faceBakery.makeBakedQuad(new Vector3f(2.01F, 2.01F, 2.01F),
                    new Vector3f(2.01F, fluidHeight, 13.99F), face, texture, EnumFacing.WEST, modelRot,
                    null, scale, true));
            quadList.add(faceBakery.makeBakedQuad(new Vector3f(13.99F, 2.01F, 2.01F),
                    new Vector3f(13.99F, fluidHeight, 13.99F), face, texture, EnumFacing.EAST, modelRot,
                    null, scale, true));
        }

        return quadList;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return parentModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return parentModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return parentModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return parentModel.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return new ItemFluidStorageOverrideList();
    }

    /*******************************************************************************************************************
     * ItemOverrideList                                                                                                *
     ******************************************************************************************************************/

    private class ItemFluidStorageOverrideList extends ItemOverrideList {

        /**
         * Default Constructor
         */
        public ItemFluidStorageOverrideList() {
            super(new ArrayList<>());
        }

        /**
         * Handles adding the data to the model we need from the itemstack
         * @param originalModel The base model loaded from json
         * @param stack         The stack containing info
         * @param world         The world
         * @param entity
         * @return
         */
        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
            if(stack.hasTagCompound() && stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) &&
                    originalModel instanceof ModelItemFluidStorage && stack.getItem() instanceof ItemBlockFluidStorage) {
                // Grab the info needed
                ModelItemFluidStorage model = (ModelItemFluidStorage) originalModel;
                IFluidHandler tank          = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

                // Attempt to extract fluid
                FluidStack currentStored = FluidUtil.getFluidContained(stack);
                if(currentStored == null) {
                    model.fluidHeight = 0.0F;
                    model.renderFluid = null;
                    return model; // There is nothing stored, don't need extra rendering
                }

                model.fluidHeight = // Scale fluid to model height
                        (Math.min(14.99F,
                                ((currentStored.amount * 14F) / tank.getTankProperties()[0].getCapacity()) + 1.31F));
                model.renderFluid = currentStored.getFluid(); // Set fluid to render
                return model; // Returned model that will handle fluid rendering
            } else if(originalModel instanceof ModelItemFluidStorage) {
                ModelItemFluidStorage model = (ModelItemFluidStorage) originalModel;
                model.fluidHeight = 0.0F;
                model.renderFluid = null;
                return model;
            }

            return originalModel;
        }
    }
}
