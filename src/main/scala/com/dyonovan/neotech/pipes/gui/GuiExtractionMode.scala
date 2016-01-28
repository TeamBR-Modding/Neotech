package com.dyonovan.neotech.pipes.gui

import com.dyonovan.neotech.network.PacketDispatcher
import com.dyonovan.neotech.pipes.types.{AdvancedPipe, InterfacePipe}
import com.teambr.bookshelf.client.gui.GuiBase
import com.teambr.bookshelf.client.gui.component.control.GuiComponentButton
import com.teambr.bookshelf.common.container.ContainerGeneric
import net.minecraftforge.fml.client.FMLClientHandler

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/15/2016
  */
class GuiExtractionMode(tile : AdvancedPipe) extends GuiBase[ContainerGeneric](new ContainerGeneric(), 150, 50, "Extraction Mode") {
    override def addComponents(): Unit = {
        components += new GuiComponentButton(5, 20, 15, 20, "<") {
            override def doAction(): Unit = {
                tile.moveMode(-1)
                tile.sendValueToServer(AdvancedPipe.MODE_FIELD_ID, tile.mode)
            }
        }
        components += new GuiComponentButton(25, 20, 100, 20, tile.getModeName) {
            override def doAction(): Unit = {}
            override def renderOverlay(i : Int, j : Int) : Unit = {
                setText(tile.getModeName)
                super.renderOverlay(i, j)
            }
        }
        components += new GuiComponentButton(130, 20, 15, 20, ">") {
            override def doAction(): Unit = {
                tile.moveMode(1)
                tile.sendValueToServer(AdvancedPipe.MODE_FIELD_ID, tile.mode)
            }
        }
    }
}
