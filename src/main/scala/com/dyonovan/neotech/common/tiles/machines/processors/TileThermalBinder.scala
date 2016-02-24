package com.dyonovan.neotech.common.tiles.machines.processors

import com.dyonovan.neotech.client.gui.machines.processors.GuiThermalBinder
import com.dyonovan.neotech.collections.UpgradeBoard
import com.dyonovan.neotech.common.blocks.traits.ThermalBinderItem
import com.dyonovan.neotech.common.container.machines.processors.ContainerThermalBinder
import com.dyonovan.neotech.common.tiles.MachineProcessor
import com.dyonovan.neotech.managers.{BlockManager, ItemManager}
import com.dyonovan.neotech.tools.upgradeitems.BaseUpgradeItem
import com.teambr.bookshelf.client.gui.{GuiTextFormat, GuiColor}
import com.teambr.bookshelf.common.tiles.traits.FluidHandler
import com.teambr.bookshelf.notification.{NotificationHelper, Notification}
import com.teambr.bookshelf.util.InventoryUtils
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{StatCollector, EnumFacing, EnumParticleTypes}
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidTank

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan, Paul Davis <pauljoda>
  * @since August 21, 2015
  */
class TileThermalBinder extends MachineProcessor[ItemStack, ItemStack] with FluidHandler {

    lazy val RUNNING_VARIABLE_ID = 10
    lazy val BUILD_NOW_ID        = 11

    lazy val INPUT1        = 0
    lazy val INPUT2        = 1
    lazy val INPUT3        = 2
    lazy val INPUT4        = 3
    lazy val INPUT_SLOTS   = INPUT1 to INPUT4 // Just as a handy little helper
    lazy val OBJECT_INPUT  = 4
    lazy val OBJECT_OUTPUT = 5

    lazy val BASE_ENERGY_TICK = 200

    var count     = 0
    var lastCount = 0

    var isRunning = false

    /**
      * Used to get how long it takes to cook things, you should check for upgrades at this point
      *
      * @return The time it takes in ticks to cook the current item
      */
    override def getCookTime : Int = {
        if(getUpgradeBoard != null && getUpgradeBoard.getProcessorCount > 0)
            (200 * getCount) - (getUpgradeBoard.getProcessorCount * ((200 * getCount) / 8.33).toInt)
        else
            200 * getCount
    }

    /**
      * Used to tell if this tile is able to process
      *
      * @return True if you are able to process
      */
    override def canProcess : Boolean = {
        if(isRunning && energyStorage.getEnergyStored >= getEnergyCostPerTick) {
            if(getStackInSlot(OBJECT_OUTPUT) == null && getStackInSlot(OBJECT_INPUT) != null && lastCount != 0 && getCount == lastCount)
                return true
        }
        isRunning = false
        cookTime = 0
        failCoolDown = 40
        false
    }

    /**
      * Used to check if the amount of upgrades do not exceed max allowed
      *
      * @return True if valid configuration
      */
    def isValid: Boolean = {
        if (getStackInSlot(OBJECT_INPUT) == null) return false

        val board = getStackInSlot(OBJECT_INPUT)
        if (board.hasTagCompound) {
            for (i <- 0 to 3) {
                if (getStackInSlot(i) != null) {
                    val notify = new Notification(new ItemStack(BlockManager.thermalBinder), "Slots Occupied",
                        "Empty Slots", Notification.SHORT_DURATION)
                    NotificationHelper.addNotification(notify)
                    return false
                }}
            true
        } else {
            var pro = 0
            var hd = 0
            var cap = 0
            var exp = 0
            for (i <- 0 to 3) {
                if (getStackInSlot(i) != null) {
                    val item = getStackInSlot(i).getItem
                    item match {
                        case _: ItemManager.upgradeControl.type => cap += getStackInSlot(i).stackSize
                        case _: ItemManager.upgradeExpansion.type => exp += getStackInSlot(i).stackSize
                        case _: ItemManager.upgradeHardDrive.type => hd += getStackInSlot(i).stackSize
                        case _: ItemManager.upgradeProcessor.type => pro += getStackInSlot(i).stackSize
                        case _ =>
                    }
                }
            }
            if (pro <= 8 && hd <= 8 && cap <= 1 && exp <= 1) true
            else {
                val notify = new Notification(new ItemStack(BlockManager.thermalBinder), "Invalid Upgrades",
                    "To Many Upgrades", Notification.SHORT_DURATION)
                NotificationHelper.addNotification(notify)
                false
            }
        }
    }

    /**
      * Get the output of the recipe
      *
      * @param stack The input
      * @return The output
      */
    override def getOutputForStack(stack: ItemStack): ItemStack = {
        if (stack != null && (stack.getItem == ItemManager.upgradeMBEmpty || stack.getItem == ItemManager.upgradeMBFull))
            new ItemStack(ItemManager.upgradeMBFull) //Just used to tell the Sided inventory that we have something, not actual output
        else
            null
    }

    /**
      * Get the output of the recipe
      *
      * @param input The input
      * @return The output
      */
    override def getOutput(input: ItemStack): ItemStack = getOutputForStack(input)

    /**
      * Used to actually cook the item. You should reset values here if need be
      */
    override def cook(): Unit = cookTime += 1

    /**
      * Called when the tile has completed the cook process
      */
    override def completeCook(): Unit = build(); lastCount = 0

    /**
      * Used to get how much energy to drain per tick, you should check for upgrades at this point
      *
      * @return How much energy to drain per tick
      */
    override def getEnergyCostPerTick: Int = {
        if(getUpgradeBoard != null && getUpgradeBoard.getProcessorCount > 0)
            BASE_ENERGY_TICK * getUpgradeBoard.getProcessorCount
        else
            BASE_ENERGY_TICK
    }

    override def getDescription : String = {
        GuiColor.YELLOW + "" + GuiTextFormat.BOLD + StatCollector.translateToLocal("tile.neotech:thermalBinder.name") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.thermalBinder.desc") + "\n\n" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + StatCollector.translateToLocal("neotech.text.upgrades") + ":\n" + GuiTextFormat.RESET +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.processors") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.thermalBinder.processorUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.hardDrives") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.electricFurnace.hardDriveUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.control") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.electricFurnace.controlUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.expansion") + ":\n" +
                GuiColor.WHITE +  StatCollector.translateToLocal("neotech.electricFurnace.expansionUpgrade.desc")
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
        new ContainerThermalBinder(player.inventory, this)

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
        new GuiThermalBinder(player, this)

    /**
      * Creates the Motherboard or removes the upgrades from it
      */
    def build(): Unit = {
        getStackInSlot(OBJECT_INPUT).getItem match {
            case ItemManager.upgradeMBEmpty if hasSlotUpgrades =>
                val tag = writeToMB()
                val newMB = new ItemStack(ItemManager.upgradeMBFull)
                newMB.setTagCompound(tag)
                setInventorySlotContents(OBJECT_OUTPUT, newMB)
                setInventorySlotContents(OBJECT_INPUT, null)
            case ItemManager.upgradeMBFull if !hasSlotUpgrades =>
                val mb = UpgradeBoard.getBoardFromStack(getStackInSlot(OBJECT_INPUT))
                var slot = 0
                if (mb.hasControl) {
                    setInventorySlotContents(slot, new ItemStack(ItemManager.upgradeControl, 1))
                    slot += 1
                }
                if (mb.hasExpansion) {
                    setInventorySlotContents(slot, new ItemStack(ItemManager.upgradeExpansion, 1))
                    slot += 1
                }
                if (mb.getHardDriveCount > 0) {
                    setInventorySlotContents(slot, new ItemStack(ItemManager.upgradeHardDrive, mb.getHardDriveCount))
                    slot += 1
                }
                if (mb.getProcessorCount > 0) {
                    setInventorySlotContents(slot, new ItemStack(ItemManager.upgradeProcessor, mb.getProcessorCount))
                    slot += 1
                }
                setInventorySlotContents(OBJECT_INPUT, null)
                setInventorySlotContents(OBJECT_OUTPUT, new ItemStack(ItemManager.upgradeMBEmpty, 1))
            case _ =>
        }
    }

    /**
      * Used to get the count of upgrades on the motherboard or to be added to the empty motherboard
      *
      * @return How many upgrades are present
      */
    def getCount: Int = {
        if (getStackInSlot(OBJECT_INPUT) != null) {
            getStackInSlot(OBJECT_INPUT).getItem match {
                case ItemManager.upgradeMBEmpty if hasSlotUpgrades =>
                    var count = 0
                    for (i <- 0 to 3) {
                        if (getStackInSlot(i) != null)
                            count += getStackInSlot(i).stackSize
                    }
                    return count
                case ItemManager.upgradeMBFull if !hasSlotUpgrades =>
                    val mb = UpgradeBoard.getBoardFromStack(getStackInSlot(OBJECT_INPUT))
                    if (mb != null) {
                        var count = mb.getHardDriveCount + mb.getProcessorCount
                        if (mb.hasControl) count += 1
                        if (mb.hasExpansion) count += 1
                        return count
                    }
                case _ =>
            }
        }
        0
    }

    /**
      * Used to write the tag needed to the ItemStack to hold the upgrade info
      *
      * @return The tag to write to the ItemStack
      */
    private def writeToMB(): NBTTagCompound = {
        val tag = new NBTTagCompound
        for (i <- 0 to 3) {
            if (getStackInSlot(i) != null) {
                getStackInSlot(i).getItem match {
                    case item: ItemManager.upgradeControl.type =>
                        if (!tag.getBoolean("Control")) {
                            tag.setBoolean("Control", true)
                            setInventorySlotContents(i, null)
                        }
                    case item: ItemManager.upgradeExpansion.type =>
                        if (!tag.getBoolean("Expansion")) {
                            tag.setBoolean("Expansion", true)
                            setInventorySlotContents(i, null)
                        }
                    case item: ItemManager.upgradeHardDrive.type =>
                        val countTag = tag.getInteger("HardDrive")
                        if (countTag + getStackInSlot(i).stackSize <= 8) {
                            tag.setInteger("HardDrive", countTag + getStackInSlot(i).stackSize)
                            setInventorySlotContents(i, null)
                        } else if (countTag + getStackInSlot(i).stackSize > 8) {
                            tag.setInteger("HardDrive", 8)
                            getStackInSlot(i).stackSize -= 8 - countTag
                        }
                    case item: ItemManager.upgradeProcessor.type =>
                        val countTag = tag.getInteger("Processor")
                        if (countTag + getStackInSlot(i).stackSize <= 8) {
                            tag.setInteger("Processor", countTag + getStackInSlot(i).stackSize)
                            setInventorySlotContents(i, null)
                        } else if (countTag + getStackInSlot(i).stackSize > 8) {
                            tag.setInteger("Processor", 8)
                            getStackInSlot(i).stackSize -= 8 - countTag
                        }
                }
            }
        }
        tag
    }

    /**
      * Used to tell if the upgrades are in the slots
      *
      * @return True if upgrades are present
      */
    def hasSlotUpgrades: Boolean = {
        for (i <- 0 to 3) {
            if (getStackInSlot(i) != null) return true
        }
        false
    }

    /**
      * Write the tag
      */
    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super.writeToNBT(tag)
        super[FluidHandler].writeToNBT(tag)
        tag.setInteger("Count", count)
    }

    /**
      * Read the tag
      */
    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super.readFromNBT(tag)
        super[FluidHandler].readFromNBT(tag)
        count = tag.getInteger("Count")
    }

    /*******************************************************************************************************************
      ************************************************* Recipe methods *************************************************
      ******************************************************************************************************************/

    def hasValidInput : Boolean = {
        if(energyStorage.getEnergyStored <= 0 || getStackInSlot(OBJECT_OUTPUT) != null || getStackInSlot(OBJECT_INPUT) == null || tanks(TIN_TANK).getFluid == null
                || tanks(TIN_TANK).getFluidAmount <= REQUIRED_MB)
            return false
        else {

        }

        false
    }

    /*******************************************************************************************************************
      ************************************************** Fluid methods *************************************************
      ******************************************************************************************************************/

    lazy val TIN_TANK    = 0  // Tin Tank ID
    lazy val REQUIRED_MB = 20 // The amount required to process

    /**
      * Used to set up the tanks needed. You can insert any number of tanks
      */
    override def setupTanks(): Unit = tanks += new FluidTank(bucketsToMB(1))

    /**
      * Which tanks can input
      */
    override def getInputTanks: Array[Int] = Array(TIN_TANK)

    /**
      * Which tanks can output
      */
    override def getOutputTanks: Array[Int] = Array()

    /**
      * Called when something happens to the tank, you should mark the block for update here if a tile
      */
    override def onTankChanged(tank: FluidTank): Unit = worldObj.markBlockForUpdate(pos)

    /*******************************************************************************************************************
      ************************************************ Inventory methods ***********************************************
      ******************************************************************************************************************/

    /**
      * The initial size of the inventory
      *
      * @return
      */
    override def initialSize: Int = 6

    /**
      * Used to get what slots are allowed to be output
      *
      * @return The slots to output from
      */
    override def getOutputSlots : Array[Int] = Array(OBJECT_OUTPUT)

    /**
      * Used to get what slots are allowed to be input
      *
      * @return The slots to input from
      */
    override def getInputSlots : Array[Int] = Array(INPUT1, INPUT2, INPUT3, INPUT4, OBJECT_INPUT)

    /**
      * Returns true if automation can insert the given item in the given slot from the given side. Args: slot, item,
      * side
      */
    override def canInsertItem(slot: Int, itemStackIn: ItemStack, direction: EnumFacing): Boolean =
        isItemValidForSlot(slot, itemStackIn)

    /**
      * Returns true if automation can extract the given item in the given slot from the given side. Args: slot, item,
      * side
      */
    override def canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean = index == OBJECT_OUTPUT

    /**
      * Used to define if an item is valid for a slot
      *
      * @param slot The slot id
      * @param stack The stack to check
      * @return True if you can put this there
      */
    override def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean = {
        val item = stack.getItem
        if (INPUT_SLOTS.contains(slot)) item.isInstanceOf[BaseUpgradeItem]
        else if (slot == OBJECT_INPUT) item.isInstanceOf[ThermalBinderItem]
        else false
    }

    /*******************************************************************************************************************
      ********************************************** Syncable methods **************************************************
      ******************************************************************************************************************/

    /**
      * Used to set the variable for this tile, the Syncable will use this when you send a value to the server
      *
      * @param id The ID of the variable to send
      * @param value The new value to set to (you can use this however you want, eg using the ordinal of EnumFacing)
      */
    override def setVariable(id : Int, value : Double): Unit = {
        id match {
            case RUNNING_VARIABLE_ID => isRunning = true; lastCount = getCount; worldObj.markBlockForUpdate(pos)
            case BUILD_NOW_ID        => build(); worldObj.markBlockForUpdate(pos)
            case _ => //No Operation, not defined ID
        }
        super.setVariable(id, value)
    }

    /*******************************************************************************************************************
      *************************************************** Misc methods *************************************************
      ******************************************************************************************************************/

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
    override def getRedstoneOutput: Int = InventoryUtils.calcRedstoneFromInventory(this)

    /**
      * Used to get what particles to spawn. This will be called when the tile is active
      */
    override def spawnActiveParticles(x: Double, y: Double, z: Double): Unit = {
        worldObj.spawnParticle(EnumParticleTypes.REDSTONE, x, y + 0.4, z, 0, 255, 0)
        worldObj.spawnParticle(EnumParticleTypes.REDSTONE, x, y + 0.4, z, 0, 255, 0)
    }
}
