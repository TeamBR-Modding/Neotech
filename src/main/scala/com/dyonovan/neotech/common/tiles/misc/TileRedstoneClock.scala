package com.dyonovan.neotech.common.tiles.misc

import com.dyonovan.neotech.common.blocks.misc.BlockRedstoneClock
import com.teambr.bookshelf.common.tiles.traits.{RedstoneAware, Syncable}
import net.minecraft.block.BlockPressurePlate
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
import net.minecraft.world.World

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
class TileRedstoneClock extends Syncable with RedstoneAware {
    var delay = 20

    override def readFromNBT(tag : NBTTagCompound) = {
        super[TileEntity].readFromNBT(tag)
        delay = tag.getInteger("delay")
    }

    override def writeToNBT(tag : NBTTagCompound) = {
        super[TileEntity].writeToNBT(tag)
        tag.setInteger("delay", delay)
    }

    override def setVariable(id: Int, value: Double): Unit = {
        delay = value.toInt
    }

    override def getVariable(id: Int): Double = {
        delay
    }

    var ticker = 0
    override def onServerTick(): Unit = {
        ticker += 1
        val power = worldObj.getBlockState(pos).getValue(BlockPressurePlate.POWERED)
        if(!isPowered && ticker % delay == 0 || power)
            worldObj.getBlockState(pos).getBlock.asInstanceOf[BlockRedstoneClock].updateState(worldObj, pos, worldObj.getBlockState(pos), !power)
    }
}
