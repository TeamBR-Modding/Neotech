package com.dyonovan.neotech.common.container.machines

import com.dyonovan.neotech.common.tiles.machines.TileThermalBinder
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
 * @since August 21, 2015
 */
class ContainerThermalBinder(playerInventory: InventoryPlayer, tileEntity: TileThermalBinder) extends
        ContainerAbstractMachine(playerInventory, tileEntity) {

    addSlotToContainer(new RestrictedSlot(tileEntity, 0, 67, 22))
    addSlotToContainer(new RestrictedSlot(tileEntity, 1, 92, 22))
    addSlotToContainer(new RestrictedSlot(tileEntity, 2, 67, 67))
    addSlotToContainer(new RestrictedSlot(tileEntity, 3, 92, 67))
    addSlotToContainer(new RestrictedSlot(tileEntity, 4, 38, 44))
    addSlotToContainer(new RestrictedSlot(tileEntity, 5, 122, 44))
    addPlayerInventorySlots(8, 103)

}
