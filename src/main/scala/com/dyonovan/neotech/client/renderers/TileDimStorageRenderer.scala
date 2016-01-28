package com.dyonovan.neotech.client.renderers

import com.dyonovan.neotech.common.tiles.storage.TileDimStorage
import com.teambr.bookshelf.collections.Location
import com.teambr.bookshelf.common.blocks.properties.PropertyRotation
import com.teambr.bookshelf.util.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.{RenderHelper, OpenGlHelper, GlStateManager}
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.ForgeHooksClient
import org.lwjgl.opengl.{GL12, GL11}

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
class TileDimStorageRenderer extends TileRenderHelper[TileDimStorage]{

    override def renderTileEntityAt(tile: TileDimStorage, x: Double, y: Double, z: Double,  partialTicks: Float, breakPart: Int): Unit = {
        if (tile.getQty > 0) {
            val savedGLState = modifyGLState(Array(GL11.GL_BLEND, GL11.GL_LIGHTING), null)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GlStateManager.enableLighting()
            for(dir <- EnumFacing.values()) {
                if(tile.getWorld.getBlockState(tile.getPos).getValue(PropertyRotation.FOUR_WAY) == dir) {

                    mc.entityRenderer.disableLightmap()
                    RenderHelper.enableGUIStandardItemLighting()
                    setLight(tile, dir)

                    val stack = tile.getStackInSlot(0).copy()
                    stack.stackSize = 1
                    renderStackOnBlock(stack, dir,  tile.getWorld.getBlockState(tile.getPos).getValue(PropertyRotation.FOUR_WAY), new LocationDouble(x, y, z), 6.8F, 72.0F, 112.0F)

                    RenderHelper.disableStandardItemLighting()
                    mc.entityRenderer.enableLightmap()

                    renderTextOnBlock(tile.getQty.toString, dir, dir, new LocationDouble(x, y, z), 2.6F, 129.0F, 55.0F, 0xFFFFFF, TileRenderHelper.ALIGNCENTER)
                }
            }

            RenderUtils.restoreRenderState()
            RenderUtils.restoreColor()
            restoreGlState(savedGLState)
        }
    }
}
