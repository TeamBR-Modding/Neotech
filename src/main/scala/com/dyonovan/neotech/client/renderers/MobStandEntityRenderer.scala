package com.dyonovan.neotech.client.renderers

import com.dyonovan.neotech.common.tiles.misc.TileMobStand
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.{GlStateManager, RenderHelper}

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
        GlStateManager.translate(x + 0.5, y + 1.0D, z + 0.5)
        if(te.fitToBlock){
            val heightScale = 0.5F / ent.height
            val widthScale = 0.5F / ent.width
            if(heightScale >= widthScale)
                GlStateManager.scale(heightScale, heightScale, heightScale)
            else
                GlStateManager.scale(widthScale, widthScale, widthScale)
        } else
            GlStateManager.scale(1.0F - te.scale, 1.0F - te.scale, 1.0F - te.scale)

        if(te.lookAtPlayer) {
            val player = Minecraft.getMinecraft.thePlayer
            var angle = Math.toDegrees(Math.atan2(te.getPos.getZ + 0.5 - player.posZ, te.getPos.getX + 0.5 - player.posX))
            if(angle < 0)
                angle += 360
            GlStateManager.rotate(-angle.toFloat - 90, 0.0F, 1.0F, 0.0F)
        } else
            GlStateManager.rotate(te.rotation * 360, 0.0F, 1.0F, 0.0F)
        RenderHelper.enableStandardItemLighting()
        val renderManager: RenderManager = Minecraft.getMinecraft.getRenderManager

        ent.posX = 0
        ent.posY = 0
        ent.posZ = 0
        ent.setRotationYawHead(0.0F)

        renderManager.setRenderShadow(false)
        renderManager.renderEntityWithPosYaw(ent, 0.0, 0.0, 0.0, 0.0F, partialTicks)
        renderManager.setRenderShadow(true)

        GlStateManager.popMatrix()
    }
}
