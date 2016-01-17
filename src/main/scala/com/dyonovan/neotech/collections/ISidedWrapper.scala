package com.dyonovan.neotech.collections

import com.teambr.bookshelf.common.tiles.traits.Inventory
import net.minecraft.inventory.ISidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/17/2016
  */
abstract class ISidedWrapper(iSided : ISidedInventory) extends Inventory with ISidedInventory {
    copyFrom(iSided)
    override def getSlotsForFace(side: EnumFacing): Array[Int] =
        iSided.getSlotsForFace(side)

    override def canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean =
        iSided.canExtractItem(index, stack, direction)

    override def canInsertItem(index: Int, itemStackIn: ItemStack, direction: EnumFacing): Boolean =
        iSided.canInsertItem(index, itemStackIn, direction)

    override def isItemValidForSlot(index : Int, itemStack : ItemStack) : Boolean =
        iSided.isItemValidForSlot(index, itemStack)
}
