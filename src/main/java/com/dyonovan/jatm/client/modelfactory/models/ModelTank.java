package com.dyonovan.jatm.client.modelfactory.models;

import com.dyonovan.jatm.collections.DummyState;
import com.dyonovan.jatm.common.tileentity.storage.TileTank;
import com.dyonovan.jatm.handlers.BlockHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class ModelTank implements ISmartBlockModel, ISmartItemModel {
    protected static TextureAtlasSprite iron = null;
    protected static TextureAtlasSprite glass = null;
    protected static FaceBakery faceBakery = new FaceBakery();
    protected float fluidHeight;
    protected Fluid renderFluid;

    public ModelTank() {
        iron = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/iron_block");
        glass = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/glass");
    }

    public ModelTank(DummyState state) {
        fluidHeight = ((TileTank)state.blockAccess.getTileEntity(state.pos)).getFluidLevelScaled();
        renderFluid = ((TileTank)state.blockAccess.getTileEntity(state.pos)).getCurrentFluid();
    }

    public ModelTank(float renderHeight, Fluid fluid) {
        this.fluidHeight = renderHeight;
        this.renderFluid = fluid;
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
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.001F, 0.0F), new Vector3f(16.0F, 0.001F, 16.0F), face, iron, EnumFacing.DOWN.getOpposite(), modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 16.0F, 0.0F), new Vector3f(16.0F, 16.0F, 16.0F), face, iron, EnumFacing.UP, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 15.99F, 0.0F), new Vector3f(16.0F, 15.99F, 16.0F), face, iron, EnumFacing.UP.getOpposite(), modelRot, null, scale, true));

        //Sides (Glass)
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(16.0F, 16.0F, 0.0F), face, glass, EnumFacing.NORTH, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(16.0F, 16.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F), face, glass, EnumFacing.NORTH.getOpposite(), modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 16.0F), new Vector3f(16.0F, 16.0F, 16.0F), face, glass, EnumFacing.SOUTH, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(16.0F, 16.0F, 16.0F), new Vector3f(0.0F, 0.0F, 16.0F), face, glass, EnumFacing.SOUTH.getOpposite(), modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 16.0F, 16.0F), face, glass, EnumFacing.WEST, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 16.0F, 16.0F), new Vector3f(0.0F, 0.0F, 0.0F), face, glass, EnumFacing.WEST.getOpposite(), modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(16.0F, 0.0F, 0.0F), new Vector3f(16.0F, 16.0F, 16.0F), face, glass, EnumFacing.EAST, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(16.0F, 16.0F, 16.0F), new Vector3f(16.0F, 0.0F, 0.0F), face, glass, EnumFacing.EAST.getOpposite(), modelRot, null, scale, true));

        //Render Fluid
        if(renderFluid != null) {
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.01F, fluidHeight, 0.01F), new Vector3f(15.99F, fluidHeight, 15.99F), face, renderFluid.getIcon(), EnumFacing.UP, modelRot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.01F, 0.001F, 0.01F), new Vector3f(15.999F, 0.001F, 15.999F), face, renderFluid.getIcon(), EnumFacing.DOWN, modelRot, null, scale, true));

            list.add(faceBakery.makeBakedQuad(new Vector3f(0.01F, 0.01F, 0.01F), new Vector3f(15.99F, fluidHeight, 0.01F), face, renderFluid.getIcon(), EnumFacing.NORTH, modelRot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.01F, 0.01F, 15.99F), new Vector3f(15.99F, fluidHeight, 15.99F), face, renderFluid.getIcon(), EnumFacing.SOUTH, modelRot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.01F, 0.01F, 0.01F), new Vector3f(0.01F, fluidHeight, 15.99F), face, renderFluid.getIcon(), EnumFacing.WEST, modelRot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(15.99F, 0.01F, 0.01F), new Vector3f(15.99F, fluidHeight, 15.99F), face, renderFluid.getIcon(), EnumFacing.EAST, modelRot, null, scale, true));
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
        return glass;
    }

    public static ItemTransformVec3f MovedUp = new ItemTransformVec3f(new Vector3f(-1.0F, 0.0F, 0.0F), new Vector3f(-0.05F, 0.05F, -0.15F), new Vector3f(-0.5F, -0.5F, -0.5F));

    public ItemCameraTransforms getItemCameraTransforms() {
        return new ItemCameraTransforms(MovedUp, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT);
    }

    public IBakedModel handleBlockState(IBlockState state) {
        return state instanceof DummyState ? new ModelTank((DummyState)state) : null;
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack) {
        if(stack.hasTagCompound()) {
            NBTTagCompound liquidTag = stack.getTagCompound().getCompoundTag("Fluid");
            if (liquidTag != null) {
                FluidStack liquid = FluidStack.loadFluidStackFromNBT(liquidTag);
                float height;
                if (stack.getItem() == Item.getItemFromBlock(BlockHandler.basicTank))
                    height = Math.min((16 * liquid.amount) / (FluidContainerRegistry.BUCKET_VOLUME * 8), 15.99F);
                else
                    height = 16;
                return new ModelTank(height, liquid.getFluid());
            }
        }
        return new ModelTank();
    }
}
