package com.teambrmodding.neotech.common.container.machines

import com.teambrmodding.neotech.common.tiles.machines.TileGrinder
import com.teambr.bookshelf.common.container.BaseContainer
import net.minecraft.entity.player.InventoryPlayer

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/14/2016
  */
class ContainerGrinder(playerInventory : InventoryPlayer, tile : TileGrinder) extends
        BaseContainer(playerInventory, tile) {
    addInventoryLine(62, 22, 0, 3)
    addSlotToContainer(new RestrictedSlot(tile, 3, 80, 44))
    addInventoryLine(62, 66, 4, 3)
    addPlayerInventorySlots(8, 99)
}
