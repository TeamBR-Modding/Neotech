package com.dyonovan.neotech.common.tiles.misc

import java.util

import com.dyonovan.neotech.registries.FertilizerBlacklistRegistry
import com.google.common.collect.Lists
import com.teambr.bookshelf.api.waila.Waila
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.common.tiles.traits.{Inventory, UpdatingTile}
import net.minecraft.block.state.IBlockState
import net.minecraft.block.{Block, IGrowable}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
import net.minecraftforge.common.IPlantable


/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 22, 2015
 */
class TileFertilizer extends TileEntity with Inventory with UpdatingTile with Waila {

    var corner1: BlockPos = _
    var corner2: BlockPos = _
    var list: util.ArrayList[BlockPos] = _
    var disabled = false

    override def onServerTick(): Unit = {
        if (corner1 == null) {
            corner2 = pos.north(4).west(4)
            corner1 = pos.south(4).east(4).down(2)
            list = Lists.newArrayList(BlockPos.getAllInBox(corner1, corner2))
        }
        val plantPOS = list.get(worldObj.rand.nextInt(list.size()))
        val state = worldObj.getBlockState(plantPOS)
        val block = state.getBlock
        if (!FertilizerBlacklistRegistry.isBlacklisted(block)) {
            if (block.isInstanceOf[IPlantable] || block.isInstanceOf[IGrowable]) {
                if (!disabled && isBoneMeal._1 && worldObj.rand.nextInt(20) == 0) {
                    growPlant(block, plantPOS, state)
                    if (worldObj.rand.nextInt(100) <= 14) {
                        decrStackSize(isBoneMeal._2, 1)
                        worldObj.markBlockForUpdate(getPos)
                    }
                } else if (worldObj.rand.nextInt(100) <= 79) {
                    growPlant(block, plantPOS, state)
                }
            }
        }
    }

    private def growPlant(block: Block, pos: BlockPos, state: IBlockState): Unit = {
        worldObj.scheduleUpdate(pos, block, 0)
        block.updateTick(worldObj, pos, state, worldObj.rand)
    }

    private def isBoneMeal: (Boolean, Int) = {
        for (i <- 0 until getSizeInventory) {
            if (getStackInSlot(i) != null && getStackInSlot(i).stackSize > 0) return (true, i)
        }
        (false, -1)
    }

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].writeToNBT(tag)
        super[Inventory].writeToNBT(tag)
        tag.setBoolean("Disabled", disabled)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].readFromNBT(tag)
        super[Inventory].readFromNBT(tag)
        disabled = tag.getBoolean("Disabled")
    }

    override def markDirty(): Unit = {
        super[TileEntity].markDirty()
        super[Inventory].markDirty()
    }

    override def initialSize: Int = 4

    override def returnWailaBody(tipList: java.util.List[String]): java.util.List[String] = {
        var count = 0
        for (i <- 0 until getSizeInventory) {
            if (getStackInSlot(i) != null)
                count += getStackInSlot(i).stackSize
        }
        tipList.add(GuiColor.WHITE + "BoneMeal: " + count)
        tipList
    }
}
