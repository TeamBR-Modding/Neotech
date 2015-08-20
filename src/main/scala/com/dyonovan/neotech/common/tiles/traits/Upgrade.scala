package com.dyonovan.neotech.common.tiles.traits

import com.teambr.bookshelf.common.tiles.traits.Inventory
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

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
trait Upgrade extends TileEntity with Inventory {

    inventoryName = "upgradeInventory"
    override def initialSize = 1

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super[Inventory].writeToNBT(tag)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[Inventory].readFromNBT(tag)
    }

    override def markDirty(): Unit = {
        super[Inventory].markDirty()
    }

}
