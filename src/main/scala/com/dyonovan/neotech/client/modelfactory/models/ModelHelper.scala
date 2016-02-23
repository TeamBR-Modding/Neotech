package com.dyonovan.neotech.client.modelfactory.models

import java.io.InputStreamReader
import java.lang.reflect.Type
import java.util
import javax.vecmath.Vector3f

import com.google.common.base.Charsets
import com.google.common.collect.{ImmutableList, ImmutableMap}
import com.google.gson._
import com.google.gson.reflect.TypeToken
import com.teambr.bookshelf.helper.LogHelper
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.{ItemTransformVec3f, ModelBlock, BakedQuad, ItemCameraTransforms}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.ResourceLocation
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

    lazy val mapType = new TypeToken[java.util.Map[java.lang.String, java.lang.String]]() {}.getType
    lazy val offsetType = new TypeToken[Offset]() {}.getType
    lazy val GSON : Gson = new GsonBuilder().registerTypeAdapter(mapType, ModelTextureDeserializerInstance).registerTypeAdapter(offsetType, OffsetDeserializerInstance).create()

    var ModelTextureDeserializerInstance : ModelTextureDeserializer = null
    var OffsetDeserializerInstance       : OffsetDeserializer = null

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

    /*******************************************************************************************************************
      *********************************************** Loading **********************************************************
      ******************************************************************************************************************/

    def loadTexturesFromJson(location : ResourceLocation) : util.Map[String, String] = {
        // Load the Json
        val resource = Minecraft.getMinecraft.getResourceManager
                                .getResource(new ResourceLocation(location.getResourceDomain, location.getResourcePath + ".json"))
        val reader = new InputStreamReader(resource.getInputStream, Charsets.UTF_8)
        GSON.fromJson(reader, mapType)
    }

    def loadOffsetFromJson(location : ResourceLocation) : Offset = {
        // Load the Json
        val resource = Minecraft.getMinecraft.getResourceManager
                .getResource(new ResourceLocation(location.getResourceDomain, location.getResourcePath + ".json"))
        val reader = new InputStreamReader(resource.getInputStream, Charsets.UTF_8)
        GSON.fromJson(reader, offsetType)
    }

    def loadTransformFromJson(location : ResourceLocation) : ImmutableMap[ItemCameraTransforms.TransformType, TRSRTransformation] = {
        // Load the Json
        val resource = Minecraft.getMinecraft.getResourceManager
                .getResource(new ResourceLocation(location.getResourceDomain, location.getResourcePath + ".json"))
        val reader = new InputStreamReader(resource.getInputStream, Charsets.UTF_8)

        val modelBlock = ModelBlock.deserialize(reader)
        val itemCameraTransforms = modelBlock.func_181682_g()
        val builder = ImmutableMap.builder[ItemCameraTransforms.TransformType, TRSRTransformation]()
        for(typeTransform <- ItemCameraTransforms.TransformType.values()) {
            if(itemCameraTransforms.getTransform(typeTransform) != ItemTransformVec3f.DEFAULT)
                builder.put(typeTransform, new TRSRTransformation(itemCameraTransforms.getTransform(typeTransform)))
        }
        builder.build()
    }

    def loadTextureListFromJson(location : ResourceLocation) : ImmutableList[ResourceLocation] = {
        val builder = ImmutableList.builder[ResourceLocation]()
        for(string <- loadTexturesFromJson(location).values().toArray)
            builder.add(new ResourceLocation(string.asInstanceOf[String]))
        builder.build()
    }

    def getModelLocation(location : ResourceLocation) =
        new ResourceLocation(location.getResourceDomain, "models/" + location.getResourcePath + ".json")

    /*******************************************************************************************************************
      ******************************************* Deserializers ********************************************************
      ******************************************************************************************************************/

    final class ModelTextureDeserializer extends JsonDeserializer[java.util.Map[java.lang.String, java.lang.String]] {
        ModelTextureDeserializerInstance = new ModelTextureDeserializer
        private val GSONOURS = new Gson()
        override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): util.Map[String, String] = {
            val jsonObject = json.getAsJsonObject
            val texElem = jsonObject.get("textures")

            if (texElem == null) {
                LogHelper.severe("Missing texture entries in json")
                return null
            }

            GSONOURS.fromJson(texElem, mapType)
        }
    }

    final class OffsetDeserializer extends JsonDeserializer[Offset] {
        OffsetDeserializerInstance = new OffsetDeserializer
        private val GSONOURS = new Gson()
        override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Offset = {
            val jsonObject = json.getAsJsonObject
            val texElem = jsonObject.get("offset")

            if (texElem == null) {
                new Offset(0, 0)
            }

            GSONOURS.fromJson(texElem, offsetType)
        }
    }

    class Offset(var x: Int, var y: Int)
}