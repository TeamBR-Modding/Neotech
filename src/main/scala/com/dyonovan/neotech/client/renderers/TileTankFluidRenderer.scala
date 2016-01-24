package com.dyonovan.neotech.client.renderers

import com.dyonovan.neotech.common.tiles.storage.TileTank
import com.teambr.bookshelf.util.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import org.lwjgl.opengl.GL11

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/24/2016
  */
class TileTankFluidRenderer extends TileEntitySpecialRenderer[TileTank] {
    override def renderTileEntityAt(te: TileTank, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int): Unit = {
        if(te.getCurrentFluid != null) {
            GlStateManager.pushMatrix()
            GlStateManager.pushAttrib()

            GlStateManager.translate(x, y, z)

            RenderUtils.bindMinecraftBlockSheet
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GlStateManager.disableLighting()

            val fluidIcon: TextureAtlasSprite = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(te.getCurrentFluid.getStill(te.tank.getFluid).toString)
            RenderUtils.renderCubeWithTexture(2.01 / 16.0, 1.01 / 16, 2.01 / 16.0, 13.99 / 16.0, (te.getFluidLevelScaled + 1.01) / 16, 13.99 / 16.0,
                fluidIcon.getMinU, fluidIcon.getMinV, fluidIcon.getMaxU, fluidIcon.getMaxV)

            GlStateManager.enableLighting()
            RenderUtils.bindMinecraftBlockSheet

            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GlStateManager.popAttrib()
            GlStateManager.popMatrix()
        }
    }
}
