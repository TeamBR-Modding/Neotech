package com.dyonovan.neotech.common.container.machines

import com.dyonovan.neotech.common.blocks.traits.Upgradeable
import com.teambr.bookshelf.common.container.BaseContainer
import net.minecraft.entity.player.InventoryPlayer

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/9/2016
  */
class ContainerMachineUpgrade(playerInventory: InventoryPlayer, tile : Upgradeable) extends
    BaseContainer(playerInventory, tile.upgradeInventory) {

    addSlotToContainer(new RestrictedSlot(tile.upgradeInventory, 0, 80, 35))
    addPlayerInventorySlots(8, 84)
}
