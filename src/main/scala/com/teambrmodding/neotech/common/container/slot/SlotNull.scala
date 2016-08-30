package com.teambrmodding.neotech.common.container.slot

import java.awt.Color

import com.teambr.bookshelf.common.container.slots.{ICustomSlot, SLOT_SIZE}
import net.minecraft.inventory.{IInventory, Slot}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}


/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/26/2016
  */
class SlotNull(slotSize : SLOT_SIZE.Value, inventory : IInventory, id : Int, x : Int, y : Int)
    extends Slot(inventory, id, x, y) with ICustomSlot {
    override def getSlotSize: SLOT_SIZE.Value = slotSize

    override def getPoint: (Integer, Integer) = {
        slotSize match {
            case SLOT_SIZE.LARGE => (xDisplayPosition - 5, yDisplayPosition - 5)
            case _ => (xDisplayPosition - 1, yDisplayPosition - 1)
        }
    }

    @SideOnly(Side.CLIENT)
    override def getSlotTexture : String = "neotech:items/trashBag"
}
