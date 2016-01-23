package com.dyonovan.neotech.common.container.storage

import com.dyonovan.neotech.common.tiles.storage.TileFlushableChest
import com.teambr.bookshelf.common.container.BaseContainer
import net.minecraft.entity.player.InventoryPlayer

/**
  * Created by Dyonovan on 1/22/2016.
  */
class ContainerFlushableChest(inventory: InventoryPlayer, tile: TileFlushableChest) extends BaseContainer(inventory, tile) {
    addInventoryGrid(8, 16, 9)
    addPlayerInventorySlots(8, 84)
}
