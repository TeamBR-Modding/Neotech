package com.teambrmodding.neotech.common.container.machines.processors

import com.teambrmodding.neotech.common.container.machines.ContainerAbstractMachine
import com.teambrmodding.neotech.common.container.slot.SlotFurnaceOutputItemHandler
import com.teambrmodding.neotech.common.tiles.machines.processors.TileElectricFurnace
import net.minecraft.entity.player.InventoryPlayer

class ContainerElectricFurnace(playerInventory: InventoryPlayer, tile: TileElectricFurnace) extends
        ContainerAbstractMachine(playerInventory, tile) {

    addSlotToContainer(new RestrictedSlot(tile, 0, 56, 35))
    addSlotToContainer(new SlotFurnaceOutputItemHandler(playerInventory.player, tile, 1, 116, 35))
    addPlayerInventorySlots(8, 84)
}
