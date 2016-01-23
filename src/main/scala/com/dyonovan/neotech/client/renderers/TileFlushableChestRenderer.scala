package com.dyonovan.neotech.client.renderers

import com.dyonovan.neotech.common.tiles.storage.TileFlushableChest
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.BlockManager
import com.teambr.bookshelf.common.blocks.properties.PropertyRotation
import net.minecraft.client.model.ModelChest
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.ResourceLocation

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/22/2016
  */
class TileFlushableChestRenderer[T <: TileFlushableChest] extends TileEntitySpecialRenderer[T] {

    val location = new ResourceLocation(Reference.MOD_ID, "textures/blocks/flushablechest.png")
    val modelChest = new ModelChest

    override def renderTileEntityAt(tile: T, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int): Unit = {
        if(tile == null)
            return

        var facing = 3

        if(tile.hasWorldObj && tile.getWorld.getBlockState(tile.getPos).getBlock == BlockManager.flushableChest) {
            facing = tile.getWorld.getBlockState(tile.getPos).getValue(PropertyRotation.FOUR_WAY).getIndex
        }

        if(destroyStage >= 0) {
            bindTexture(TileEntitySpecialRenderer.DESTROY_STAGES(destroyStage))
            GlStateManager.matrixMode(5890)
            GlStateManager.pushMatrix()
            GlStateManager.scale(4.0F, 4.0F, 1.0F)
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F)
            GlStateManager.matrixMode(5888)
        } else
            bindTexture(location)

        GlStateManager.pushMatrix()
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)
        GlStateManager.translate(x.toFloat, y.toFloat + 1.0F, z.toFloat + 1.0F)
        GlStateManager.scale(1.0F, -1F, -1F)
        GlStateManager.translate(0.5F, 0.5F, 0.5F)
        var k = 0
        if (facing == 2) {
            k = 180
        }
        if (facing == 3) {
            k = 0
        }
        if (facing == 4) {
            k = 90
        }
        if (facing == 5) {
            k = -90
        }
        GlStateManager.rotate(k, 0.0F, 1.0F, 0.0F)
        GlStateManager.translate(-0.5F, -0.5F, -0.5F)
        var lidangle : Float = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * partialTicks
        lidangle = 1.0F - lidangle
        lidangle = 1.0F - lidangle * lidangle * lidangle
        modelChest.chestLid.rotateAngleX = -(lidangle * Math.PI.toFloat / 2.0F)
        // Render the chest itself
        modelChest.renderAll()
        if (destroyStage >= 0) {
            GlStateManager.matrixMode(5890)
            GlStateManager.popMatrix()
            GlStateManager.matrixMode(5888)
        }

        GlStateManager.popMatrix()
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)
    }
}
