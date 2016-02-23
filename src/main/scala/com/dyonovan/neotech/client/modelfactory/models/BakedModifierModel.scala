package com.dyonovan.neotech.client.modelfactory.models

import javax.vecmath.Matrix4f

import com.google.common.base.Function
import com.google.common.collect.{ImmutableList, ImmutableMap}
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.IBakedModel
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IPerspectiveAwareModel.MapWrapper
import net.minecraftforge.client.model._
import org.apache.commons.lang3.tuple.Pair

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/22/2016
  */
class BakedModifierModel(parent : IFlexibleBakedModel, transform : ImmutableMap[TransformType, TRSRTransformation]) extends
        ItemLayerModel.BakedModel(parent.getGeneralQuads.asInstanceOf[ImmutableList[BakedQuad]],
            parent.getParticleTexture, parent.getFormat) with ISmartItemModel with IPerspectiveAwareModel {

    override def handleItemState(stack: ItemStack): IBakedModel = {
        if(!stack.hasTagCompound)
            return parent
        else {
            val function = new Function[ResourceLocation, TextureAtlasSprite] {
                override def apply(input: ResourceLocation): TextureAtlasSprite =
                    Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(input.toString)
            }
            val textureBuilder = ImmutableMap.builder[String, String]()
            textureBuilder.put("layer0", "neotech:items/electric_pickaxe")
            textureBuilder.put("layer1", "neotech:items/electric_pickaxe_mod_diamond")
            val model = ItemLayerModel.instance.retexture(textureBuilder.build())
            val bakedModel = model.bake(ModelHelper.DEFAULT_TOOL_STATE, parent.getFormat, function)
            return bakedModel
        }
    }

    override def handlePerspective(cameraTransformType: TransformType): Pair[_ <: IFlexibleBakedModel, Matrix4f] =
        MapWrapper.handlePerspective(this, transform, cameraTransformType)
}
