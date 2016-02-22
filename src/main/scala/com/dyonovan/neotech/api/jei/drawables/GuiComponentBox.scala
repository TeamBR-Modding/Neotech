package com.dyonovan.neotech.api.jei.drawables

import com.teambr.bookshelf.client.gui.component.display.GuiComponentFluidTank
import com.teambr.bookshelf.util.RenderUtils
import mezz.jei.api.gui.IDrawable
import net.minecraft.client.Minecraft
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11

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
class GuiComponentBox(x: Int, y: Int, width: Int, height: Int)
    extends GuiComponentFluidTank(x, y, width, height, null) with IDrawable {

    override def render(guiLeft: Int, guiTop: Int, mouseX : Int, mouseY : Int) {
            GL11.glPushMatrix()
            GL11.glTranslated(xPos, yPos, 0)
            RenderUtils.bindGuiComponentsSheet()
            renderer.render(this, 0, 0, width, height)
            GL11.glPopMatrix()
    }

    override def draw(minecraft: Minecraft): Unit = {
        draw(minecraft, x, y)
    }

    override def draw(minecraft: Minecraft, xOffset: Int, yOffset: Int): Unit = {
        render(x, y, Mouse.getX, Mouse.getY)
    }
}
