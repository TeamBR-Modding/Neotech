package com.dyonovan.neotech.client.modelfactory.models;

import com.dyonovan.neotech.collections.DummyState;
import com.dyonovan.neotech.common.tileentity.storage.TileIronTank;
import com.dyonovan.neotech.handlers.BlockHandler;
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
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;
@SuppressWarnings( "deprecation" )
public class ModelTank implements ISmartBlockModel, ISmartItemModel {
    protected TextureAtlasSprite topIcon = null;
    protected static TextureAtlasSprite glass = null;
    protected static FaceBakery faceBakery = new FaceBakery();
    protected float fluidHeight;
    protected Fluid renderFluid;
    protected boolean isItem;

    public ModelTank() {
        topIcon = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/iron_block");
        glass = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/glass");
    }

    public ModelTank(DummyState state) {
        if(state.blockAccess.getTileEntity(state.pos) != null) {
            fluidHeight = ((TileIronTank) state.blockAccess.getTileEntity(state.pos)).getFluidLevelScaled();
            renderFluid = ((TileIronTank) state.blockAccess.getTileEntity(state.pos)).getCurrentFluid();
            topIcon = ((TileIronTank) state.blockAccess.getTileEntity(state.pos)).getTierIcon();
        }
        if (topIcon == null)
            topIcon = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/iron_block");
        isItem = false;
    }

    public ModelTank(float renderHeight, Fluid fluid, TextureAtlasSprite icon) {
        this.fluidHeight = renderHeight;
        this.renderFluid = fluid;
        this.topIcon = icon;
        if (topIcon == null)
            topIcon = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/iron_block");
        isItem = true;
    }

    @Override
    public List getFaceQuads(EnumFacing p_177551_1_) {
        return new ArrayList<>();
    }

    @Override
    public List getGeneralQuads() {
        ArrayList<BakedQuad> list = new ArrayList<>();
        BlockFaceUV uv = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
        BlockPartFace face = new BlockPartFace(null, 0, "", uv);
        ModelRotation modelRot = ModelRotation.X0_Y0;
        boolean scale = true;

        if(MinecraftForgeClient.getRenderLayer() == EnumWorldBlockLayer.CUTOUT || isItem) {
            //Top and Bottom (Iron)
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(16.0F, 0.0F, 16.0F), face, topIcon, EnumFacing.DOWN, modelRot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.01F, 0.0F), new Vector3f(16.0F, 0.01F, 16.0F), face, topIcon, EnumFacing.DOWN.getOpposite(), modelRot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 16.0F, 0.0F), new Vector3f(16.0F, 16.0F, 16.0F), face, topIcon, EnumFacing.UP, modelRot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 15.99F, 0.0F), new Vector3f(16.0F, 15.99F, 16.0F), face, topIcon, EnumFacing.UP.getOpposite(), modelRot, null, scale, true));

            //Sides (Glass)
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(16.0F, 16.0F, 0.0F), face, glass, EnumFacing.NORTH, modelRot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(16.0F, 16.0F, 0.01F), new Vector3f(0.0F, 0.0F, 0.01F), face, glass, EnumFacing.NORTH.getOpposite(), modelRot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 16.0F), new Vector3f(16.0F, 16.0F, 16.0F), face, glass, EnumFacing.SOUTH, modelRot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(16.0F, 16.0F, 15.99F), new Vector3f(0.0F, 0.0F, 15.99F), face, glass, EnumFacing.SOUTH.getOpposite(), modelRot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 16.0F, 16.0F), face, glass, EnumFacing.WEST, modelRot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.01F, 16.0F, 16.0F), new Vector3f(0.01F, 0.0F, 0.0F), face, glass, EnumFacing.WEST.getOpposite(), modelRot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(16.0F, 0.0F, 0.0F), new Vector3f(16.0F, 16.0F, 16.0F), face, glass, EnumFacing.EAST, modelRot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(15.99F, 16.0F, 16.0F), new Vector3f(15.99F, 0.0F, 0.0F), face, glass, EnumFacing.EAST.getOpposite(), modelRot, null, scale, true));
        }
        //Render Fluid
        if (renderFluid != null && (MinecraftForgeClient.getRenderLayer() == EnumWorldBlockLayer.TRANSLUCENT || isItem)) {
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

    public static ItemTransformVec3f MovedUp = new ItemTransformVec3f(new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(-0.05F, 0.05F, -0.15F), new Vector3f(-0.5F, -0.5F, -0.5F));

    public ItemCameraTransforms getItemCameraTransforms() {
        return new ItemCameraTransforms(MovedUp, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT);
    }

    public IBakedModel handleBlockState(IBlockState state) {
        return state instanceof DummyState ? new ModelTank((DummyState) state) : null;
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack) {
        float height = 0.01F;
        Fluid fluid = null;
        TextureAtlasSprite top = null;
        if (stack.hasTagCompound()) {
            NBTTagCompound liquidTag = stack.getTagCompound().getCompoundTag("Fluid");

            if (liquidTag != null) {
                FluidStack liquid = FluidStack.loadFluidStackFromNBT(liquidTag);
                fluid = liquid.getFluid();
                if (stack.getItem() == Item.getItemFromBlock(BlockHandler.ironTank))
                    height = Math.min((16 * liquid.amount) / (FluidContainerRegistry.BUCKET_VOLUME * 8), 15.99F);
                else if (stack.getItem() == Item.getItemFromBlock(BlockHandler.goldTank))
                    height = Math.min((16 * liquid.amount) / (FluidContainerRegistry.BUCKET_VOLUME * 16), 15.99F);
                else if (stack.getItem() == Item.getItemFromBlock(BlockHandler.diamondTank))
                    height = Math.min((16 * liquid.amount) / (FluidContainerRegistry.BUCKET_VOLUME * 64), 15.99F);
                else
                    height = 16;
            }
        }
        if (stack.getItem() == Item.getItemFromBlock(BlockHandler.ironTank))
            top = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/iron_block");
        else if (stack.getItem() == Item.getItemFromBlock(BlockHandler.goldTank))
            top = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/gold_block");
        else if (stack.getItem() == Item.getItemFromBlock(BlockHandler.diamondTank))
            top = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/diamond_block");

        return new ModelTank(height, fluid, top);
    }
}
