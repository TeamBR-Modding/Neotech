package com.teambrmodding.neotech.common.container.machines.generators

import com.teambrmodding.neotech.common.container.machines.ContainerAbstractMachine
import com.teambrmodding.neotech.common.tiles.machines.generators.TileFluidGenerator
import net.minecraft.entity.player.InventoryPlayer

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/31/2016
  */
class ContainerFluidGenerator(playerInventory: InventoryPlayer, tile: TileFluidGenerator) extends
        ContainerAbstractMachine(playerInventory, tile) {

    addSlotToContainer(new RestrictedSlot(tile, 0, 30, 20) {
        override def getSlotTexture = "neotech:gui/in"
    })
    addSlotToContainer(new RestrictedSlot(tile, 1, 30, 60) {
        override def getSlotTexture = "neotech:gui/out"
    })

    addPlayerInventorySlots(8, 84)
}
