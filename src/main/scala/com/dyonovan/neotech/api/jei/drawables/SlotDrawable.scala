package com.dyonovan.neotech.api.jei.drawables

import com.teambr.bookshelf.util.RenderUtils
import mezz.jei.api.gui.IDrawable
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, WorldRenderer, Tessellator}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/21/2016
  */
class SlotDrawable(x : Int, y : Int, isLarge : Boolean = false) extends IDrawable {
    lazy val zLevel = 0

    def drawTexturedModalRect (x: Int, y: Int, textureX: Int, textureY: Int, width: Int, height: Int) : Unit = {
        val f: Float = 0.00390625F
        val f1: Float = 0.00390625F
        val tessellator: Tessellator = Tessellator.getInstance
        val worldrenderer: WorldRenderer = tessellator.getWorldRenderer
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
        worldrenderer.pos((x + 0).toDouble, (y + height).toDouble, this.zLevel.toDouble).tex(((textureX + 0).toFloat * f).toDouble, ((textureY + height).toFloat * f1).toDouble).endVertex()
        worldrenderer.pos((x + width).toDouble, (y + height).toDouble, this.zLevel.toDouble).tex(((textureX + width).toFloat * f).toDouble, ((textureY + height).toFloat * f1).toDouble).endVertex()
        worldrenderer.pos((x + width).toDouble, (y + 0).toDouble, this.zLevel.toDouble).tex(((textureX + width).toFloat * f).toDouble, ((textureY + 0).toFloat * f1).toDouble).endVertex()
        worldrenderer.pos((x + 0).toDouble, (y + 0).toDouble, this.zLevel.toDouble).tex(((textureX + 0).toFloat * f).toDouble, ((textureY + 0).toFloat * f1).toDouble).endVertex()
        tessellator.draw()
    }

    override def getHeight: Int = if(isLarge) 26 else 18

    override def getWidth: Int = if(isLarge) 26 else 18

    override def draw(minecraft: Minecraft): Unit = {
        draw(minecraft, if(isLarge) x - 4 else x, if(isLarge) y - 4 else y)
    }

    override def draw(minecraft: Minecraft, xOffset: Int, yOffset: Int): Unit = {
        GlStateManager.pushMatrix()

        RenderUtils.bindGuiComponentsSheet()

        drawTexturedModalRect(xOffset, yOffset, 0, if(isLarge) 38 else 20, getWidth, getHeight)

        GlStateManager.popMatrix()
    }
}
