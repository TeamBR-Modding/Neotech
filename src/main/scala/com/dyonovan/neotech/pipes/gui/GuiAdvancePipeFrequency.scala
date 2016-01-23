package com.dyonovan.neotech.pipes.gui

import com.dyonovan.neotech.pipes.types.AdvancedPipe
import com.teambr.bookshelf.client.gui.GuiBase
import com.teambr.bookshelf.client.gui.component.control.GuiComponentSetNumber
import com.teambr.bookshelf.common.container.ContainerGeneric

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/22/2016
  */
class GuiAdvancePipeFrequency(tile : AdvancedPipe) extends GuiBase[ContainerGeneric](new ContainerGeneric, 100, 50, "Frequency") {
    override def addComponents(): Unit = {
        components += new GuiComponentSetNumber(20, 20, 60, tile.frequency, 0, 100) {
            override def setValue(i: Int): Unit = {
                tile.setVariable(AdvancedPipe.FREQUENCY, if(i < 0) 0 else if(i > 100) 100 else i)
                tile.sendValueToServer(AdvancedPipe.FREQUENCY, tile.getVariable(AdvancedPipe.FREQUENCY))
            }
        }
    }
}
