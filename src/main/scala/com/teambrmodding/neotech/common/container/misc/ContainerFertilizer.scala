package com.teambrmodding.neotech.common.container.misc

import com.teambrmodding.neotech.common.tiles.misc.{TileFertilizer, TileCrafter}
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
 * @since August 22, 2015
 */
class ContainerFertilizer(playerInventory: InventoryPlayer, tile: TileFertilizer) extends BaseContainer(playerInventory, tile) {
    addInventoryGrid(70, 30, 2)
    addPlayerInventorySlots(8, 84)
}
