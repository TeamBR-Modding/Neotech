package com.teambrmodding.neotech.common.tiles.machines.operators

import java.util
import java.util.Comparator

import cofh.api.energy.IEnergyReceiver
import com.teambrmodding.neotech.client.gui.machines.operators.GuiTreeFarm
import com.teambrmodding.neotech.collections.EnumInputOutputMode
import com.teambrmodding.neotech.common.container.machines.operators.ContainerTreeFarm
import com.teambrmodding.neotech.common.tiles.AbstractMachine
import com.teambrmodding.neotech.utils.{ClientUtils, TimeUtils}
import com.teambr.bookshelf.client.gui.{GuiColor, GuiTextFormat}
import com.teambr.bookshelf.collections.Location
import com.teambr.bookshelf.util.InventoryUtils
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem
import net.minecraft.block.state.IBlockState
import net.minecraft.block.{Block, BlockLeaves, BlockSapling}
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item._
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.util.text.translation.I18n
import net.minecraft.world.World

import scala.collection.JavaConversions._
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
    lazy val AXE_SLOT = 0
    lazy val SHEARS_SLOT = 1
    lazy val SAPLING_SLOTS = getSizeInventory - 3 until getSizeInventory

    var isBuildingCache = false
    lazy val cache : util.Queue[BlockPos] = new util.PriorityQueue[BlockPos](new Comparator[BlockPos] {
        override def compare(o1: BlockPos, o2: BlockPos): Int =
            o1.distanceSq(pos.getX, pos.getY, pos.getZ)
                    .compareTo(o2.distanceSq(pos.getX, pos.getY, pos.getZ))
    })

    /**
      * Used to get the range of this machines operation, includes upgrades
      */
    def RANGE : Int = {
        4 * Math.max(1, getUpgradeCountByID(IUpgradeItem.MEMORY_DDR1))
    }

    /**
      * Used to get the cost to operate, includes upgrades
      */
    def costToOperate : Int = {
        200 * Math.max(1, getUpgradeCountByID(IUpgradeItem.CPU_SINGLE_CORE))

    }

    /**
      * Gets how many blocks to break based on upgrades, 2 by default
      *
      * @return
      */
    def getChopCount : Int = {
        2 * Math.max(1, getUpgradeCountByID(IUpgradeItem.CPU_SINGLE_CORE) * 16)
    }

    /**
      * The initial size of the inventory
      *
      * @return
      */
    override def initialSize: Int = 18

    /**
      * Add all modes you want, in order, here
      */
    def addValidModes() : Unit = {
        validModes += EnumInputOutputMode.INPUT_ALL
        validModes += EnumInputOutputMode.INPUT_PRIMARY
        validModes += EnumInputOutputMode.INPUT_SECONDARY
        validModes += EnumInputOutputMode.OUTPUT_ALL
        validModes += EnumInputOutputMode.ALL_MODES
    }

    /*******************************************************************************************************************
      **************************************************  Tile Methods  ************************************************
      ******************************************************************************************************************/

    /**
      * Called per tick, does the work
      */
    override def doWork() : Unit = {
        if (!isBuildingCache && TimeUtils.onSecond(2) && energyStorage.getEnergyStored() > costToOperate) {
            if(cache.isEmpty)
                findNextTree()
            else
                chopTree()
            pullInSaplings()
        }

        if (TimeUtils.onSecond(30)) {
            plantSaplings()
        }
    }

    /**
      * Find the next tree in the chopping area
      */
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

    /**
      * Chop the tree cache found
      */
    def chopTree() : Unit = {
        breakable {
            for (x <- 0 until getChopCount) {
                if(cache.isEmpty)
                    break
                val logPosition = cache.peek()
                if (worldObj.getBlockState(logPosition).getBlock != null) {
                    worldObj.getBlockState(logPosition).getBlock match {
                        case log: Block if log.isWood(worldObj, logPosition) =>
                            if (chopLog(logPosition))
                                cache.poll()
                            else
                                break
                        case leave: BlockLeaves =>
                            if (chopLeave(logPosition))
                                cache.poll()
                            else
                                break
                        case _ => cache.poll()
                    }
                } else
                    cache.poll()
            }
        }
    }

    /**
      * Chop a log
      *
      * @param logPosition The log position
      * @return True if able to chop
      */
    def chopLog(logPosition : BlockPos, isLast : Boolean = false) : Boolean = {
        if(getStackInSlot(AXE_SLOT) != null && addHarvestToInventory(new ItemStack(worldObj.getBlockState(logPosition).getBlock, 1, worldObj.getBlockState(logPosition).getBlock.damageDropped(worldObj.getBlockState(logPosition))), sapling = false)) {
            worldObj.setBlockToAir(logPosition)
            if(getStackInSlot(AXE_SLOT).attemptDamageItem(1, worldObj.rand))
                setStackInSlot(AXE_SLOT, null)
            energyStorage. providePower(costToOperate, true)
            sendValueToClient(ENERGY_UPDATE, energyStorage.getEnergyStored())
            return true
        }
        false
    }

    /**
      * Chop a leave, taking into account the leave drops, eg apples
      *
      * @param leavePosition The leave position
      * @return True if something happened
      */
    def chopLeave(leavePosition : BlockPos, isLast : Boolean = false) : Boolean = {
        if(getStackInSlot(SHEARS_SLOT) != null && addHarvestToInventory(new ItemStack(worldObj.getBlockState(leavePosition).getBlock, 1, worldObj.getBlockState(leavePosition).getBlock.damageDropped(worldObj.getBlockState(leavePosition))), sapling = false)) {
            if(worldObj.getBlockState(leavePosition).getBlock != null)
                worldObj.playEvent(2001, leavePosition, Block.getIdFromBlock(worldObj.getBlockState(leavePosition).getBlock))
            worldObj.setBlockToAir(leavePosition)
            if(getStackInSlot(SHEARS_SLOT).attemptDamageItem(1, worldObj.rand))
                setStackInSlot(SHEARS_SLOT, null)
            energyStorage.providePower(costToOperate, true)
            sendValueToClient(ENERGY_UPDATE, energyStorage.getEnergyStored)
            return true
        } else {
            for(item <- worldObj.getBlockState(leavePosition).getBlock.asInstanceOf[BlockLeaves]
                    .getDrops(worldObj, leavePosition, worldObj.getBlockState(leavePosition), 1)) {
                addHarvestToInventory(item, Block.getBlockFromItem(item.getItem) != null &&
                        Block.getBlockFromItem(item.getItem).isInstanceOf[BlockSapling])
            }
            worldObj.setBlockToAir(leavePosition)
            return true
        }
        false
    }

    /**
      * Scan the area around and pick saplings
      */
    def pullInSaplings() : Unit = {
        val items = worldObj.getEntitiesWithinAABB(classOf[EntityItem], new AxisAlignedBB(pos.getX - RANGE - 4, pos.getY, pos.getZ - RANGE - 4, pos.getX + RANGE + 5, pos.getY + 2, pos.getZ + RANGE + 5))
        for(x <- 0 until items.size()) {
            val item = items.get(x)
            item.getEntityItem.getItem match {
                case sapling : Item if Block.getBlockFromItem(sapling).isInstanceOf[BlockSapling] =>
                    if(addHarvestToInventory(item.getEntityItem, sapling = true)) {
                        item.setDead()
                    }
                case sapling : Items.APPLE.type =>
                    if(addHarvestToInventory(item.getEntityItem, sapling = false)) {
                        item.setDead()
                    }
                case _ =>
            }
        }
    }

    /**
      * Take saplings in the inventory and plant them
      */
    def plantSaplings() : Unit = {
        if(hasSaplings) {
            for (x <- pos.getX - RANGE + 1 until pos.getX + RANGE) {
                for (z <- pos.getZ - RANGE + 1 until pos.getZ + RANGE) {
                    val blockPos = new BlockPos(x, pos.getY, z)
                    if(worldObj.isAirBlock(blockPos) && worldObj.getBlockState(blockPos.down()) != null &&
                            (worldObj.getBlockState(blockPos.down()).getBlock == Blocks.DIRT || worldObj.getBlockState(blockPos.down()).getBlock == Blocks.GRASS)) {
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

    /**
      * Checks if this block actually has some saplings
      *
      * @return
      */
    def hasSaplings: Boolean = {
        for(x <- SAPLING_SLOTS) {
            if(getStackInSlot(x) != null)
                return true
        }
        false
    }

    /**
      * Gets the state for the next sapling to place and reduces the stack
      *
      * @return
      */
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

    /**
      * Adds a harvest into the inventory, making sure to put saplings in the correct place
      *
      * @param stack The stack to insert
      * @param sapling True to try place in sapling slot first
      * @return True if able to place in inventory
      */
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

    /**
      * This will try to take things from other inventories and put it into ours
      */
    override def tryInput(): Unit = {
        for(dir <- EnumFacing.values()) {
            if(canInputFromSide(dir)) // Try Axe Slot
                InventoryUtils.moveItemInto(worldObj.getTileEntity(pos.offset(dir)), -1, this, AXE_SLOT, 64,
                    dir.getOpposite, doMove = true, checkSidedTarget = false)
            if(canInputFromSide(dir, isPrimary = false)) // Try Shears Slot
                InventoryUtils.moveItemInto(worldObj.getTileEntity(pos.offset(dir)), -1, this, SHEARS_SLOT, 64,
                    dir.getOpposite, doMove = true, checkSidedTarget = false)
        }
    }

    /**
      * This will try to take things from our inventory and try to place them in others
      */
    override def tryOutput(): Unit = {
        for(dir <- EnumFacing.values()) {
            if(canOutputFromSide(dir))
                for(x <- getOutputSlots(getModeForSide(dir))) // Try to get rid of our things
                    InventoryUtils.moveItemInto(this, x, worldObj.getTileEntity(pos.offset(dir)), -1, 64,
                        dir.getOpposite, doMove = true, checkSidedSource = false)
        }
    }

    /**
      * Use this to set all variables back to the default values, usually means the operation failed
      */
    override def reset(): Unit = {}

    /*******************************************************************************************************************
      ************************************************ Inventory methods ***********************************************
      ******************************************************************************************************************/

    /**
      * Used to get what slots are allowed to be input
      *
      * @return The slots to input from
      */
    override def getInputSlots(mode : EnumInputOutputMode): Array[Int] = {
        if(mode == EnumInputOutputMode.INPUT_ALL)
            Array(AXE_SLOT, SHEARS_SLOT)
        else if(mode == EnumInputOutputMode.INPUT_PRIMARY)
            Array(AXE_SLOT)
        else if(mode == EnumInputOutputMode.INPUT_SECONDARY)
            Array(SHEARS_SLOT)
        else if(mode == EnumInputOutputMode.DEFAULT)
            Array(AXE_SLOT, SHEARS_SLOT)
        else
            Array()
    }

    /**
      * Used to get what slots are allowed to be output
      *
      * @return The slots to output from
      */
    override def getOutputSlots(mode : EnumInputOutputMode) : Array[Int] = SHEARS_SLOT until getSizeInventory - 3 toArray

    override def getSlotsForFace(side: EnumFacing): Array[Int] = 0 until getSizeInventory toArray

    override def canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean =
        index > SHEARS_SLOT && index < getSizeInventory - 3

    override def canInsertItem(slot: Int, itemStackIn: ItemStack, direction: EnumFacing): Boolean = {
        slot match {
            case AXE_SLOT if itemStackIn != null => itemStackIn.getItem.isInstanceOf[ItemTool] && itemStackIn.getItem.asInstanceOf[ItemTool].getToolClasses(itemStackIn).contains("axe")
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
            case AXE_SLOT if itemStackIn != null => itemStackIn.getItem.isInstanceOf[ItemTool] && itemStackIn.getItem.asInstanceOf[ItemTool].getToolClasses(itemStackIn).contains("axe")
            case SHEARS_SLOT if itemStackIn != null => itemStackIn.getItem.isInstanceOf[ItemShears]
            case _ => true
        }
    }

    /*******************************************************************************************************************
      ************************************************ Energy methods **************************************************
      ******************************************************************************************************************/

    /**
      * Return true if you want this to be able to provide energy
      *
      * @return
      */
    def isProvider : Boolean = false

    /**
      * Return true if you want this to be able to receive energy
      *
      * @return
      */
    def isReceiver : Boolean = true

    /*******************************************************************************************************************
      *************************************************** Misc methods *************************************************
      ******************************************************************************************************************/

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

    override def getDescription : String = {
        "" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + ClientUtils.translate("neotech.text.stats") + ":\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.energyUsage") + ":\n" +
                GuiColor.WHITE + "  " + costToOperate + " RF/chop\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.rangeTree") + ":\n" +
                GuiColor.WHITE + "  " + (RANGE + 1) + "x"  + (RANGE + 1) + " blocks\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.chopCount") + ":\n" +
                GuiColor.WHITE + "  " + getChopCount + "  \n\n" +
                GuiColor.WHITE + I18n.translateToLocal("neotech.treeFarm.desc") + "\n\n" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + I18n.translateToLocal("neotech.text.upgrades") + ":\n" + GuiTextFormat.RESET +
                GuiColor.YELLOW + GuiTextFormat.BOLD + I18n.translateToLocal("neotech.text.processors") + ":\n" +
                GuiColor.WHITE + I18n.translateToLocal("neotech.treeFarm.processorUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + I18n.translateToLocal("neotech.text.hardDrives") + ":\n" +
                GuiColor.WHITE + I18n.translateToLocal("neotech.treeFarm.hardDriveUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + I18n.translateToLocal("neotech.text.control") + ":\n" +
                GuiColor.WHITE + I18n.translateToLocal("neotech.electricFurnace.controlUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + I18n.translateToLocal("neotech.text.expansion") + ":\n" +
                GuiColor.WHITE +  I18n.translateToLocal("neotech.electricFurnace.expansionUpgrade.desc")
    }

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
    override def getRedstoneOutput: Int = (energyStorage.getEnergyStored * 16) / energyStorage.getMaxStored

    /**
      * Used to get what particles to spawn. This will be called when the tile is active
      */
    override def spawnActiveParticles(xPos: Double, yPos: Double, zPos: Double): Unit = {}

    /**
      * Used to check if this tile is active or not
      *
      * @return True if active state
      */
    override def isActive: Boolean = {
        false
    }
}
