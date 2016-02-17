package com.dyonovan.neotech.common.container.misc

import com.dyonovan.neotech.common.tiles.misc.TileAttractor
import com.teambr.bookshelf.common.container.BaseContainer
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
class ContainerAttractor(playerInventory: InventoryPlayer, tile: TileAttractor) extends BaseContainer(playerInventory, tile) {
    addInventoryGrid(70, 30, 2)
    addPlayerInventorySlots(8, 84)
}
