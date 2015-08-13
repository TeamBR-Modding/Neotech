package com.dyonovan.neotech.common.container.machines

import com.dyonovan.neotech.common.tiles.machines.TileElectricCrusher
import com.teambr.bookshelf.common.container.BaseContainer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.SlotFurnaceOutput

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 13, 2015
 */
class ContainerElectricCrusher(playerInventory: InventoryPlayer, tile: TileElectricCrusher) extends
        BaseContainer(playerInventory, tile) {

    addSlotToContainer(new RestrictedSlot(tile, 0, 41, 35))
    addSlotToContainer(new SlotFurnaceOutput(playerInventory.player, tile, 1, 101, 35))
    addSlotToContainer(new SlotFurnaceOutput(playerInventory.player, tile, 2, 131, 35))
    addPlayerInventorySlots(8, 84)
}
