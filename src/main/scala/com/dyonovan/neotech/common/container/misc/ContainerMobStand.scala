package com.dyonovan.neotech.common.container.misc

import com.dyonovan.neotech.common.tiles.misc.{TileMobStand, TileFertilizer}
import com.teambr.bookshelf.common.container.BaseContainer
import net.minecraft.entity.player.InventoryPlayer

/**
  * Created by Dyonovan on 2/14/2016.
  */
class ContainerMobStand(playerInventory: InventoryPlayer, tile: TileMobStand) extends BaseContainer(playerInventory, tile) {
    addSlotToContainer(new RestrictedSlot(tile, 0, 78, 30))
    addPlayerInventorySlots(8, 84)
}
