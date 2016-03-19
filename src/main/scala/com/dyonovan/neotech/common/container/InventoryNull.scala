package com.dyonovan.neotech.common.container

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.text.ITextComponent

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
class InventoryNull extends IInventory {
    override def decrStackSize(index: Int, count: Int): ItemStack = null
    override def closeInventory(player: EntityPlayer): Unit = {}
    override def getSizeInventory: Int = 0
    override def clear(): Unit = {}
    override def getInventoryStackLimit: Int = 64
    override def markDirty(): Unit = {}
    override def isItemValidForSlot(index: Int, stack: ItemStack): Boolean = true
    override def openInventory(player: EntityPlayer): Unit = {}
    override def getFieldCount: Int = 0
    override def getField(id: Int): Int = 0
    override def setInventorySlotContents(index: Int, stack: ItemStack): Unit = {}
    override def isUseableByPlayer(player: EntityPlayer): Boolean = true
    override def getStackInSlot(index: Int): ItemStack = null
    override def removeStackFromSlot(index: Int): ItemStack = null
    override def setField(id: Int, value: Int): Unit = {}
    override def getName: String = "INVENTORYNULL"
    override def hasCustomName: Boolean = false
    override def getDisplayName: ITextComponent = null
}
