package com.dyonovan.neotech.pipes.gui

import com.dyonovan.neotech.network.{OpenContainerGui, PacketDispatcher}
import com.dyonovan.neotech.pipes.types.ExtractionPipe
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
class GuiExtractionMenu(tile : ExtractionPipe[_, _]) extends GuiBase[ContainerGeneric](new ContainerGeneric(), 150, tile.getGUIHeight, "") {
    override def addComponents(): Unit = {
        components += new GuiComponentButton(25, 10, 100, 20, "Motherboard") {
            override def doAction(): Unit = {
                PacketDispatcher.net.sendToServer(new OpenContainerGui(tile.getPos, 0))
            }
        }
        if(tile.getUpgradeBoard != null && tile.getUpgradeBoard.hasControl) {
            components += new GuiComponentButton(25, 40, 100, 20, "Control") {
                override def doAction(): Unit = {
                    FMLClientHandler.instance().showGuiScreen(new GuiExtractionRedstone(tile))
                }
            }
        }
        if(tile.getUpgradeBoard != null && tile.getUpgradeBoard.hasExpansion) {
            components += new GuiComponentButton(25, 70, 100, 20, "Mode") {
                override def doAction(): Unit = {
                    FMLClientHandler.instance().showGuiScreen(new GuiExtractionMode(tile))
                }
            }
        }
    }
}
