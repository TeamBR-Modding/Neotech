package com.dyonovan.neotech.common.tiles.machines.operators

import java.util
import java.util.Comparator

import cofh.api.energy.{IEnergyReceiver, EnergyStorage}
import com.dyonovan.neotech.client.gui.machines.operators.GuiTreeFarm
import com.dyonovan.neotech.common.container.machines.operators.ContainerTreeFarm
import com.dyonovan.neotech.common.tiles.AbstractMachine
import com.teambr.bookshelf.collections.Location
import net.minecraft.block.{BlockSapling, Block, BlockLeavesBase}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemAxe, ItemShears, ItemStack}
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.World

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
class TileTreeFarm extends AbstractMachine with IEnergyReceiver {
    val RANGE = 10
    def costToOperate = 200
    energy = new EnergyStorage(10000)
    override def initialSize: Int = 18

    lazy val AXE_SLOT = 0
    lazy val SHEARS_SLOT = 1
    lazy val SAPLING_SLOTS = getSizeInventory - 3 until getSizeInventory

    var isBuildingCache = false
    lazy val cache : util.Queue[BlockPos] = new util.PriorityQueue[BlockPos](new Comparator[BlockPos] {
        override def compare(o1: BlockPos, o2: BlockPos): Int =
            o1.distanceSq(pos.getX, pos.getY, pos.getZ)
                    .compareTo(o2.distanceSq(pos.getX, pos.getY, pos.getZ))
    })

    def operationDelay = 1

    var time = operationDelay
    override def doWork() : Unit = {
        time -= 1
        if (!isBuildingCache && time <= 0) {
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
        if(worldObj.getBlockState(logPosition).getBlock != null)
            worldObj.playAuxSFX(2001, logPosition, Block.getIdFromBlock(worldObj.getBlockState(logPosition).getBlock))
        worldObj.setBlockToAir(logPosition)
    }

    /**
      * Used to get what particles to spawn. This will be called when the tile is active
      */
    override def spawnActiveParticles(xPos: Double, yPos: Double, zPos: Double): Unit = {}

    /**
      * Used to check if this tile is active or not
      *
      * @return True if active state
      */
    override def isActive: Boolean = { false }

    /**
      * Used to get what slots are allowed to be output
      *
      * @return The slots to output from
      */
    override def getOutputSlots: Array[Int] = SHEARS_SLOT until getSizeInventory - 3 toArray

    /**
      * Used to output the redstone single from this structure
      *
      * Use a range from 0 - 16.
      *
      * 0 Usually means that there is nothing in the tile, so take that for lowest level. Like the generator has no energy while
      * 16 is usually the flip side of that. Output 16 when it is totally full and not less
      *
      * @return int range 0 - 16
      */
    override def getRedstoneOutput: Int = 0

    /**
      * Use this to set all variables back to the default values, usually means the operation failed
      */
    override def reset(): Unit = {}

    /**
      * Used to get what slots are allowed to be input
      *
      * @return The slots to input from
      */
    override def getInputSlots: Array[Int] = Array(AXE_SLOT, SHEARS_SLOT) ++ SAPLING_SLOTS

    override def getSlotsForFace(side: EnumFacing): Array[Int] = 0 until getSizeInventory toArray

    override def canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean =
        index > SHEARS_SLOT && index < getSizeInventory - 3

    override def canInsertItem(slot: Int, itemStackIn: ItemStack, direction: EnumFacing): Boolean = {
        slot match {
            case AXE_SLOT if itemStackIn != null => itemStackIn.getItem.isInstanceOf[ItemAxe]
            case SHEARS_SLOT if itemStackIn != null => itemStackIn.getItem.isInstanceOf[ItemShears]
            case _ => false
        }
    }

    override def isItemValidForSlot(slot: Int, itemStackIn: ItemStack): Boolean = {
        if(slot >= getSizeInventory - 3) {
            return Block.getBlockFromItem(itemStackIn.getItem) != null &&
                    Block.getBlockFromItem(itemStackIn.getItem).isInstanceOf[BlockSapling]
        }
        slot match {
            case AXE_SLOT if itemStackIn != null => itemStackIn.getItem.isInstanceOf[ItemAxe]
            case SHEARS_SLOT if itemStackIn != null => itemStackIn.getItem.isInstanceOf[ItemShears]
            case _ => true
        }
    }

    /**
      * Return the container for this tile
      *
      * @param ID Id, probably not needed but could be used for multiple guis
      * @param player The player that is opening the gui
      * @param world The world
      * @param x X Pos
      * @param y Y Pos
      * @param z Z Pos
      * @return The container to open
      */
    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef =
        new ContainerTreeFarm(player.inventory, this)

    /**
      * Return the gui for this tile
      *
      * @param ID Id, probably not needed but could be used for multiple guis
      * @param player The player that is opening the gui
      * @param world The world
      * @param x X Pos
      * @param y Y Pos
      * @param z Z Pos
      * @return The gui to open
      */
    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef =
        new GuiTreeFarm(player, this)

    /*******************************************************************************************************************
      ************************************************** Energy methods ************************************************
      ******************************************************************************************************************/

    /**
      * Add energy to an IEnergyReceiver, internal distribution is left entirely to the IEnergyReceiver.
      *
      * @param from Orientation the energy is received from.
      * @param maxReceive Maximum amount of energy to receive.
      * @param simulate If TRUE, the charge will only be simulated.
      * @return Amount of energy that was (or would have been, if simulated) received.
      */
    override def receiveEnergy(from: EnumFacing, maxReceive: Int, simulate: Boolean): Int = {
        if (energy != null) {
            val actual = energy.receiveEnergy(maxReceive, simulate)
            if (worldObj != null)
                worldObj.markBlockForUpdate(pos)
            actual
        } else 0
    }
}
