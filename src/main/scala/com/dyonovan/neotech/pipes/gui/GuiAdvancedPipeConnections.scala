package com.dyonovan.neotech.pipes.gui

import com.dyonovan.neotech.pipes.collections.ConnectedSides
import com.dyonovan.neotech.pipes.types.{SinkPipe, ExtractionPipe}
import com.teambr.bookshelf.client.gui.GuiBase
import com.teambr.bookshelf.client.gui.component.control.GuiComponentCheckBox
import com.teambr.bookshelf.common.container.ContainerGeneric
import com.teambr.bookshelf.common.tiles.traits.Syncable
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.client.FMLClientHandler

import scala.reflect.internal.util.StringOps

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/21/2016
  */
class GuiAdvancedPipeConnections(sides : ConnectedSides, tile : Syncable) extends GuiBase[ContainerGeneric](new ContainerGeneric, 75, 110, "Connections") {
    override def addComponents(): Unit = {
        for(x <- 0 until EnumFacing.values().size) {
            components += new GuiComponentCheckBox(8, 20 + (x * 15), EnumFacing.getFront(x).getName.toUpperCase, sides.get(x)) {
                override def setValue(bool: Boolean): Unit = {
                    tile.setVariable(2, x)
                    tile.sendValueToServer(2, x)
                }
            }
        }
    }
}
