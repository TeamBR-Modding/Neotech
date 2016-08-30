package com.teambrmodding.neotech.common.container.machines.processors

import com.teambrmodding.neotech.common.container.machines.ContainerAbstractMachine
import com.teambrmodding.neotech.common.tiles.machines.processors.TileCentrifuge
import net.minecraft.entity.player.InventoryPlayer

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/21/2016
  */
class ContainerCentrifuge(playerInventory: InventoryPlayer, tileEntity: TileCentrifuge) extends
        ContainerAbstractMachine(playerInventory, tileEntity) {

    addPlayerInventorySlots(84)
}