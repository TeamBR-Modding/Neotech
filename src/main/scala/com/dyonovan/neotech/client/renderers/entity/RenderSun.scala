package com.dyonovan.neotech.client.renderers.entity

import java.awt.Color

import com.dyonovan.neotech.universe.entities.{EnumSunType, EntitySun}
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
        val texture = new ResourceLocation("neotech", "blocks/metal_still")
        var tex : TextureAtlasSprite = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(texture.toString)

        val radius = entity.getDataWatcher.getWatchableObjectFloat(entity.DATA_WATCHER_RADIUS)
        val sunType = EnumSunType.values()(entity.getDataWatcher.getWatchableObjectInt(entity.DATA_WATCHER_TYPE))

        if(sunType == EnumSunType.INERT)
            tex = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(new ResourceLocation("minecraft", "blocks/stone").toString)

        GlStateManager.translate(x.toFloat, y.toFloat, z.toFloat)
        GlStateManager.enableRescaleNormal()
        GlStateManager.rotate(Minecraft.getMinecraft.theWorld.getTotalWorldTime + partialTicks, 0.75F, 1.0F, -0.5F)
        RenderUtils.renderSphere(radius, 16, 16, tex, TEXTURE_MODE.PANEL, sunType.getColor)
        GlStateManager.rotate(Minecraft.getMinecraft.theWorld.getTotalWorldTime + partialTicks, 0.75F, 1.0F, -0.5F)
        val color = new Color(sunType.getColor.getRed, sunType.getColor.getGreen, sunType.getColor.getBlue, 155)

        RenderUtils.renderSphere(radius + sunType.getSecondLayerOffset, 16, 16, tex, TEXTURE_MODE.PANEL, color)

        GlStateManager.popMatrix()
        RenderUtils.restoreRenderState()
    }

    override def getEntityTexture(entity: EntitySun): ResourceLocation = TextureMap.locationBlocksTexture
}
