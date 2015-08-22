package com.dyonovan.neotech.client.gui.misc

import com.dyonovan.neotech.common.container.misc.ContainerFertilizer
import com.dyonovan.neotech.common.tiles.misc.TileFertilizer
import com.teambr.bookshelf.client.gui.GuiBase
import net.minecraft.entity.player.EntityPlayer

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
class GuiFertilizer(player: EntityPlayer, tileEntity: TileFertilizer) extends
GuiBase[ContainerFertilizer](new ContainerFertilizer(player.inventory, tileEntity), 175, 165, "inventory.fertilizer.title") {

    override def addComponents(): Unit = {}
}
