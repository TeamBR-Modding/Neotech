package com.dyonovan.neotech.common.container.machines

import com.dyonovan.neotech.common.tiles.machines.TileFurnaceGenerator
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
 * @since August 14, 2015
 */
class ContainerFurnaceGenerator(playerInventory: InventoryPlayer, tile: TileFurnaceGenerator) extends
        BaseContainer(playerInventory, tile) {

    addSlotToContainer(new RestrictedSlot(tile, 0, 78, 35))
    addPlayerInventorySlots(8, 84)
}
