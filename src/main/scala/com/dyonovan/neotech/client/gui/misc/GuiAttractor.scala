package com.dyonovan.neotech.client.gui.misc

import com.dyonovan.neotech.common.container.misc.{ContainerAttractor, ContainerFertilizer}
import com.dyonovan.neotech.common.tiles.misc.{TileAttractor, TileFertilizer}
import com.teambr.bookshelf.client.gui.GuiBase
import net.minecraft.entity.player.InventoryPlayer

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/16/2016
  */
class GuiAttractor(playerInv: InventoryPlayer, tileEntity: TileAttractor) extends
  GuiBase[ContainerAttractor](new ContainerAttractor(playerInv, tileEntity), 175, 165, "neotech.attractor.title") {

    override def addComponents(): Unit = {

    }
}
