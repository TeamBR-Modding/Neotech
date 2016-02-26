package com.dyonovan.neotech.client.modelfactory.models

import javax.vecmath.Matrix4f

import com.dyonovan.neotech.tools.ToolHelper
import com.dyonovan.neotech.tools.tools.BaseElectricTool
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
class ToolModel(parent : IFlexibleBakedModel, transform : ImmutableMap[TransformType, TRSRTransformation]) extends
        ItemLayerModel.BakedModel(parent.getGeneralQuads.asInstanceOf[ImmutableList[BakedQuad]],
            parent.getParticleTexture, parent.getFormat) with ISmartItemModel with IPerspectiveAwareModel {

    override def handleItemState(stack: ItemStack): IBakedModel = {
        if(stack == null || !stack.hasTagCompound)
            parent
        else {
            val function = new Function[ResourceLocation, TextureAtlasSprite] {
                override def apply(input: ResourceLocation): TextureAtlasSprite =
                    Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(input.toString)
            }
            val textureBuilder = ImmutableMap.builder[String, String]()
            textureBuilder.put("layer0", stack.getItem.asInstanceOf[BaseElectricTool].getBaseTexture)

            val modifierList = ToolHelper.getModifierTagList(stack)
            var i = 1
            if(modifierList != null) {
                for(x <- 0 until modifierList.tagCount()) {
                    val tagCompound = modifierList.getCompoundTagAt(x)
                    if(!tagCompound.hasKey("Active") || (tagCompound.hasKey("Active") && tagCompound.getBoolean("Active"))) {
                        textureBuilder.put("layer" + i.toString, tagCompound.getString("TextureLocation"))
                        i += 1
                    }
                }
            }

            val model = ItemLayerModel.instance.retexture(textureBuilder.build())
            val bakedModel = model.bake(ModelHelper.DEFAULT_TOOL_STATE, parent.getFormat, function)
            bakedModel
        }
    }

    override def handlePerspective(cameraTransformType: TransformType): Pair[_ <: IFlexibleBakedModel, Matrix4f] =
        MapWrapper.handlePerspective(this, transform, cameraTransformType)
}
