package com.dyonovan.neotech.common.container

import com.dyonovan.neotech.common.tiles.TileFurnace
import com.teambr.bookshelf.client.gui.GuiBase
import com.teambr.bookshelf.common.container.BaseContainer
import net.minecraft.entity.player.{InventoryPlayer, EntityPlayer}
import net.minecraft.inventory.SlotFurnaceOutput

class ContainerFurnace(playerInventory: InventoryPlayer, tile: TileFurnace) extends
        BaseContainer(playerInventory, tile) {

    addSlotToContainer(new RestrictedSlot(tile, 0, 56, 35))
    addSlotToContainer(new SlotFurnaceOutput(playerInventory.player, tile, 1, 116, 35))
    addPlayerInventorySlots(8, 84)
}
