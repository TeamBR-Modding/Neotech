package com.dyonovan.neotech.pipes.gui

import com.dyonovan.neotech.network.{UpdateExtractionPipeValue, OpenContainerGui, PacketDispatcher}
import com.dyonovan.neotech.pipes.types.ExtractionPipe
import com.teambr.bookshelf.client.gui.GuiBase
import com.teambr.bookshelf.client.gui.component.control.GuiComponentButton
import com.teambr.bookshelf.common.container.ContainerGeneric
import com.teambr.bookshelf.network.PacketManager
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
class GuiExtractionRedstone(tile : ExtractionPipe[_, _]) extends GuiBase[ContainerGeneric](new ContainerGeneric(), 150, 50, "Redstone Mode") {
    override def addComponents(): Unit = {
        components += new GuiComponentButton(5, 20, 15, 20, "<") {
            override def doAction(): Unit = {
                tile.moveRedstoneMode(-1)
                tile.sendValueToServer(tile.REDSTONE_FIELD_ID, tile.redstone)
                FMLClientHandler.instance().showGuiScreen(new GuiExtractionRedstone(tile))
            }
        }
        components += new GuiComponentButton(25, 20, 100, 20, tile.getRedstoneModeName) {
            override def doAction(): Unit = {}
        }
        components += new GuiComponentButton(130, 20, 15, 20, ">") {
            override def doAction(): Unit = {
                tile.moveRedstoneMode(1)
                tile.sendValueToServer(tile.REDSTONE_FIELD_ID, tile.redstone)
                FMLClientHandler.instance().showGuiScreen(new GuiExtractionRedstone(tile))
            }
        }
    }
}
