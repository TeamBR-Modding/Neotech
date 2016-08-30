package com.teambrmodding.neotech.client.gui.misc

import com.teambrmodding.neotech.common.container.misc.ContainerTrashBag
import com.teambr.bookshelf.client.gui.GuiBase
import com.teambr.bookshelf.common.tiles.traits.Inventory
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack

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
class GuiTrashBag(inventory : Inventory, inventoryPlayer: InventoryPlayer, bag : ItemStack) extends
    GuiBase[ContainerTrashBag](new ContainerTrashBag(inventory, inventoryPlayer, bag), 175, 165, "inventory.trashBag.title") {
    override def addComponents(): Unit = {}
}
