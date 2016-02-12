package com.dyonovan.neotech.common.tiles.machines.operators

import java.util
import java.util.Comparator

import cofh.api.energy.EnergyStorage
import com.teambr.bookshelf.collections.Location
import com.teambr.bookshelf.common.tiles.traits.{Inventory, UpdatingTile}
import net.minecraft.block.BlockLeavesBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.BlockPos

import scala.util.control.Breaks._

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/11/2016
  */
class TreeFarm extends UpdatingTile with Inventory {
    val RANGE = 10
    def costToOperate = 200
    var energy = new EnergyStorage(10000)
    override def initialSize: Int = 20

    var isBuildingCache = false
    lazy val cache : util.Queue[BlockPos] = new util.PriorityQueue[BlockPos](new Comparator[BlockPos] {
        override def compare(o1: BlockPos, o2: BlockPos): Int =
            o1.distanceSq(pos.getX, pos.getY, pos.getZ)
                    .compareTo(o2.distanceSq(pos.getX, pos.getY, pos.getZ))
    })

    def operationDelay = 1

    var ticker = operationDelay
    override def onServerTick() : Unit = {
        ticker -= 1
        if (!isBuildingCache && ticker <= 0) {
            ticker = operationDelay
            energy.extractEnergy(costToOperate, false)
            if(cache.isEmpty)
                findNextTree()
            else
                chopTree()
        }
    }

    def findNextTree() : Unit = {
        isBuildingCache = true

        val corner1 = new Location(pos.getX - RANGE, pos.getY, pos.getZ - RANGE)
        val corner2 = new Location(pos.getX + RANGE, pos.getY, pos.getZ + RANGE)

        var logPosition : BlockPos = null

        breakable {
            val list = corner1.getAllWithinBounds(corner2, includeInner = true, includeOuter = true)
            for (x <- 0 until list.size()) {
                if (worldObj.getBlockState(list.get(x).asBlockPos).getBlock.isWood(worldObj, list.get(x).asBlockPos)) {
                    logPosition = list.get(x).asBlockPos
                    break
                }
            }
        }

        if(logPosition != null) {
            val stack : util.Stack[BlockPos] = new util.Stack[BlockPos]()
            stack.push(logPosition)
            while(!stack.isEmpty) {
                val lookingPosition = stack.pop()
                if (worldObj.getBlockState(lookingPosition).getBlock.isWood(worldObj, lookingPosition) ||
                        worldObj.getBlockState(lookingPosition).getBlock.isInstanceOf[BlockLeavesBase]) {
                    val blocksAround = new Location(lookingPosition.getX - 1, lookingPosition.getY - 1, lookingPosition.getZ - 1)
                            .getAllWithinBounds(new Location(lookingPosition.getX + 1, lookingPosition.getY + 1, lookingPosition.getZ + 1), includeInner = true, includeOuter = true)
                    for(x <- 0 until blocksAround.size()) {
                        val attachedPosition = blocksAround.get(x).asBlockPos
                        if(!cache.contains(attachedPosition) &&
                                attachedPosition.distanceSq(pos.getX, pos.getY, pos.getZ) <= 1000 &&
                                (worldObj.getBlockState(attachedPosition).getBlock.isWood(worldObj, attachedPosition) ||
                                        worldObj.getBlockState(attachedPosition).getBlock.isInstanceOf[BlockLeavesBase])) {
                            stack.push(attachedPosition)
                            cache.add(attachedPosition)
                        }
                    }
                }
            }
        }

        isBuildingCache = false
    }

    def chopTree() : Unit = {
        val logPosition = cache.poll()
        worldObj.setBlockToAir(logPosition)
    }

    override def writeToNBT(tag : NBTTagCompound) : Unit = {
        super[TileEntity].writeToNBT(tag)
        super[Inventory].writeToNBT(tag)
    }

    override def readFromNBT(tag : NBTTagCompound) : Unit = {
        super[TileEntity].readFromNBT(tag)
        super[Inventory].readFromNBT(tag)
    }
}
