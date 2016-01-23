package com.dyonovan.neotech.pipes.container

import com.dyonovan.neotech.pipes.types.AdvancedPipe
import com.teambr.bookshelf.common.container.BaseContainer
import com.teambr.bookshelf.common.container.slots.PhantomSlot
import net.minecraft.inventory.IInventory

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/22/2016
  */
class ContainerPipeFilter(playerInventory : IInventory, tile : AdvancedPipe) extends BaseContainer(playerInventory, tile.filterInventory) {

    addPhantomInventoryLine(8, 25, 0, 9, 0)
    addPlayerInventorySlots(85)

    /**
      * Adds a line of inventory slots with a margin around them
      * @param xOffset X Offset
      * @param yOffset Y Offset
      * @param start The start slot id
      * @param count The count of slots
      * @param margin How much to pad the slots
      */
    def addPhantomInventoryLine(xOffset: Int, yOffset: Int, start: Int, count: Int, margin: Int) : Unit = {
        var slotId = start
        for(x <- 0 until count) {
            addSlotToContainer(new PhantomSlot(inventory, slotId, xOffset + x * (18 + margin), yOffset))
            slotId += 1
        }
    }
}
