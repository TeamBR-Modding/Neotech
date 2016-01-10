package com.dyonovan.neotech.common.blocks.traits

import com.dyonovan.neotech.collections.UpgradeBoard
import com.dyonovan.neotech.managers.ItemManager
import com.teambr.bookshelf.common.tiles.traits.{Inventory, UpdatingTile}
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
trait Upgradeable extends UpdatingTile with Inventory {
    //Inventory Info
    override var inventoryName: String = _
    override def hasCustomName(): Boolean = false
    override def initialSize: Int = 1

    //NBT
    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].readFromNBT(tag)
        super[Inventory].readFromNBT(tag)
    }

    override def writeToNBT(tag : NBTTagCompound): Unit = {
        super[TileEntity].writeToNBT(tag)
        super[Inventory].writeToNBT(tag)
    }

    override def markDirty(): Unit = {
        super[TileEntity].markDirty()
        super[Inventory].markDirty()
    }

    /**
      * Used to define if an item is valid for a slot, we only want full MotherBoards
      * @param index The slot id
      * @param stack The stack to check
      * @return True if you can put this there
      */
    override def isItemValidForSlot(index: Int, stack: ItemStack): Boolean = stack.getItem == ItemManager.upgradeMBFull

    /**
      * Returns an object for the Upgrade Board, if it exists
      * @return
      */
    def getUpgradeBoard : UpgradeBoard = {
        if(inventoryContents.get(0) != null)
            UpgradeBoard.getBoardFromStack(inventoryContents.get(0))
        else
            null
    }
}
