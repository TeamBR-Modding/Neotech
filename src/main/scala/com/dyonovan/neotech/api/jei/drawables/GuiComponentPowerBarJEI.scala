package com.dyonovan.neotech.api.jei.drawables

import java.awt.Color

import com.teambr.bookshelf.client.gui.component.display.GuiComponentPowerBarGradient
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
class GuiComponentPowerBarJEI(x: Int, y: Int, width: Int, height: Int, colorFull: Color)
        extends GuiComponentPowerBarGradient(x, y, width, height, colorFull) with IDrawableAnimated {

    var ticker = maxTick
    def maxTick = 20

    override def draw(minecraft: Minecraft): Unit = {}

    override def draw(minecraft: Minecraft, xOffset: Int, yOffset: Int): Unit = {
        super.render(xOffset, yOffset, Mouse.getX, Mouse.getY)
        super.renderOverlay(xOffset, yOffset, Mouse.getX, Mouse.getY)
        if(System.currentTimeMillis() % 100 == 0)
            ticker -= 1
        if(ticker < 0)
            ticker = maxTick
    }

    override def getEnergyPercent(scale: Int): Int = (ticker * scale) / maxTick
}
