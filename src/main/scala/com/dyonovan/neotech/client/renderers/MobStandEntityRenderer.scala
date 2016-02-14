package com.dyonovan.neotech.client.renderers

import com.dyonovan.neotech.common.tiles.misc.TileMobStand
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.{GlStateManager, OpenGlHelper, RenderHelper}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/13/2016
  */
class MobStandEntityRenderer[T <: TileMobStand] extends TileEntitySpecialRenderer[T] {

    override def renderTileEntityAt(te: T, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int): Unit = {
        if (te.entity == null) return
        val ent = te.entity

        GlStateManager.enableColorMaterial()
        GlStateManager.pushMatrix()
        GlStateManager.scale(1.0F - te.scale, 1.0F, 1.0F - te.scale)
        RenderHelper.enableStandardItemLighting()
        val renderManager: RenderManager = Minecraft.getMinecraft.getRenderManager

        ent.posX = te.getPos.getX + 0.5D
        ent.posY = te.getPos.getY + 1
        ent.posZ = te.getPos.getZ + 0.5D
        ent.setRotationYawHead(0.0F)

        renderManager.setRenderShadow(false)
        renderManager.renderEntityStatic(ent, 1.0F, false)
        renderManager.setRenderShadow(true)

        GlStateManager.popMatrix()
        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit)
        GlStateManager.disableTexture2D()
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
    }
}
