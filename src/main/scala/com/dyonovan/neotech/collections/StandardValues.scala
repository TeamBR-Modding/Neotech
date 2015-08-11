package com.dyonovan.neotech.collections

import com.teambr.bookshelf.traits.NBTSavable
import net.minecraft.nbt.NBTTagCompound

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 11, 2015
 */
class StandardValues extends NBTSavable {

    var burnTime = 0
    var currentItemBurnTime = 0
    var cookTime = 0

    var isPowered = false

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        tag.setInteger("Burn Time", burnTime)
        tag.setInteger("Cook Time", cookTime)
        tag.setInteger("Current Burn", currentItemBurnTime)

        tag.setBoolean("IsPowered", isPowered)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        burnTime = tag.getInteger("Burn Time")
        cookTime = tag.getInteger("Cook Time")
        currentItemBurnTime = tag.getInteger("Current Burn")

        isPowered = tag.getBoolean("IsPowered")
    }
}
