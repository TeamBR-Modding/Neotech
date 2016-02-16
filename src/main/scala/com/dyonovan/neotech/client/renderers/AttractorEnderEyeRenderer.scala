package com.dyonovan.neotech.client.renderers

import com.dyonovan.neotech.common.tiles.misc.TileAttractor
import com.teambr.bookshelf.util.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.{RenderHelper, GlStateManager}
import net.minecraft.client.renderer.entity.{RenderEntityItem, RenderManager}
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.entity.item.EntityItem
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/16/2016
  */
class AttractorEnderEyeRenderer extends TileEntitySpecialRenderer[TileAttractor] {

    override def renderTileEntityAt(te: TileAttractor, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int): Unit = {
        GlStateManager.pushMatrix()
        GlStateManager.pushAttrib()
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5)
        GlStateManager.scale(0.25F, 0.25F, 0.25F)

        val entity = new EntityItem(getWorld, 0, 0, 0, new ItemStack(Items.ender_eye, 1))

        val player = Minecraft.getMinecraft.thePlayer
        var angle = Math.toDegrees(Math.atan2(te.getPos.getZ + 0.5 - player.posZ, te.getPos.getX + 0.5 - player.posX))
        if(angle < 0)
            angle += 360
        GlStateManager.rotate(-angle.toFloat - 90, 0.0F, 1.0F, 0.0F)

        val itemRenderer = Minecraft.getMinecraft.getRenderItem
        RenderHelper.enableStandardItemLighting()
        itemRenderer.renderItem(entity.getEntityItem, ItemCameraTransforms.TransformType.FIXED)
        RenderHelper.disableStandardItemLighting()

        GlStateManager.enableLighting()
        RenderUtils.bindMinecraftBlockSheet
        GlStateManager.popAttrib()
        GlStateManager.popMatrix()
    }
}
