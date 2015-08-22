package com.dyonovan.neotech.common.tiles.misc

import java.util

import com.google.common.collect.Lists
import com.teambr.bookshelf.common.tiles.traits.{Inventory, UpdatingTile}
import net.minecraft.block.IGrowable
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos


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
class TileFertilizer extends TileEntity with UpdatingTile with Inventory {

    var corner1: BlockPos = _ //pos.north(3).west(3)
    var corner2: BlockPos = _ //pos.south(3).east(3)
    var usesLeft: Int = 100

    override def onServerTick(): Unit = {
        if (corner1 == null) {
            corner1 = pos.north(3).west(3)
            corner2 = pos.south(3).east(3)
        }
        if (usesLeft > 0) {
            val list: util.ArrayList[BlockPos] = Lists.newArrayList(BlockPos.getAllInBox(corner1, corner2))
                    .asInstanceOf[util.ArrayList[BlockPos]]
            for (i <- 0 until list.size()) {
                val plantPOS = list.get(i)
                if (plantPOS != pos) {
                    worldObj.getBlockState(plantPOS).getBlock match {
                        case plant: IGrowable =>
                            val state = worldObj.getBlockState(plantPOS)
                            if (plant.canGrow(worldObj, plantPOS, state, worldObj.isRemote) &&
                                    plant.canUseBonemeal(worldObj, worldObj.rand, plantPOS, state)) {
                                plant.grow(worldObj, worldObj.rand, plantPOS, state)
                                //ItemDye.spawnBonemealParticles(worldObj, plantPOS, 10)
                                usesLeft -= 1
                                if (usesLeft < 1) worldObj.destroyBlock(pos, false)
                            }
                        case _ =>
                    }
                }
            }
        }
    }

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].writeToNBT(tag)
        if (corner1 != null) {
            tag.setLong("Corner1", corner1.toLong)
            tag.setLong("Corner2", corner2.toLong)
        }
        tag.setInteger("Uses", usesLeft)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].readFromNBT(tag)
        if (tag.hasKey("Corner1")) {
            corner1 = BlockPos.fromLong(tag.getLong("Corner1"))
            corner2 = BlockPos.fromLong(tag.getLong("Corner2"))
        }
        usesLeft = tag.getInteger("Uses")
    }

    override def markDirty(): Unit = {
        super[TileEntity].markDirty()
        super[Inventory].markDirty()
    }

    override var inventoryName: String = _

    override def hasCustomName(): Boolean = false

    override def initialSize: Int = 4
}
