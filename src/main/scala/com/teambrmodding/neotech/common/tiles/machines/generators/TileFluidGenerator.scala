package com.teambrmodding.neotech.common.tiles.machines.generators

import java.util

import com.teambrmodding.neotech.client.gui.machines.generators.GuiFluidGenerator
import com.teambrmodding.neotech.collections.EnumInputOutputMode
import com.teambrmodding.neotech.common.container.machines.generators.ContainerFluidGenerator
import com.teambrmodding.neotech.common.tiles.MachineGenerator
import com.teambrmodding.neotech.managers.RecipeManager
import com.teambrmodding.neotech.registries.FluidFuelRecipeHandler
import com.teambrmodding.neotech.utils.ClientUtils
import com.teambr.bookshelf.client.gui.{GuiColor, GuiTextFormat}
import com.teambr.bookshelf.common.tiles.traits.FluidHandler
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.text.translation.I18n
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraftforge.fluids._

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since August 21, 2015
  */
class TileFluidGenerator extends MachineGenerator with FluidHandler {

    lazy val BASE_ENERGY_TICK = 175
    lazy val INPUT_SLOT       = 0
    lazy val OUTPUT_SLOT      = 1

    /**
      * The initial size of the inventory
      *
      * @return
      */
    override def initialSize: Int = 2

    /**
      * Add all modes you want, in order, here
      */
    def addValidModes() : Unit = {
        validModes += EnumInputOutputMode.INPUT_ALL
    }

    /**
      * Return the list of upgrades by their id that are allowed in this machine
      * @return A list of valid upgrades
      */
    override def getAcceptableUpgrades: util.ArrayList[String] = {
        val list = new util.ArrayList[String]()
        list.add(IUpgradeItem.CPU_SINGLE_CORE)
        list.add(IUpgradeItem.CPU_DUAL_CORE)
        list.add(IUpgradeItem.CPU_QUAD_CORE)
        list.add(IUpgradeItem.CPU_OCT_CORE)
        list.add(IUpgradeItem.MEMORY_DDR1)
        list.add(IUpgradeItem.MEMORY_DDR2)
        list.add(IUpgradeItem.MEMORY_DDR3)
        list.add(IUpgradeItem.MEMORY_DDR4)
        list.add(IUpgradeItem.PSU_250W)
        list.add(IUpgradeItem.PSU_500W)
        list.add(IUpgradeItem.PSU_750W)
        list.add(IUpgradeItem.PSU_960W)
        list.add(IUpgradeItem.TRANSFORMER)
        list.add(IUpgradeItem.REDSTONE_CIRCUIT)
        list.add(IUpgradeItem.NETWORK_CARD)
        list
    }

    /**
      * This method handles how much energy to produce per tick
      *
      * @return How much energy to produce per tick
      */
    override def getEnergyProduced: Int = {
        BASE_ENERGY_TICK * getMultiplierByCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY) +
                ((getMultiplierByCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU) - 1) * 12)
    }

    /**
      * Called to tick generation. This is where you add power to the generator
      */
    override def generate(): Unit =
    energyStorage.receivePower(getEnergyProduced, true)

    /**
      * Called per tick to manage burn time. You can do nothing here if there is nothing to generate. You should decrease burn time here
      * You should be handling checks if burnTime is 0 in this method, otherwise the tile won't know what to do
      *
      * @return True if able to continue generating
      */
    override def manageBurnTime(): Boolean = {
        //Handle Items
        if(getStackInSlot(INPUT_SLOT) != null && FluidContainerRegistry.getFluidForFilledItem(getStackInSlot(INPUT_SLOT)) != null &&
                getStackInSlot(OUTPUT_SLOT) == null) {
            val fluidStackCopy = FluidContainerRegistry.getFluidForFilledItem(getStackInSlot(INPUT_SLOT))
            if(tanks(INPUT_TANK).getFluidAmount + FluidContainerRegistry.getFluidForFilledItem(getStackInSlot(INPUT_SLOT)).amount < tanks(INPUT_TANK).getCapacity &&
                    FluidContainerRegistry.drainFluidContainer(getStackInSlot(INPUT_SLOT)) != null) {
                tanks(INPUT_TANK).fill(fluidStackCopy, true)

                if(getStackInSlot(OUTPUT_SLOT) == null) {
                    setInventorySlotContents(OUTPUT_SLOT, FluidContainerRegistry.drainFluidContainer(getStackInSlot(INPUT_SLOT)))
                    setInventorySlotContents(INPUT_SLOT, null)
                }
            }
        }

        //Do burntime
        if(energyStorage.getEnergyStored < energyStorage.getMaxStored && burnTime <= 1) {
            if (tanks == null || tanks.size <= 0 || tanks(INPUT_TANK) == null) return false
            val fluidDrained = tanks(INPUT_TANK).drain(getMultiplierByCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY) * 10, true)
            if (fluidDrained == null || fluidDrained.getFluid == null || fluidDrained.amount <= 0)
                return false

            burnTime =
                    if(RecipeManager.getHandler[FluidFuelRecipeHandler](RecipeManager.FluidFuels).getOutput(fluidDrained.getFluid).isDefined)
                        RecipeManager.getHandler[FluidFuelRecipeHandler](RecipeManager.FluidFuels).getOutput(fluidDrained.getFluid).get / 10
                    else 0
            if (burnTime > 0) {
                currentObjectBurnTime = burnTime
                return true
            }
        }
        burnTime -= getMultiplierByCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY)
        burnTime > 0
    }

    /*******************************************************************************************************************
      **************************************************  Tile Methods  ************************************************
      ******************************************************************************************************************/

    /**
      * This will try to take things from other inventories and put it into ours
      */
    override def tryInput() : Unit = {
        for(dir <- EnumFacing.values) {
            if(canInputFromSide(dir)) {
                worldObj.getTileEntity(pos.offset(dir)) match {
                    case otherTank : IFluidHandler =>
                        if(otherTank.getTankInfo(dir.getOpposite) != null && otherTank.getTankInfo(dir.getOpposite).nonEmpty &&
                                otherTank.getTankInfo(dir.getOpposite)(0) != null && otherTank.getTankInfo(dir.getOpposite)(0).fluid != null &&
                                canFill(dir, otherTank.getTankInfo(dir.getOpposite)(0).fluid.getFluid)) {
                            val amount = fill(dir, otherTank.drain(dir.getOpposite, 1000, false), doFill = false)
                            if (amount > 0)
                                fill(dir, otherTank.drain(dir.getOpposite, amount, true), doFill = true)
                        }
                    case _ =>
                }
            }
        }
    }

    /**
      * This will try to take things from our inventory and try to place them in others
      */
    override def tryOutput(): Unit = { /* No Op, no output */ }

    override def writeToNBT(tag: NBTTagCompound): NBTTagCompound = {
        super[MachineGenerator].writeToNBT(tag)
        super[FluidHandler].writeToNBT(tag)
        tag
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[MachineGenerator].readFromNBT(tag)
        super[FluidHandler].readFromNBT(tag)
    }

    /*******************************************************************************************************************
      ********************************************* FluidHandler methods ***********************************************
      ******************************************************************************************************************/

    lazy val INPUT_TANK = 0

    /**
      * Used to set up the tanks needed. You can insert any number of tanks
      */
    def setupTanks() : Unit = {
        tanks += new FluidTank(bucketsToMB(10))
    }

    /**
      * Which tanks can input
      *
      * @return
      */
    def getInputTanks: Array[Int] = Array(INPUT_TANK)

    /**
      * Which tanks can output
      *
      * @return
      */
    def getOutputTanks : Array[Int] = Array(INPUT_TANK)

    /**
      * Called when something happens to the tank, you should mark the block for update here if a tile
      */
    override def onTankChanged(tank: FluidTank): Unit =
        worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 6)
    /**
      * Returns true if the given fluid can be inserted into the given direction.
      *
      * More formally, this should return true if fluid is able to enter from the given direction.
      */
    override def canFill(from: EnumFacing, fluid: Fluid): Boolean =
        !isDisabled(from) &&
                super.canFill(from, fluid) &&
                RecipeManager.getHandler[FluidFuelRecipeHandler](RecipeManager.FluidFuels).isValidInput(fluid)


    /**
      * Returns true if the given fluid can be extracted from the given direction.
      *
      * More formally, this should return true if fluid is able to leave from the given direction.
      */
    override def canDrain(from: EnumFacing, fluid: Fluid): Boolean = !isDisabled(from)

    /*******************************************************************************************************************
      ************************************************ Inventory methods ***********************************************
      ******************************************************************************************************************/

    /**
      * Used to get what slots are allowed to be input
      *
      * @return The slots to input from
      */
    override def getInputSlots(mode : EnumInputOutputMode) : Array[Int] = Array(INPUT_SLOT)

    /**
      * Used to get what slots are allowed to be output
      *
      * @return The slots to output from
      */
    override def getOutputSlots(mode : EnumInputOutputMode) : Array[Int] = Array(OUTPUT_SLOT)

    /**
      * Used to define if an item is valid for a slot
      *
      * @param slot The slot id
      * @param itemStackIn The stack to check
      * @return True if you can put this there
      */
    override def isItemValidForSlot(slot: Int, itemStackIn: ItemStack): Boolean = {
        val inputSlot = slot == INPUT_SLOT
        var isValidContainer = FluidContainerRegistry.isContainer(itemStackIn) && !FluidContainerRegistry.isEmptyContainer(itemStackIn)
        if(isValidContainer &&
                RecipeManager.getHandler[FluidFuelRecipeHandler](RecipeManager.FluidFuels).getOutput(FluidContainerRegistry.getFluidForFilledItem(itemStackIn).getFluid).isDefined &&
                RecipeManager.getHandler[FluidFuelRecipeHandler](RecipeManager.FluidFuels).getOutput(FluidContainerRegistry.getFluidForFilledItem(itemStackIn).getFluid).get <= 0)
            isValidContainer = false
        inputSlot && isValidContainer
    }

    /**
      * Returns true if automation can insert the given item in the given slot from the given side. Args: slot, item,
      * side
      */
    override def canInsertItem(slot: Int, itemStackIn: ItemStack, direction: EnumFacing): Boolean = {
        val inputSlot = slot == INPUT_SLOT
        var isValidContainer = FluidContainerRegistry.isContainer(itemStackIn) && !FluidContainerRegistry.isEmptyContainer(itemStackIn)
        if(isValidContainer &&
                RecipeManager.getHandler[FluidFuelRecipeHandler](RecipeManager.FluidFuels).getOutput(FluidContainerRegistry.getFluidForFilledItem(itemStackIn).getFluid).isDefined &&
                RecipeManager.getHandler[FluidFuelRecipeHandler](RecipeManager.FluidFuels).getOutput(FluidContainerRegistry.getFluidForFilledItem(itemStackIn).getFluid).get <= 0)
            isValidContainer = false
        inputSlot && isValidContainer
    }

    /**
      * Returns true if automation can extract the given item in the given slot from the given side. Args: slot, item,
      * side
      */
    override def canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean = index == OUTPUT_SLOT

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
        new ContainerFluidGenerator(player.inventory, this)

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
        new GuiFluidGenerator(player, this)

    override def getDescription : String = {
        "" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + ClientUtils.translate("neotech.text.stats") + ":\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.generating") + ":\n" +
                GuiColor.WHITE + "  " + getEnergyProduced + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.operations") + ":\n" +
                GuiColor.WHITE + "  " + getMultiplierByCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY) + "\n\n" +
                GuiColor.WHITE + I18n.translateToLocal("neotech.fluidGenerator.desc") + "\n\n" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + I18n.translateToLocal("neotech.text.upgrade") + ":\n" + GuiTextFormat.RESET +
                GuiColor.YELLOW + GuiTextFormat.BOLD + I18n.translateToLocal("neotech.text.processors") + ":\n" +
                GuiColor.WHITE + I18n.translateToLocal("neotech.furnaceGenerator.processorUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + I18n.translateToLocal("neotech.text.hardDrives") + ":\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + I18n.translateToLocal("neotech.text.memory") + ":\n" +
                GuiColor.WHITE + I18n.translateToLocal("neotech.electricFurnace.memoryUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + I18n.translateToLocal("neotech.text.psu") + ":\n" +
                GuiColor.WHITE + I18n.translateToLocal("neotech.electricFurnace.psuUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + I18n.translateToLocal("neotech.text.control") + ":\n" +
                GuiColor.WHITE + I18n.translateToLocal("neotech.electricFurnace.controlUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + I18n.translateToLocal("neotech.text.network") + ":\n" +
                GuiColor.WHITE +  I18n.translateToLocal("neotech.electricFurnace.networkUpgrade.desc")
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
    override def spawnActiveParticles(x: Double, y: Double, z: Double): Unit = {}
}
