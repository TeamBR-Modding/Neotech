package com.dyonovan.neotech.api.jei.drawables

import com.teambr.bookshelf.client.gui.component.control.GuiComponentItemStackButton
import mezz.jei.api.gui.IDrawable
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack

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
class GuiComponentItemStackButtonJEI(x : Int, y : Int, stack : ItemStack)
    extends GuiComponentItemStackButton(x, y, stack) with IDrawable {
    override def draw(minecraft: Minecraft): Unit = {
        draw(minecraft, x, y)
    }
    override def draw(minecraft: Minecraft, xOffset: Int, yOffset: Int): Unit = {
        render(xOffset, yOffset, 0, 0)
        if(stack != null)
            renderOverlay(xOffset, yOffset, 0, 0)
    }
    override def doAction(): Unit = {}
}
