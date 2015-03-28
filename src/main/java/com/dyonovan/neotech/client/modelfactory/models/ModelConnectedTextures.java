package com.dyonovan.neotech.client.modelfactory.models;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings( "deprecation" )
public class ModelConnectedTextures implements ISmartBlockModel, ISmartItemModel {

    @Override
    public List getFaceQuads(EnumFacing face) {
        return new ArrayList<>();
    }

    @Override
    public List getGeneralQuads() {
        return null;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public IBakedModel handleBlockState(IBlockState state) {
        return null;
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack) {
        return null;
    }

    @Override
    public TextureAtlasSprite getTexture() {
        return null;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return null;
    }
}
