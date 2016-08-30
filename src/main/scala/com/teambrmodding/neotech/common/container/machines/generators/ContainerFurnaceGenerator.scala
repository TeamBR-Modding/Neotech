package com.teambrmodding.neotech.common.container.machines.generators

import com.teambrmodding.neotech.common.container.machines.ContainerAbstractMachine
import com.teambrmodding.neotech.common.tiles.machines.generators.TileFurnaceGenerator
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
        ContainerAbstractMachine(playerInventory, tile) {

    addSlotToContainer(new RestrictedSlot(tile, 0, 78, 35))
    addPlayerInventorySlots(8, 84)
}
