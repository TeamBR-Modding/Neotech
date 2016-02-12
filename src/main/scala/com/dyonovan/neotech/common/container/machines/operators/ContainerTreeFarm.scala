package com.dyonovan.neotech.common.container.machines.operators

import com.dyonovan.neotech.common.container.machines.ContainerAbstractMachine
import com.dyonovan.neotech.common.tiles.machines.operators.TileTreeFarm
import net.minecraft.entity.player.InventoryPlayer

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/11/2016
  */
class ContainerTreeFarm(playerInventory: InventoryPlayer, tile: TileTreeFarm) extends
        ContainerAbstractMachine(playerInventory, tile) {

    addSlotToContainer(new RestrictedSlot(tile, tile.AXE_SLOT, 62, 20) {
        override def getSlotTexture = "neotech:items/axe_ghost"
    })
    addSlotToContainer(new RestrictedSlot(tile, tile.SHEARS_SLOT, 89, 20){
        override def getSlotTexture = "neotech:items/shears_ghost"
    })

    addInventoryLine(62, 40,      3, 6)
    addInventoryLine(62, 58,  3 + 6, 6)
    addInventoryLine(116, 20, 3 + 12, 3)

    addPlayerInventorySlots(8, 84)
}
