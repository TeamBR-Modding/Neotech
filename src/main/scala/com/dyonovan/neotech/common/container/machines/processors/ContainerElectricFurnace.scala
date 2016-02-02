package com.dyonovan.neotech.common.container.machines.processors

import com.dyonovan.neotech.common.container.machines.ContainerAbstractMachine
import com.dyonovan.neotech.common.container.slot.SlotFurnaceOutputItemHandler
import com.dyonovan.neotech.common.tiles.machines.processors.TileElectricFurnace
import net.minecraft.entity.player.InventoryPlayer

class ContainerElectricFurnace(playerInventory: InventoryPlayer, tile: TileElectricFurnace) extends
        ContainerAbstractMachine(playerInventory, tile) {

    addSlotToContainer(new RestrictedSlot(tile, 0, 56, 35))
    addSlotToContainer(new SlotFurnaceOutputItemHandler(playerInventory.player, tile, 1, 116, 35))
    addPlayerInventorySlots(8, 84)
}
