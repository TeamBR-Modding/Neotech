package com.dyonovan.neotech.client.renderers.tiles

import java.awt.Color

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
        if(te.getCurrentFluid != null || te.getTier == 5) {
            GlStateManager.pushMatrix()
            GlStateManager.pushAttrib()

            GlStateManager.translate(x, y, z)

            RenderUtils.bindMinecraftBlockSheet
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GlStateManager.disableLighting()

            if (te.getTier < 5) {
                val fluidIcon: TextureAtlasSprite = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(te.getCurrentFluid.getStill(te.tank.getFluid).toString)
                RenderUtils.setColor(Color.decode(te.tank.getFluid.getFluid.getColor.toString))
                if(te.tank.getFluid.getFluid.isGaseous(te.tank.getFluid))
                    GlStateManager.translate(0, 1 - (te.getFluidLevelScaled / 16) - 0.1, 0)
                RenderUtils.renderCubeWithTexture(2.01 / 16.0, 1.01 / 16, 2.01 / 16.0, 13.99 / 16.0, te.getFluidLevelScaled / 16, 13.99 / 16.0,
                    fluidIcon.getMinU, fluidIcon.getMinV, fluidIcon.getMaxU, fluidIcon.getMaxV)
            } else if (te.getTier == 5) {
                val voidIcon: TextureAtlasSprite = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite("minecraft:blocks/portal")
                RenderUtils.renderCubeWithTexture(2.01 / 16.0, 1.01 / 16, 2.01 / 16.0, 13.99 / 16.0, 13.99 / 16, 13.99 / 16.0,
                    voidIcon.getMinU, voidIcon.getMinV, voidIcon.getMaxU, voidIcon.getMaxV)
            }
            RenderUtils.restoreColor()
            GlStateManager.enableLighting()
            RenderUtils.bindMinecraftBlockSheet

            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GlStateManager.popAttrib()
            GlStateManager.popMatrix()
        }
    }
}
