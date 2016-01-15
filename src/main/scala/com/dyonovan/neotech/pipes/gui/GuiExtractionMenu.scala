package com.dyonovan.neotech.pipes.gui

import com.dyonovan.neotech.pipes.types.ExtractionPipe
import com.teambr.bookshelf.client.gui.GuiBase
import com.teambr.bookshelf.common.container.ContainerGeneric

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
    override def addComponents(): Unit = {}
}
