package com.dyonovan.neotech.common.tiles.machines.operators

import java.util
import java.util.Comparator

import cofh.api.energy.{EnergyStorage, IEnergyReceiver}
import com.dyonovan.neotech.client.gui.machines.operators.GuiTreeFarm
import com.dyonovan.neotech.common.container.machines.operators.ContainerTreeFarm
import com.dyonovan.neotech.common.tiles.AbstractMachine
import com.teambr.bookshelf.client.gui.{GuiTextFormat, GuiColor}
import com.teambr.bookshelf.collections.Location
import net.minecraft.block.state.IBlockState
import net.minecraft.block.{Block, BlockLeaves, BlockSapling}
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.{Item, ItemAxe, ItemShears, ItemStack}
import net.minecraft.util.{StatCollector, AxisAlignedBB, BlockPos, EnumFacing}
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

    def RANGE : Int = {
        if(processorCount > 0)
            return 4 + processorCount
        4
    }

    def costToOperate : Int = {
        if(processorCount > 0)
            return 200 * processorCount
        200
    }

    def operationDelay : Int = {
        if(processorCount > 0)
            20 - (processorCount * 2)
        else
            20
    }

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

    var time = operationDelay
    override def doWork() : Unit = {
        time -= 1
        if (!isBuildingCache && time <= 0 && energy.getEnergyStored > costToOperate) {
            time = operationDelay
            if(cache.isEmpty)
                findNextTree()
            else
                chopTree()
            pullInSaplings()
            plantSaplings()
        }
    }

    def findNextTree() : Unit = {
        isBuildingCache = true

        val corner1 = new Location(pos.getX - RANGE + 1, pos.getY, pos.getZ - RANGE + 1)
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
                        worldObj.getBlockState(lookingPosition).getBlock.isInstanceOf[BlockLeaves]) {
                    val blocksAround = new Location(lookingPosition.getX - 1, lookingPosition.getY - 1, lookingPosition.getZ - 1)
                            .getAllWithinBounds(new Location(lookingPosition.getX + 1, lookingPosition.getY + 1, lookingPosition.getZ + 1), includeInner = true, includeOuter = true)
                    for(x <- 0 until blocksAround.size()) {
                        val attachedPosition = blocksAround.get(x).asBlockPos
                        if(!cache.contains(attachedPosition) &&
                                attachedPosition.distanceSq(pos.getX, pos.getY, pos.getZ) <= 1000 &&
                                (worldObj.getBlockState(attachedPosition).getBlock.isWood(worldObj, attachedPosition) ||
                                        worldObj.getBlockState(attachedPosition).getBlock.isInstanceOf[BlockLeaves])) {
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
        val logPosition = cache.peek()
        if(worldObj.getBlockState(logPosition).getBlock != null) {
            worldObj.getBlockState(logPosition).getBlock match {
                case log : Block if log.isWood(worldObj, logPosition) =>
                    if(chopLog(logPosition))
                        cache.poll()
                case leave : BlockLeaves =>
                    if(chopLeave(logPosition))
                        cache.poll()
                case _ => cache.poll()
            }
        } else
            cache.poll()
    }

    def chopLog(logPosition : BlockPos) : Boolean = {
        if(getStackInSlot(AXE_SLOT) != null && addHarvestToInventory(new ItemStack(worldObj.getBlockState(logPosition).getBlock, 1, worldObj.getBlockState(logPosition).getBlock.damageDropped(worldObj.getBlockState(logPosition))), sapling = false)) {
            if(worldObj.getBlockState(logPosition).getBlock != null)
                worldObj.playAuxSFX(2001, logPosition, Block.getIdFromBlock(worldObj.getBlockState(logPosition).getBlock))
            worldObj.setBlockToAir(logPosition)
            if(getStackInSlot(AXE_SLOT).attemptDamageItem(1, worldObj.rand))
                setStackInSlot(AXE_SLOT, null)
            energy.extractEnergy(costToOperate, false)
            worldObj.markBlockForUpdate(pos)
            return true
        }
        false
    }

    def chopLeave(leavePosition : BlockPos) : Boolean = {
        if(getStackInSlot(SHEARS_SLOT) != null && addHarvestToInventory(new ItemStack(worldObj.getBlockState(leavePosition).getBlock, 1, worldObj.getBlockState(leavePosition).getBlock.damageDropped(worldObj.getBlockState(leavePosition))), sapling = false)) {
            if(worldObj.getBlockState(leavePosition).getBlock != null)
                worldObj.playAuxSFX(2001, leavePosition, Block.getIdFromBlock(worldObj.getBlockState(leavePosition).getBlock))
            worldObj.setBlockToAir(leavePosition)
            if(getStackInSlot(SHEARS_SLOT).attemptDamageItem(1, worldObj.rand))
                setStackInSlot(SHEARS_SLOT, null)
            energy.extractEnergy(costToOperate, false)
            worldObj.markBlockForUpdate(pos)
            return true
        } else {
            if(worldObj.rand.nextInt(20) == 0) {
                addHarvestToInventory(
                    new ItemStack(worldObj.getBlockState(leavePosition).getBlock.asInstanceOf[BlockLeaves]
                            .getItemDropped(worldObj.getBlockState(leavePosition), worldObj.rand, 0), 1,
                        worldObj.getBlockState(leavePosition).getBlock.asInstanceOf[BlockLeaves]
                                .damageDropped(worldObj.getBlockState(leavePosition))), sapling = true)
            }
            worldObj.setBlockToAir(leavePosition)
            return true
        }
        false
    }

    def pullInSaplings() : Unit = {
        val items = worldObj.getEntitiesWithinAABB(classOf[EntityItem], AxisAlignedBB.fromBounds(pos.getX - RANGE - 4, pos.getY, pos.getZ - RANGE - 4, pos.getX + RANGE + 5, pos.getY + 1, pos.getZ + RANGE + 5))
        for(x <- 0 until items.size()) {
            val item = items.get(x)
            item.getEntityItem.getItem match {
                case sapling : Item if Block.getBlockFromItem(sapling).isInstanceOf[BlockSapling] =>
                    if(addHarvestToInventory(item.getEntityItem, sapling = true)) {
                        item.setDead()
                    }
                case sapling : Items.apple.type =>
                    if(addHarvestToInventory(item.getEntityItem, sapling = false)) {
                        item.setDead()
                    }
                case _ =>
            }
        }
    }

    def plantSaplings() : Unit = {
        if(hasSaplings) {
            for (x <- pos.getX - RANGE + 1 until pos.getX + RANGE) {
                for (z <- pos.getZ - RANGE + 1 until pos.getZ + RANGE) {
                    val blockPos = new BlockPos(x, pos.getY, z)
                    if(worldObj.isAirBlock(blockPos) && worldObj.getBlockState(blockPos.down()) != null &&
                            (worldObj.getBlockState(blockPos.down()).getBlock == Blocks.dirt || worldObj.getBlockState(blockPos.down()).getBlock == Blocks.grass)) {
                        val blockState = getNextSaplingAndReduce
                        if(blockState != null) {
                            worldObj.setBlockState(blockPos, blockState)
                        }
                        else
                            return
                    }
                }
            }
        }
    }

    def hasSaplings: Boolean = {
        for(x <- SAPLING_SLOTS) {
            if(getStackInSlot(x) != null)
                return true
        }
        false
    }

    def getNextSaplingAndReduce:  IBlockState = {
        for(x <- SAPLING_SLOTS) {
            if (getStackInSlot(x) != null) {
                val block = Block.getBlockFromItem(getStackInSlot(x).getItem)
                val damage = getStackInSlot(x).getItemDamage
                getStackInSlot(x).stackSize -= 1
                if(getStackInSlot(x).stackSize <= 0)
                    setStackInSlot(x, null)
                return block.getStateFromMeta(damage)
            }
        }
        null
    }

    def addHarvestToInventory(stack : ItemStack, sapling : Boolean): Boolean = {
        if(sapling) {
            for(x <- SAPLING_SLOTS) {
                if(getStackInSlot(x) == null) {
                    setStackInSlot(x, stack)
                    return true
                } else if(getStackInSlot(x).getItem == stack.getItem && getStackInSlot(x).getItemDamage == stack.getItemDamage && getStackInSlot(x).stackSize + stack.stackSize <= stack.getMaxStackSize) {
                    getStackInSlot(x).stackSize += stack.stackSize
                    return true
                }
            }
        }

        for(x <- 3 until getSizeInventory - 3) {
            if(getStackInSlot(x) == null) {
                setStackInSlot(x, stack)
                return true
            } else if(getStackInSlot(x).getItem == stack.getItem && getStackInSlot(x).getItemDamage == stack.getItemDamage && getStackInSlot(x).stackSize + stack.stackSize <= stack.getMaxStackSize) {
                getStackInSlot(x).stackSize += stack.stackSize
                return true
            }
        }

        false
    }

    override def getDescription : String = {
        GuiColor.YELLOW + "" + GuiTextFormat.BOLD + StatCollector.translateToLocal("tile.neotech:treeFarm.name") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.treeFarm.desc") + "\n\n" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + StatCollector.translateToLocal("neotech.text.upgrades") + ":\n" + GuiTextFormat.RESET +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.processors") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.treeFarm.processorUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.hardDrives") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.electricFurnace.hardDriveUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.control") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.electricFurnace.controlUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.expansion") + ":\n" +
                GuiColor.WHITE +  StatCollector.translateToLocal("neotech.electricFurnace.expansionUpgrade.desc")
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
    override def getInputSlots: Array[Int] = Array(AXE_SLOT, SHEARS_SLOT)

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
