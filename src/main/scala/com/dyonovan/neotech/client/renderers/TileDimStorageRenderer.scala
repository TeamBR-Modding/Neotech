package com.dyonovan.neotech.client.renderers

import com.dyonovan.neotech.common.tiles.storage.TileDimStorage
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import org.lwjgl.opengl.GL11

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 1/24/2016
  */
class TileDimStorageRenderer extends TileEntitySpecialRenderer[TileDimStorage]{

    override def renderTileEntityAt(tile: TileDimStorage, v: Double, v1: Double, v2: Double, v3: Float, i: Int): Unit = {
        if (tile.getQty() > 0) {
            GlStateManager.pushMatrix()
            GlStateManager.pushAttrib()

            this.getFontRenderer.drawString(tile.getQty().toString, 0, 0, 2)

            GlStateManager.popAttrib()
            GlStateManager.popMatrix()
        }
    }
}
