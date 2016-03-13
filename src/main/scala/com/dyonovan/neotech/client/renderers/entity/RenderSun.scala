package com.dyonovan.neotech.client.renderers.entity

import java.awt.Color

import com.dyonovan.neotech.common.entities.EntitySun
import com.teambr.bookshelf.client.shapes.DrawableShape.TEXTURE_MODE
import com.teambr.bookshelf.util.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.{Render, RenderManager}
import net.minecraft.client.renderer.texture.{TextureAtlasSprite, TextureMap}
import net.minecraft.util.ResourceLocation

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 3/12/2016
  */
class RenderSun (renderManager : RenderManager) extends Render[EntitySun](renderManager) {

    /**
      * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
      * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
      * (Render<T extends Entity>) and this method has signature public void func_76986_a(T entity, double d, double d1,
      * double d2, float f, float f1). But JAD is pre 1.5 so doe
      */
    override def doRender(entity: EntitySun, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float): Unit = {
        GlStateManager.pushMatrix()
        val texture = new ResourceLocation("minecraft", "blocks/lava_still")
        val tex : TextureAtlasSprite = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(texture.toString)
        GlStateManager.translate(x.toFloat, y.toFloat, z.toFloat)
        GlStateManager.enableRescaleNormal()
        GlStateManager.rotate(Minecraft.getMinecraft.theWorld.getTotalWorldTime + partialTicks, 0.75F, 1.0F, -0.5F)
        RenderUtils.renderSphere(0.23F, 16, 16, tex, TEXTURE_MODE.PANEL, new Color(255, 255, 255))
        GlStateManager.rotate(Minecraft.getMinecraft.theWorld.getTotalWorldTime + partialTicks, 0.75F, 1.0F, -0.5F)
        GlStateManager.disableDepth()
        RenderUtils.renderSphere(0.25F, 16, 16, tex, TEXTURE_MODE.PANEL, new Color(255, 255, 255, 150))
        GlStateManager.enableDepth()
        GlStateManager.popMatrix()
    }

    override def getEntityTexture(entity: EntitySun): ResourceLocation = TextureMap.locationBlocksTexture
}
