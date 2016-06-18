package com.dyonovan.neotech.common.tiles.misc

import com.teambr.bookshelf.common.tiles.traits.Inventory
import net.minecraft.item.ItemStack
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
 * @since August 18, 2015
 */
class TileCrafter extends TileEntity with Inventory {

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].readFromNBT(tag)
        super[Inventory].readFromNBT(tag)
    }

    override def writeToNBT(tag: NBTTagCompound): NBTTagCompound = {
        super[TileEntity].writeToNBT(tag)
        super[Inventory].writeToNBT(tag)
        tag
    }

    override def markDirty(): Unit = {
        super[TileEntity].markDirty()
    }

    override def initialSize: Int = 20

    override def isItemValidForSlot(index: Int, stack: ItemStack): Boolean = {
        index !=9 && index !=18
    }
}
