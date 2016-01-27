package com.dyonovan.neotech.common.container.machines

import com.dyonovan.neotech.common.container.slot.SlotFurnaceOutputItemHandler
import com.dyonovan.neotech.common.tiles.machines.TileElectricFurnace
import com.teambr.bookshelf.common.container.BaseContainer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.SlotFurnaceOutput

class ContainerElectricFurnace(playerInventory: InventoryPlayer, tile: TileElectricFurnace) extends
        BaseContainer(playerInventory, tile) {

    addSlotToContainer(new RestrictedSlot(tile, 0, 56, 35))
    addSlotToContainer(new SlotFurnaceOutputItemHandler(playerInventory.player, tile, 1, 116, 35))
    addPlayerInventorySlots(8, 84)
}
