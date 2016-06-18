package com.dyonovan.neotech.common.tiles.machines.processors

import com.dyonovan.neotech.client.gui.machines.processors.GuiThermalBinder
import com.dyonovan.neotech.collections.EnumInputOutputMode
import com.dyonovan.neotech.common.container.machines.processors.ContainerThermalBinder
import com.dyonovan.neotech.common.tiles.MachineProcessor
import com.dyonovan.neotech.managers.ItemManager
import com.dyonovan.neotech.tools.tools.BaseElectricTool
import com.dyonovan.neotech.tools.upgradeitems.{BaseUpgradeItem, ThermalBinderItem}
import com.dyonovan.neotech.utils.ClientUtils
import com.teambr.bookshelf.client.gui.{GuiColor, GuiTextFormat}
import com.teambr.bookshelf.common.tiles.traits.FluidHandler
import com.teambr.bookshelf.util.InventoryUtils
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.text.translation.I18n
import net.minecraft.util.{EnumFacing, EnumParticleTypes}
import net.minecraft.world.World
import net.minecraftforge.fluids.{IFluidHandler, Fluid, FluidTank}

import scala.collection.mutable.ArrayBuffer

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
      * The initial size of the inventory
      *
      * @return
      */
    override def initialSize: Int = 6

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

    /**
      * Used to get how long it takes to cook things, you should check for upgrades at this point
      *
      * @return The time it takes in ticks to cook the current item
      */
    override def getCookTime : Int = {
        if(getUpgradeBoard != null && getUpgradeBoard.getProcessorCount > 0)
            (200 * getCount) - (getCount * (getUpgradeBoard.getProcessorCount * 20))
        else
            200 * getCount
    }

    /**
      * Used to tell if this tile is able to process
      *
      * @return True if you are able to process
      */
    override def canProcess : Boolean = {
        if(hasValidInput && isRunning)
            true
        else {
            isRunning = false
            cookTime = 0
            failCoolDown = 40
            false
        }
    }

    /**
      * Used to actually cook the item. You should reset values here if need be
      */
    override def cook(): Unit = cookTime += 1

    /**
      * Called when the tile has completed the cook process
      */
    override def completeCook(): Unit = build(); lastCount = 0

    /**
      * Get the output of the recipe
      *
      * @param stack The input
      * @return The output
      */
    override def getOutputForStack(stack: ItemStack): ItemStack = {
        if (stack != null && stack.getItem.isInstanceOf[ThermalBinderItem])
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

    /*******************************************************************************************************************
      **************************************************  Tile Methods  ************************************************
      ******************************************************************************************************************/

    /**
      * This will try to take things from other inventories and put it into ours
      */
    override def tryInput(): Unit = {
        for(dir <- EnumFacing.values()) {
            // Try for items
            if (canInputFromSide(dir)) {
                InventoryUtils.moveItemInto(worldObj.getTileEntity(pos.offset(dir)), -1, this, OBJECT_INPUT, 64,
                    dir.getOpposite, doMove = true, checkSidedTarget = false)
                for (x <- INPUT_SLOTS)
                    InventoryUtils.moveItemInto(worldObj.getTileEntity(pos.offset(dir)), -1, this, x, 64,
                        dir.getOpposite, doMove = true, checkSidedTarget = false)
            }

            // Try for fluids
            worldObj.getTileEntity(pos.offset(dir)) match {
                case otherTank : IFluidHandler if canInputFromSide(dir, isPrimary = false) =>
                    if(otherTank.getTankInfo(dir.getOpposite) != null && otherTank.getTankInfo(dir.getOpposite).nonEmpty &&
                            otherTank.getTankInfo(dir.getOpposite)(0) != null && otherTank.getTankInfo(dir.getOpposite)(0).fluid != null && canFill(dir, otherTank.getTankInfo(dir.getOpposite)(0).fluid.getFluid)) {
                        val amount = fill(dir, otherTank.drain(dir.getOpposite, 1000, false), doFill = false)
                        if (amount > 0)
                            fill(dir, otherTank.drain(dir.getOpposite, amount, true), doFill = true)
                    }
                case _ =>
            }
        }
    }

    /**
      * This will try to take things from our inventory and try to place them in others
      */
    override def tryOutput(): Unit = {
        for(dir <- EnumFacing.values()) {
            if(canOutputFromSide(dir))
                InventoryUtils.moveItemInto(this, OBJECT_OUTPUT, worldObj.getTileEntity(pos.offset(dir)), -1, 64,
                    dir.getOpposite, doMove = true, checkSidedSource = false)
        }
    }

    /**
      * Write the tag
      */
    override def writeToNBT(tag: NBTTagCompound): NBTTagCompound = {
        super[MachineProcessor].writeToNBT(tag)
        super[FluidHandler].writeToNBT(tag)
        tag.setInteger("Count", count)
        tag
    }

    /**
      * Read the tag
      */
    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[MachineProcessor].readFromNBT(tag)
        super[FluidHandler].readFromNBT(tag)
        if(tanks == null || tanks.isEmpty)
            setupTanks()
        count = tag.getInteger("Count")
    }

    /*******************************************************************************************************************
      ************************************************* Recipe methods *************************************************
      ******************************************************************************************************************/

    /**
      * Checks if we can process
      *
      * @return True if all is good
      */
    def hasValidInput : Boolean = {
        if(energyStorage.getEnergyStored <= 0 || getStackInSlot(OBJECT_OUTPUT) != null || getStackInSlot(OBJECT_INPUT) == null || tanks(TIN_TANK).getFluid == null
                || tanks(TIN_TANK).getFluidAmount <= REQUIRED_MB)
            return false
        else if(isRunning) {
            //Check if the input is a valid item (should always be but just to be safe, that way old machines won't break)
            getStackInSlot(OBJECT_INPUT).getItem match {
                case inputItem : ThermalBinderItem =>

                    //Check we have something at all
                    var hasInput = false
                    for(x <- INPUT_SLOTS)
                        if(getStackInSlot(x) != null) hasInput = true

                    //Break if nothing found, or if we need to have empty (ie no upgrades installed)
                    if(!hasInput)
                        return false

                    // Check that the inputs are valid for the item
                    for(x <- INPUT_SLOTS) {
                        if (getStackInSlot(x) != null &&
                                !inputItem.isAcceptableUpgrade(getStackInSlot(x).getItem.asInstanceOf[BaseUpgradeItem].getUpgradeName))
                            return false // The stack was not valid
                        if(getStackInSlot(x) != null &&
                                !getStackInSlot(x).getItem.asInstanceOf[BaseUpgradeItem].canAcceptLevel(getStackInSlot(OBJECT_INPUT),
                                    getStackInSlot(x).stackSize, getStackInSlot(x).getItem.asInstanceOf[BaseUpgradeItem].getUpgradeName))
                            return false
                    }

                    // Check for no duplicates
                    // Build the set
                    val listOfItems = ArrayBuffer[String]()
                    for(x <- INPUT_SLOTS)
                        if(getStackInSlot(x) != null) listOfItems += getStackInSlot(x).getItem.asInstanceOf[BaseUpgradeItem].getUpgradeName

                    // Check that there are no duplicates
                    if(listOfItems.size != listOfItems.toSet.size) // Sets contain no duplicates, so if that size is different return
                        return false

                    // Special Battery Case
                    if(getStackInSlot(OBJECT_INPUT).getItem.isInstanceOf[BaseElectricTool]
                            && listOfItems.size == 1 && listOfItems.contains(ItemManager.basicRFBattery.getUpgradeName))
                        return true

                    // Check count
                    if(!inputItem.canAcceptCount(getStackInSlot(OBJECT_INPUT),
                        (getStackInSlot(INPUT1), getStackInSlot(INPUT2), getStackInSlot(INPUT3), getStackInSlot(INPUT4))))
                        return false

                    // Everything looks good, return true
                    return true
                case _ => return false
            }
        }
        false
    }

    /**
      * Creates the Motherboard or removes the upgrades from it
      */
    def build(): Unit = {
        getStackInSlot(OBJECT_INPUT).getItem match {
            case binderItem : ThermalBinderItem =>
                val tag = if (getStackInSlot(OBJECT_INPUT).hasTagCompound) getStackInSlot(OBJECT_INPUT).getTagCompound else new NBTTagCompound

                // Write upgrade information
                for(x <- INPUT_SLOTS)
                    if (getStackInSlot(x) != null)
                        getStackInSlot(x).getItem.asInstanceOf[BaseUpgradeItem]
                                .writeInfoToNBT(getStackInSlot(OBJECT_INPUT), tag, getStackInSlot(x))

                // Write tag to Input
                getStackInSlot(OBJECT_INPUT).setTagCompound(tag)

                // Moves stacks
                if(getStackInSlot(OBJECT_INPUT).getItem != ItemManager.upgradeMBEmpty)
                    setStackInSlot(OBJECT_OUTPUT, getStackInSlot(OBJECT_INPUT).copy())
                else {
                    val stack = new ItemStack(ItemManager.upgradeMBFull)
                    stack.setTagCompound(tag)
                    setStackInSlot(OBJECT_OUTPUT, stack)
                }
                setStackInSlot(OBJECT_INPUT, null)
                for(x <- INPUT_SLOTS)
                    setStackInSlot(x, null)

                // Drain input
                tanks(TIN_TANK).drain(REQUIRED_MB, true)
                isRunning = false
                sendValueToClient(RUNNING_VARIABLE_ID, 0)
            case _ =>
        }
    }

    /**
      * Used to get the count of upgrades on the motherboard or to be added to the empty motherboard
      *
      * @return How many upgrades are present
      */
    def getCount: Int = {
        count = 0
        if (getStackInSlot(OBJECT_INPUT) != null) {
            getStackInSlot(OBJECT_INPUT).getItem match {
                case binderItem : ThermalBinderItem =>
                    if(hasEmptyInput && getStackInSlot(OBJECT_INPUT).hasTagCompound) {
                        count = binderItem.getUpgradeCount(getStackInSlot(OBJECT_INPUT))
                        return count
                    }
                    else {
                        for(x <- INPUT_SLOTS)
                            if(getStackInSlot(x) != null)
                                count += getStackInSlot(x).stackSize
                    }
                case _ =>
            }
        }
        count
    }

    /**
      * Used to check if the input is empty, usually used to tell if we are taking things out or putting in
      *
      * @return
      */
    def hasEmptyInput: Boolean = {
        for(x <- INPUT_SLOTS)
            if(getStackInSlot(x) != null) return false
        true
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
      * Returns true if the given fluid can be inserted into the given direction.
      *
      * More formally, this should return true if fluid is able to enter from the given direction.
      */
    override def canFill(from: EnumFacing, fluid: Fluid): Boolean =
        !isDisabled(from) && fluid.getName.equals("tin")

    /**
      * Called when something happens to the tank, you should mark the block for update here if a tile
      */
    override def onTankChanged(tank: FluidTank): Unit =
        worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 6)
    
    /*******************************************************************************************************************
      *********************************************** Inventory methods ************************************************
      ******************************************************************************************************************/

    /**
      * Used to get what slots are allowed to be output
      *
      * @return The slots to output from
      */
    override def getOutputSlots(mode : EnumInputOutputMode) : Array[Int] = Array(OBJECT_OUTPUT)

    /**
      * Used to get what slots are allowed to be input
      *
      * @return The slots to input from
      */
    override def getInputSlots(mode : EnumInputOutputMode) : Array[Int] = Array(INPUT1, INPUT2, INPUT3, INPUT4, OBJECT_INPUT)

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
            case RUNNING_VARIABLE_ID => isRunning = true; lastCount = getCount; worldObj.setBlockState(pos, worldObj.getBlockState(pos), 6)
            case BUILD_NOW_ID        => build(); worldObj.setBlockState(pos, worldObj.getBlockState(pos), 6)
            case _ => //No Operation, not defined ID
        }
        super.setVariable(id, value)
    }

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

    override def getDescription : String = {
        "" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + ClientUtils.translate("neotech.text.stats") + ":\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.energyUsage") + ":\n" +
                GuiColor.WHITE + "  " + getEnergyCostPerTick + " RF/tick\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.processTime") + ":\n" +
                GuiColor.WHITE + "  " + getCookTime + " ticks\n\n" + GuiColor.WHITE + I18n.translateToLocal("neotech.thermalBinder.desc") + "\n\n" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + I18n.translateToLocal("neotech.text.upgrades") + ":\n" + GuiTextFormat.RESET +
                GuiColor.YELLOW + GuiTextFormat.BOLD + I18n.translateToLocal("neotech.text.processors") + ":\n" +
                GuiColor.WHITE + I18n.translateToLocal("neotech.thermalBinder.processorUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + I18n.translateToLocal("neotech.text.hardDrives") + ":\n" +
                GuiColor.WHITE + I18n.translateToLocal("neotech.electricFurnace.hardDriveUpgrade.desc") + "\n\n" +
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
    override def getRedstoneOutput: Int = InventoryUtils.calcRedstoneFromInventory(this)

    /**
      * Used to get what particles to spawn. This will be called when the tile is active
      */
    override def spawnActiveParticles(x: Double, y: Double, z: Double): Unit = {
        worldObj.spawnParticle(EnumParticleTypes.REDSTONE, x, y + 0.4, z, 0, 255, 0)
        worldObj.spawnParticle(EnumParticleTypes.REDSTONE, x, y + 0.4, z, 0, 255, 0)
    }
}
