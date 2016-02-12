package com.dyonovan.neotech.client.renderers

import com.dyonovan.neotech.common.entities.EntityNet
import com.dyonovan.neotech.managers.ItemManager
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.entity.{RenderManager, Render}
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/12/2016
  */
class RenderNet(renderManager : RenderManager) extends Render[EntityNet](renderManager) {

    /**
      * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
      * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
      * (Render<T extends Entity>) and this method has signature public void func_76986_a(T entity, double d, double d1,
      * double d2, float f, float f1). But JAD is pre 1.5 so doe
      */
    override def doRender(entity: EntityNet, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
        GlStateManager.pushMatrix()
        GlStateManager.translate(x.toFloat, y.toFloat, z.toFloat)
        GlStateManager.enableRescaleNormal()
        GlStateManager.scale(0.5F, 0.5F, 0.5F)
        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F)
        GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F)
        this.bindTexture(TextureMap.locationBlocksTexture)

        val size = Math.max(entity.ticksExisted * 10.0F / 20, 1.0F)

        GlStateManager.scale(size, size, 1.0F)

        Minecraft.getMinecraft.getRenderItem.renderItem(this.getItemStackFromEntity(entity), ItemCameraTransforms.TransformType.GROUND)
        GlStateManager.disableRescaleNormal()
        GlStateManager.popMatrix()
        super.doRender(entity, x, y, z, entityYaw, partialTicks)
    }

    def getItemStackFromEntity(entityIn: EntityNet): ItemStack = {
        new ItemStack(ItemManager.mobNet, 1, 0)
    }

    override def getEntityTexture(entity: EntityNet): ResourceLocation = TextureMap.locationBlocksTexture
}
