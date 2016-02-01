package com.dyonovan.neotech.common.container.machines

import com.dyonovan.neotech.common.container.slot.SlotFurnaceOutputItemHandler
import com.dyonovan.neotech.common.tiles.machines.{TileFluidGenerator, TileElectricFurnace}
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

    addPlayerInventorySlots(8, 84)
}
