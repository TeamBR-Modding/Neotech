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
        BaseContainer(playerInventory, tileEntity) {

    addSlotToContainer(new RestrictedSlot(tileEntity, 0, 70, 27))
    addSlotToContainer(new RestrictedSlot(tileEntity, 1, 105, 27))
    addSlotToContainer(new RestrictedSlot(tileEntity, 2, 70, 61))
    addSlotToContainer(new RestrictedSlot(tileEntity, 3, 105, 61))
    addSlotToContainer(new RestrictedSlot(tileEntity, 4, 41, 43))
    addSlotToContainer(new RestrictedSlot(tileEntity, 5, 138, 44))
    addPlayerInventorySlots(8, 111)

}
