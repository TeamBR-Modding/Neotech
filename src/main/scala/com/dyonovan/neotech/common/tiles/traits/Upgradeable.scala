package com.dyonovan.neotech.common.tiles.traits

import com.dyonovan.neotech.collections.UpgradeBoard
import com.teambr.bookshelf.common.tiles.traits.Inventory
import net.minecraft.nbt.NBTTagCompound

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 20, 2015
 */
trait Upgradeable extends Inventory {

    val slotMB: Int

    def getHardDriveCount: Int = {
        val upgradeBoard = getStackInSlot(slotMB).asInstanceOf[UpgradeBoard]
        if (upgradeBoard != null)
            upgradeBoard.getHardDriveCount
        else 0
    }

    def getProcessorCount: Int = {
        val upgradeBoard = getStackInSlot(slotMB).asInstanceOf[UpgradeBoard]
        if (upgradeBoard != null)
            upgradeBoard.getProcessorCount
        else 0
    }

    def hasControl: Boolean = {
        val upgradeBoard = getStackInSlot(slotMB).asInstanceOf[UpgradeBoard]
        if (upgradeBoard != null)
            upgradeBoard.hasControl
        else false
    }

    def hasExpansion: Boolean = {
        val upgradeBoard = getStackInSlot(slotMB).asInstanceOf[UpgradeBoard]
        if (upgradeBoard != null)
            upgradeBoard.hasExpansion
        else false
    }

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super[Inventory].writeToNBT(tag)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[Inventory].readFromNBT(tag)
    }
}
