package com.dyonovan.neotech.client.gui.misc

import com.dyonovan.neotech.common.container.misc.ContainerFertilizer
import com.dyonovan.neotech.common.tiles.misc.TileFertilizer
import com.teambr.bookshelf.client.gui.GuiBase
import com.teambr.bookshelf.client.gui.component.control.GuiComponentCheckBox
import net.minecraft.entity.player.InventoryPlayer

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 22, 2015
 */
class GuiFertilizer(playerInv: InventoryPlayer, tileEntity: TileFertilizer) extends
GuiBase[ContainerFertilizer](new ContainerFertilizer(playerInv, tileEntity), 175, 165, "neotech.fertilizer.title") {

    override def addComponents(): Unit = {
        components += new GuiComponentCheckBox(60, 70, "neotech.button.disable", tileEntity.disabled) {
            override def setValue(bool: Boolean): Unit = {
                tileEntity.setVariable(0, 0)
                tileEntity.sendValueToServer(0, 0)
            }
        }
    }
}
