package com.teambrmodding.neotech.common.tiles.machines.processors

import java.util

import com.teambrmodding.neotech.client.gui.machines.processors.GuiSolidifier
import com.teambrmodding.neotech.collections.EnumInputOutputMode
import com.teambrmodding.neotech.common.container.machines.processors.ContainerSolidifier
import com.teambrmodding.neotech.common.tiles.MachineProcessor
import com.teambrmodding.neotech.managers.{MetalManager, RecipeManager}
import com.teambrmodding.neotech.registries.SolidifierRecipeHandler
import com.teambr.bookshelf.client.gui.{GuiColor, GuiTextFormat}
import com.teambr.bookshelf.common.tiles.traits.FluidHandler
import com.teambr.bookshelf.util.InventoryUtils
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.text.translation.I18n
import net.minecraft.util.{EnumFacing, EnumParticleTypes}
import net.minecraft.world.World
import net.minecraftforge.fluids.capability.{CapabilityFluidHandler, IFluidHandler}
import net.minecraftforge.fluids.{Fluid, FluidStack, FluidTank}

import scala.util.control.Breaks._


/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/18/2016
  */
class TileSolidifier extends MachineProcessor[FluidStack, ItemStack] with FluidHandler {

    lazy val OUTPUT_SLOT      = 0

    val BASE_ENERGY_TICK      = 300
    lazy val UPDATE_MODE      = 4

    var currentMode : SOLIDIFY_MODE = BLOCK_MODE

    /**
      * The initial size of the inventory
      *
      * @return
      */
    override def initialSize: Int = 1

    /**
      * Add all modes you want, in order, here
      */
    def addValidModes() : Unit = {
        validModes += EnumInputOutputMode.INPUT_ALL
        validModes += EnumInputOutputMode.OUTPUT_ALL
        validModes += EnumInputOutputMode.ALL_MODES
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
      * Used to get how much energy to drain per tick, you should check for upgrades at this point
      *
      * @return How much energy to drain per tick
      */
    override def getEnergyCostPerTick: Int =
    BASE_ENERGY_TICK * getMultiplierByCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY) +
            ((getMultiplierByCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU) - 1) * 62)

    /**
      * Used to get how long it takes to cook things, you should check for upgrades at this point
      *
      * @return The time it takes in ticks to cook the current item
      */
    override def getCookTime : Int = {
        1000 - (62 * getMultiplierByCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU))
    }

    /**
      * Used to tell if this tile is able to process
      *
      * @return True if you are able to process
      */
    override def canProcess: Boolean = {
        if(energyStorage.getEnergyStored > 0 && tanks(INPUT_TANK).getFluid != null) {
            val requiredMB = getRequiredMB(currentMode)
            if(getStackInSlot(OUTPUT_SLOT) == null) {
                if (tanks(INPUT_TANK).getFluidAmount >= requiredMB)
                    return true
            } else {
                val output = RecipeManager.getHandler[SolidifierRecipeHandler](RecipeManager.Solidifier).getOutput(new FluidStack(tanks(INPUT_TANK).getFluid, getRequiredMB(currentMode)))
                if(output.isDefined && tanks(INPUT_TANK).getFluidAmount >= requiredMB) {
                    val stackOut = output.get
                    val ourStack = getStackInSlot(OUTPUT_SLOT)
                    if(stackOut.getItem == ourStack.getItem && stackOut.getItemDamage == ourStack.getItemDamage &&
                            ourStack.stackSize + stackOut.stackSize <= ourStack.getMaxStackSize)
                        return true
                }
                return false
            }
        }
        failCoolDown = 40
        false
    }

    /**
      * Used to actually cook the item
      */
    override def cook(): Unit = cookTime += 1

    /**
      * Called when the tile has completed the cook process
      */
    override def completeCook(): Unit = {
        breakable {
            for (x <- 0 until getModifierForCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY)) {
                if (canProcess) {
                    if (tanks(INPUT_TANK).getFluid != null) {
                        val amount = drain(EnumFacing.UP, getRequiredMB(currentMode), doDrain = false)
                        val output = RecipeManager.getHandler[SolidifierRecipeHandler](RecipeManager.Solidifier).getOutput(new FluidStack(tanks(INPUT_TANK).getFluid, getRequiredMB(currentMode)))
                        // In case the user changed while going and there now isn't enough, fail
                        if (amount != null && amount.amount == getRequiredMB(currentMode) &&
                                output.isDefined &&
                                (if (getStackInSlot(OUTPUT_SLOT) != null)
                                    getStackInSlot(OUTPUT_SLOT).stackSize + output.get.stackSize <= getStackInSlot(OUTPUT_SLOT).getMaxStackSize
                                else true)) {
                            //Drain the tank
                            drain(EnumFacing.UP, getRequiredMB(currentMode), doDrain = true)
                            if (getStackInSlot(OUTPUT_SLOT) != null)
                                getStackInSlot(OUTPUT_SLOT).stackSize += output.get.stackSize
                            else
                                setStackInSlot(OUTPUT_SLOT, output.get)
                        }
                    }
                } else break
            }
        }

        markForUpdate(6)
    }

    /**
      * Get the output of the recipe
      *
      * @param stack The input
      * @return The output
      */
    override def getOutput(stack: FluidStack): ItemStack = {
        if(RecipeManager.getHandler[SolidifierRecipeHandler](RecipeManager.Solidifier).getOutput(stack).isDefined)
            RecipeManager.getHandler[SolidifierRecipeHandler](RecipeManager.Solidifier).getOutput(stack).get
        else
            null
    }

    override def getOutputForStack(stack : ItemStack) : ItemStack = null

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
                    case tile : TileEntity if tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite) =>
                        val otherTank = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite)

                        // If we have something, try to match and fill
                        if(tanks(INPUT_TANK).getFluid != null && otherTank.drain(tanks(INPUT_TANK).getFluid, false) != null) {
                            val amount = fill(otherTank.drain(tanks(INPUT_TANK).getFluid, false), doFill = false)
                            if(amount > 0)
                                fill(otherTank.drain(tanks(INPUT_TANK).getFluid, true), doFill = true)
                        }
                        // Check our tank, if we can take fluid, do so
                        else if(tanks(INPUT_TANK).getFluid == null && otherTank.drain(1000, false) != null) { // If we are empty, and they are not
                        val amount = fill(otherTank.drain(1000, false), doFill = false)
                            if(amount > 0)
                                fill(otherTank.drain(amount, true), doFill = true)
                        }
                    case _ =>
                }
            }
        }
    }

    /**
      * This will try to take things from our inventory and try to place them in others
      */
    override def tryOutput(): Unit = {
        for(dir <- EnumFacing.values()) {
            if(canOutputFromSide(dir))
                InventoryUtils.moveItemInto(this, OUTPUT_SLOT, worldObj.getTileEntity(pos.offset(dir)), -1, 64,
                    dir.getOpposite, doMove = true, checkSidedSource = false)
        }
    }

    override def writeToNBT(tag : NBTTagCompound) : NBTTagCompound = {
        super[MachineProcessor].writeToNBT(tag)
        super[FluidHandler].writeToNBT(tag)
        tag.setInteger("ProcessMode", processModeToInt(currentMode))
        tag
    }

    override def readFromNBT(tag : NBTTagCompound) : Unit = {
        super[MachineProcessor].readFromNBT(tag)
        super[FluidHandler].readFromNBT(tag)
        currentMode = processModeFromInt(tag.getInteger("ProcessMode"))
    }

    /*******************************************************************************************************************
      ************************************************ Inventory methods ***********************************************
      ******************************************************************************************************************/

    /**
      * Used to get what slots are allowed to be input
      *
      * @return The slots to input from
      */
    override def getInputSlots(mode : EnumInputOutputMode) : Array[Int] = Array()

    /**
      * Used to get what slots are allowed to be output
      *
      * @return The slots to output from
      */
    override def getOutputSlots(mode : EnumInputOutputMode) : Array[Int] = Array(OUTPUT_SLOT)

    /**
      * Returns true if automation can extract the given item in the given slot from the given side. Args: slot, item,
      * side
      */
    override def canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean = index == OUTPUT_SLOT

    /*******************************************************************************************************************
      **************************************************** Fluid methods ***********************************************
      ******************************************************************************************************************/

    lazy val INPUT_TANK       = 0

    /**
      * Used to set up the tanks needed. You can insert any number of tanks
      */
    override def setupTanks(): Unit = {
        tanks += new FluidTank(MetalManager.BLOCK_MB * 10)
    }

    /**
      * Which tanks can input
      *
      * @return
      */
    override def getInputTanks: Array[Int] = Array(INPUT_TANK)

    /**
      * Which tanks can output
      *
      * @return
      */
    override def getOutputTanks: Array[Int] = Array(INPUT_TANK)

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
    override def canFill(from: EnumFacing, fluid: Fluid): Boolean = {
        if(fluid == null) return false
        if(isDisabled(from)) return false
        if(tanks(INPUT_TANK).getFluid == null)
            return RecipeManager.getHandler[SolidifierRecipeHandler](RecipeManager.Solidifier).isValidInput(new FluidStack(fluid, 1000))
        else {
            if(fluid == tanks(INPUT_TANK).getFluid.getFluid)
                return true
            else
                return false
        }
        false
    }

    /*******************************************************************************************************************
      ***************************************************** Misc methods ***********************************************
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
        new ContainerSolidifier(player.inventory, this)

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
        new GuiSolidifier(player, this)

    override def getDescription : String = {
        "" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + ClientUtils.translate("neotech.text.stats") + ":\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.energyUsage") + ":\n" +
                GuiColor.WHITE + "  " + ClientUtils.formatNumber(getEnergyCostPerTick) + " RF/tick\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.processTime") + ":\n" +
                GuiColor.WHITE + "  " + getCookTime + " ticks\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.operations") + ":\n" +
                GuiColor.WHITE + "  " + getMultiplierByCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY) + "\n\n" +
                GuiColor.WHITE + I18n.translateToLocal("neotech.electricSolidifier.desc") + "\n\n" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + I18n.translateToLocal("neotech.text.upgrade") + ":\n" + GuiTextFormat.RESET +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.processors") + ":\n" +
                GuiColor.WHITE + I18n.translateToLocal("neotech.electricFurnace.processorUpgrade.desc") + "\n\n" +
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
    override def getRedstoneOutput: Int = if(isActive) 16 else 0

    /**
      * Used to get what particles to spawn. This will be called when the tile is active
      */
    override def spawnActiveParticles(x: Double, y: Double, z: Double): Unit = {
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0, 0)
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0, 0)
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0, 0)
    }

    /**
      * Used to set the variable for this tile, the Syncable will use this when you send a value to the server
      *
      * @param id The ID of the variable to send
      * @param value The new value to set to (you can use this however you want, eg using the ordinal of EnumFacing)
      */
    override def setVariable(id : Int, value : Double): Unit = {
        if(id == UPDATE_MODE)
            currentMode = processModeFromInt(value.toInt)
        super.setVariable(id, value)
    }

    sealed trait SOLIDIFY_MODE { def name : String }
    case object BLOCK_MODE  extends SOLIDIFY_MODE { val name = "BLOCK_MODE" }
    case object INGOT_MODE  extends SOLIDIFY_MODE { val name = "INGOT_MODE" }
    case object NUGGET_MODE extends SOLIDIFY_MODE { val name = "NUGGET_MODE" }



    def getDisplayStackForProcessMode(mode : SOLIDIFY_MODE) : ItemStack = {
        mode match {
            case BLOCK_MODE  => new ItemStack(Blocks.IRON_BLOCK)
            case INGOT_MODE  => new ItemStack(Items.IRON_INGOT)
            case NUGGET_MODE => new ItemStack(MetalManager.getMetal("iron").get.nugget.get)
            case _ => new ItemStack(Blocks.STONE)
        }
    }

    def getRequiredMB(mode : SOLIDIFY_MODE) : Int = {
        mode match {
            case BLOCK_MODE  => MetalManager.BLOCK_MB
            case INGOT_MODE  => MetalManager.INGOT_MB
            case NUGGET_MODE => MetalManager.NUGGET_MB
            case _ => 0
        }
    }

    def toggleProcessMode() : Unit = {
        currentMode match {
            case BLOCK_MODE  => currentMode = INGOT_MODE
            case INGOT_MODE  => currentMode = NUGGET_MODE
            case NUGGET_MODE => currentMode = BLOCK_MODE
            case _ =>
        }
    }
}
