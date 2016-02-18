package com.dyonovan.neotech.common.container.machines.processors

import com.dyonovan.neotech.common.container.machines.ContainerAbstractMachine
import com.dyonovan.neotech.common.tiles.machines.processors.TileCrucible
import net.minecraft.entity.player.InventoryPlayer

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/17/2016
  */
class ContainerCrucible(playerInventory: InventoryPlayer, tileEntity: TileCrucible) extends
        ContainerAbstractMachine(playerInventory, tileEntity) {

    addSlotToContainer(new RestrictedSlot(tileEntity, tileEntity.ITEM_INPUT_SLOT, 80, 41))
    addSlotToContainer(new RestrictedSlot(tileEntity, tileEntity.INPUT_SLOT, 130, 20) {
        override def getSlotTexture = "neotech:gui/in"
    })
    addSlotToContainer(new RestrictedSlot(tileEntity, tileEntity.OUTPUT_SLOT, 130, 60) {
        override def getSlotTexture = "neotech:gui/out"
    })

    addPlayerInventorySlots(84)
}
