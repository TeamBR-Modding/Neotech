package com.teambrmodding.neotech.common.container.machines

import java.awt.Color

import com.teambrmodding.neotech.common.tiles.AbstractMachine
import com.teambr.bookshelf.common.container.BaseContainer
import com.teambr.bookshelf.common.container.slots.{ICustomSlot, SLOT_SIZE}
import com.teambr.bookshelf.common.tiles.traits.Inventory
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.item.ItemStack

import scala.collection.mutable.ArrayBuffer

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
class ContainerAbstractMachine(playerInventory: InventoryPlayer, tile: AbstractMachine) extends
        BaseContainer(playerInventory, tile) {

    lazy val upgradeSlots = new ArrayBuffer[UpgradeSlot]()

    class UpgradeSlot(inventory : Inventory, id : Int, x : Int, y : Int)
            extends RestrictedSlot(inventory, id, x, y) with ICustomSlot {
        override def getSlotSize: SLOT_SIZE.Value = SLOT_SIZE.STANDARD
        override def getPoint: (Integer, Integer) = (xDisplayPosition - 1, yDisplayPosition - 1)
        override def hasColor = true
        override def getColor : Color = new Color(0, 255, 0)
    }

    for(z <- 0 until tile.upgradeInventory.inventoryContents.size()) {
        val slot = new UpgradeSlot(tile.upgradeInventory, z, -10000, -1000)
        upgradeSlots += slot
        addSlotToContainer(slot)
    }

    override def getInventorySizeNotPlayer : Int = tile.getSizeInventory + 6
}
