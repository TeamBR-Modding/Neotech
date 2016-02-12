package com.dyonovan.neotech.common.blocks.traits

import com.dyonovan.neotech.collections.UpgradeBoard
import com.dyonovan.neotech.managers.ItemManager
import com.teambr.bookshelf.common.container.InventoryCallback
import com.teambr.bookshelf.common.tiles.traits.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.items.IItemHandler

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
        override def initialSize: Int = 1

        /**
          * Used to define if an item is valid for a slot, we only want full MotherBoards
          *
          * @param index The slot id
          * @param stack The stack to check
          * @return True if you can put this there
          */
        override def isItemValidForSlot(index: Int, stack: ItemStack): Boolean = stack.getItem == ItemManager.upgradeMBFull

        addCallback(new InventoryCallback {
            override def onInventoryChanged(inventory: IItemHandler, slotNumber: Int): Unit = {
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
        if(upgradeInventory.getSizeInventory == 0)
            upgradeInventory.addInventorySlot(null)
    }

    def writeToNBT(tag : NBTTagCompound): Unit = {
        upgradeInventory.writeToNBT(tag, "upgrade")
    }

    /**
      * Returns an object for the Upgrade Board, if it exists
      *
      * @return
      */
    def getUpgradeBoard : UpgradeBoard = {
        if(upgradeInventory.getStackInSlot(0) != null)
            UpgradeBoard.getBoardFromStack(upgradeInventory.getStackInSlot(0))
        else
            null
    }

    def processorCount : Int = {
        if(getUpgradeBoard != null && getUpgradeBoard.getProcessorCount > 0)
            return getUpgradeBoard.getProcessorCount
        0
    }

    def hardDriveCount : Int = {
        if(getUpgradeBoard != null && getUpgradeBoard.getHardDriveCount > 0)
            return getUpgradeBoard.getProcessorCount
        0
    }
}
