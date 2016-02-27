package com.dyonovan.neotech.client.modelfactory.models

import javax.vecmath.Vector3f

import com.google.common.collect.ImmutableMap
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.{BakedQuad, ItemCameraTransforms}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraftforge.client.model.IColoredBakedQuad.ColoredBakedQuad
import net.minecraftforge.client.model.{IModelState, SimpleModelState, TRSRTransformation}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * Adapted from Tinker's Construct Model Helper https://github.com/SlimeKnights/TinkersConstruct/blob/master/src/main/java/slimeknights/tconstruct/library/client/model/ModelHelper.java
  *
  * @author Paul Davis <pauljoda>
  * @since 2/22/2016
  */
object ModelHelper {

    lazy val DEFAULT_ITEM_STATE : IModelState = { //Normal items (not held as a tool)
        val thirdPerson = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
                new Vector3f(0, 1F / 16, -3F / 16),
                TRSRTransformation.quatFromYXZDegrees(new Vector3f(-90, 0, 0)),
                new Vector3f(0.55F, 0.55F, 0.55F),
                null))
        val firstPerson = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
            new Vector3f(0, 4F / 16, 2F / 16),
            TRSRTransformation.quatFromYXZDegrees(new Vector3f(0, -135, 25)),
            new Vector3f(1.7F, 1.7F, 1.7F),
            null))
        new SimpleModelState(ImmutableMap.of(ItemCameraTransforms.TransformType.THIRD_PERSON, thirdPerson, ItemCameraTransforms.TransformType.FIRST_PERSON, firstPerson))
    }

    var DEFAULT_TOOL_STATE : IModelState = { //Tool items, held in hand
    val thirdPerson = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
            new Vector3f(0, 1.25F / 16, -3.5F / 16),
            TRSRTransformation.quatFromYXZDegrees(new Vector3f(0, 90, -35)),
            new Vector3f(0.85F, 0.85F, 0.85F),
            null))
        val firstPerson = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
            new Vector3f(0, 4F / 16, 2F / 16),
            TRSRTransformation.quatFromYXZDegrees(new Vector3f(0, -135, 25)),
            new Vector3f(1.7F, 1.7F, 1.7F),
            null))
        new SimpleModelState(ImmutableMap.of(ItemCameraTransforms.TransformType.THIRD_PERSON, thirdPerson, ItemCameraTransforms.TransformType.FIRST_PERSON, firstPerson))
    }

    lazy val DEFAULT_BLOCK_STATE : IModelState = { //Normal block
    val thirdPerson = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
            new Vector3f(0, 1.5F, -2.75F),
            TRSRTransformation.quatFromYXZDegrees(new Vector3f(10F, -45F, -2.75F)),
            new Vector3f(0.375F, 0.375F, 0.375F),
            null))
        new SimpleModelState(ImmutableMap.of(ItemCameraTransforms.TransformType.THIRD_PERSON, thirdPerson))
    }

    /**
      * Gets the texture for the block with meta
      *
      * @param block The block
      * @param meta The state
      * @return The texture
      */
    def getTextureFromBlock(block : Block, meta : Int) : TextureAtlasSprite =
        getTextureFromBlockState(block.getStateFromMeta(meta))

    /**
      * Gets the texture by state
      *
      * @param state The state
      * @return The texture for the block
      */
    def getTextureFromBlockState(state : IBlockState) : TextureAtlasSprite =
        Minecraft.getMinecraft.getBlockRendererDispatcher.getBlockModelShapes.getTexture(state)

    /**
      * Takes a normal baked quad and colors it
      *
      * @param c The color
      * @param quad The quad
      * @return A copy of the quad with colored faces
      */
    def colorQuad(c : Int, quad: BakedQuad) : BakedQuad = {
        // Create array of same size
        val data = new Array[Int](quad.getVertexData.length)

        //We don't want to destroy the original, so we will copy all data so the original is not touched
        for(x <- 0 until quad.getVertexData.length)
            data(x) = quad.getVertexData()(x)

        //Create the color components
        var alpha = c >> 24
        if(alpha == 0) // No point in invisible quad
            alpha = 255
        val red   = (c >> 16) & 0xFF
        val green = (c >> 8) & 0xFF
        val blue  = (c >> 0) & 0xFF

        // The new color (quads are kinda reverse so that is why we are swapping stuff)
        val color = red | green << 8 | blue << 16 | alpha << 24

        // Place the color into the vertex data
        for(i <- 0 until 4)
            data(i * 7 + 3) = color

        // Create the new quad
        new ColoredBakedQuad(data, -1, quad.getFace)
    }
}