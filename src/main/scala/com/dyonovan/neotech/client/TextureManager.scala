package com.dyonovan.neotech.client

import gnu.trove.map.hash.THashMap
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

import scala.collection.mutable.ArrayBuffer

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 2/26/2016
  */
object TextureManager {

    /**
      * The textures that need to be registered
      */
    lazy val texturesToRegister = new ArrayBuffer[String]()

    /**
      * The textures that have been created and are valid, identified by the name
      */
    lazy val stitchedTextures = new THashMap[String, TextureAtlasSprite]

    /**
      * Used to specify the texture to register, you MUST include the mod id
      */
    def registerTexture(location : String) : Unit = texturesToRegister += location

    /**
      * Used to get the texture from the location
      */
    def getTexture(textureLocation : String) : TextureAtlasSprite = stitchedTextures.get(textureLocation)

    @SubscribeEvent
    def textureStitch(event : TextureStitchEvent.Pre) : Unit = {
        // Iterate the list of things we need, and start the stitching
        for(textureLocation <- texturesToRegister) {
            stitchedTextures.put(textureLocation,
                event.map.registerSprite(new ResourceLocation(textureLocation)))
        }
    }
}
