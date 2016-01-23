package com.dyonovan.neotech.pipes.gui

import com.dyonovan.neotech.network.{OpenContainerGui, PacketDispatcher}
import com.dyonovan.neotech.pipes.tiles.energy.{EnergyExtractionPipe, EnergySinkPipe}
import com.dyonovan.neotech.pipes.types.{AdvancedPipe, ExtractionPipe}
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
class GuiAdvancedPipeMenu(tile : AdvancedPipe) extends
GuiBase[ContainerGeneric](new ContainerGeneric(), 150,
    if((tile.getUpgradeBoard != null && tile.getUpgradeBoard.hasControl) && (tile.isInstanceOf[EnergyExtractionPipe] || tile.isInstanceOf[EnergySinkPipe])) tile.getGUIHeight - 30 else tile.getGUIHeight, " ") {
    override def addComponents(): Unit = {
        components += new GuiComponentButton(25, 10, 100, 20, "Motherboard") {
            override def doAction(): Unit = {
                PacketDispatcher.net.sendToServer(new OpenContainerGui(tile.getPos, 0))
            }
        }
        if(tile.getUpgradeBoard != null && tile.getUpgradeBoard.hasControl) {
            components += new GuiComponentButton(25, 40, 100, 20, "Redstone Control") {
                override def doAction(): Unit = {
                    FMLClientHandler.instance().showGuiScreen(new GuiAdvancedPipeRedstone(tile))
                }
            }
            components += new GuiComponentButton(25, 70, 100, 20, "Connections") {
                override def doAction(): Unit = {
                    FMLClientHandler.instance().showGuiScreen(new GuiAdvancedPipeConnections(tile.connections, tile))
                }
            }
            if(!tile.isInstanceOf[EnergyExtractionPipe] && !tile.isInstanceOf[EnergySinkPipe]) {
                components += new GuiComponentButton(25, 100, 100, 20, "Filter") {
                    override def doAction(): Unit = {
                        PacketDispatcher.net.sendToServer(new OpenContainerGui(tile.getPos, 1))
                    }
                }
            }
        }
        if(tile.getUpgradeBoard != null && tile.getUpgradeBoard.hasExpansion) {
            var x = if(tile.isInstanceOf[EnergyExtractionPipe] || tile.isInstanceOf[EnergySinkPipe]) 70 else 100
            if(tile.isInstanceOf[ExtractionPipe[_, _]]) {
                x = 130
                components += new GuiComponentButton(25, if (tile.getUpgradeBoard.hasControl) x else x - 60, 100, 20, "Mode") {
                    override def doAction(): Unit = {
                        FMLClientHandler.instance().showGuiScreen(new GuiExtractionMode(tile))
                    }
                }
            }
            components += new GuiComponentButton(25, if(tile.getUpgradeBoard.hasControl) x + 30 else x - 30, 100, 20, "Frequency") {
                override def doAction(): Unit = {
                    FMLClientHandler.instance().showGuiScreen(new GuiAdvancePipeFrequency(tile))
                }
            }
        }
    }
}
