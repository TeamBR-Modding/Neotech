package com.dyonovan.neotech.client.gui.machines

import java.awt.Color
import java.text.DecimalFormat

import com.dyonovan.neotech.common.container.machines.ContainerGrinder
import com.dyonovan.neotech.common.tiles.machines.TileGrinder
import com.teambr.bookshelf.client.gui.GuiBase
import com.teambr.bookshelf.client.gui.component.display.GuiComponentText
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.text.translation.I18n

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/14/2016
  */
class GuiGrinder(player : EntityPlayer, tile : TileGrinder) extends
    GuiBase[ContainerGrinder](new ContainerGrinder(player.inventory, tile), 175, 180, "neotech.grinder.title"){
    override def addComponents(): Unit = {
        components += new GuiComponentText(I18n.translateToLocal("neotech.grinder.input"), 8, 26, new Color(77, 77, 77))
        components += new GuiComponentText(I18n.translateToLocal("neotech.grinder.grinding"), 8, 48, new Color(77, 77, 77))
        components += new GuiComponentText(I18n.translateToLocal("neotech.grinder.output"), 8, 70, new Color(77, 77, 77))
        components += new GuiComponentText(new DecimalFormat("#.##").format((tile.progress / tile.MAX_PROGRESS.toDouble) * 100) + "%", 100, 48, new Color(77, 77, 77)) {
            override def renderOverlay(i : Int, j : Int, x : Int, y : Int) = {
                this.setText(new DecimalFormat("#.##").format((tile.progress / tile.MAX_PROGRESS.toDouble) * 100) + "%")
                super.renderOverlay(i, j, x, y)
            }
        }
    }
}
