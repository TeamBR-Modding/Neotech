package com.dyonovan.neotech.common.blocks.traits

import com.dyonovan.neotech.collections.UpgradeBoard
import com.dyonovan.neotech.managers.ItemManager
import com.teambr.bookshelf.common.container.IInventoryCallback
import com.teambr.bookshelf.common.tiles.traits.{Inventory, UpdatingTile}
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

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
trait Upgradeable {
    lazy val upgradeInventory = new Inventory {
        override def hasCustomName(): Boolean = false
        override def initialSize: Int = 1
        override var inventoryName: String = _

        /**
          * Used to define if an item is valid for a slot, we only want full MotherBoards
          * @param index The slot id
          * @param stack The stack to check
          * @return True if you can put this there
          */
        override def isItemValidForSlot(index: Int, stack: ItemStack): Boolean = stack.getItem == ItemManager.upgradeMBFull

        addCallback(new IInventoryCallback {
            override def onInventoryChanged(inventory: IInventory, slotNumber: Int): Unit = {
                if(inventory.getStackInSlot(slotNumber) == null)
                    resetValues()
            }
        })
    }

    /**
      * Called when the board is removed, reset to default values
      */
    def resetValues()

    //NBT, must overwrite
    def readFromNBT(tag: NBTTagCompound): Unit = {
        upgradeInventory.readFromNBT(tag, "upgrade")
    }

    def writeToNBT(tag : NBTTagCompound): Unit = {
        upgradeInventory.writeToNBT(tag, "upgrade")
    }

    def markDirty(): Unit = {
        upgradeInventory.markDirty()
    }

    /**
      * Returns an object for the Upgrade Board, if it exists
      * @return
      */
    def getUpgradeBoard : UpgradeBoard = {
        if(upgradeInventory.getStackInSlot(0) != null)
            UpgradeBoard.getBoardFromStack(upgradeInventory.getStackInSlot(0))
        else
            null
    }
}
