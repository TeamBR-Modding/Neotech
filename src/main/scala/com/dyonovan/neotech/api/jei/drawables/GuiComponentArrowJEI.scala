package com.dyonovan.neotech.api.jei.drawables

import com.teambr.bookshelf.client.gui.component.display.GuiComponentArrow
import mezz.jei.api.gui.IDrawableAnimated
import net.minecraft.client.Minecraft
import org.lwjgl.input.Mouse

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
class GuiComponentArrowJEI(x: Int, y: Int) extends GuiComponentArrow(x, y) with IDrawableAnimated {
    var ticker = 0
    def maxTick = 20

    override def getCurrentProgress: Int = Math.min(((ticker * 24) / Math.max(maxTick, 0.001)).toInt, 24)

    override def draw(minecraft: Minecraft): Unit = {
        draw(minecraft, x, y)
    }

    override def draw(minecraft: Minecraft, xOffset: Int, yOffset: Int): Unit = {
        render(xOffset, yOffset, Mouse.getX, Mouse.getY)
        if(System.currentTimeMillis() % 100 == 0)
            ticker += 1
        if(ticker > maxTick)
            ticker = 0
    }
}
