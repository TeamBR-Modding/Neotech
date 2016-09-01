package com.teambrmodding.neotech.client.renderers.tiles

import com.teambrmodding.neotech.common.tiles.machines.TileGrinder
import gnu.trove.map.hash.THashMap
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.{GlStateManager, RenderHelper}
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemShield

/**
  * This file was created for Lux et Umbra
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 8/31/2016
  */
class TileGrinderRenderer[T <: TileGrinder] extends TileEntitySpecialRenderer[T] {
    override def renderTileEntityAt(grinder : T, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int): Unit = {
        GlStateManager.pushMatrix()
        GlStateManager.translate(x + 0.5, y, z + 0.5)
        for(iterator <- 4 until 7) {
            val stack = grinder.getStackInSlot(iterator)
            if(stack != null) {
                val entityItem = new EntityItem(getWorld, 0.0, 0.0, 0.0, stack)
                entityItem.motionX = 0
                entityItem.motionY = 0
                entityItem.motionZ = 0
                entityItem.hoverStart = 0
                entityItem.rotationYaw = 0
                GlStateManager.pushMatrix()

                RenderHelper.enableGUIStandardItemLighting()
                val renderManager = Minecraft.getMinecraft.getRenderManager

                renderManager.setRenderShadow(false)
                GlStateManager.pushAttrib()
                GlStateManager.scale(0.45, 0.45, 0.45)

                val rotation = (iterator - 3) * 120
                val xRot = 0.25 * Math.cos(Math.toRadians(rotation))
                val zRot = 0.25 * Math.sin(Math.toRadians(rotation))
                renderManager.doRenderEntity(entityItem, xRot / 0.45, -0.1, zRot / 0.45, 0.0F, 0, true)

                GlStateManager.popAttrib()
                GlStateManager.enableLighting()
                renderManager.setRenderShadow(true)
                GlStateManager.popMatrix()
            }
        }
        GlStateManager.popMatrix()
    }
}
