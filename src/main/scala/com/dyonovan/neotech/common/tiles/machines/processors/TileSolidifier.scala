package com.dyonovan.neotech.common.tiles.machines.processors

import com.dyonovan.neotech.client.gui.machines.processors.GuiSolidifier
import com.dyonovan.neotech.common.container.machines.processors.ContainerSolidifier
import com.dyonovan.neotech.common.tiles.MachineProcessor
import com.dyonovan.neotech.managers.{RecipeManager, MetalManager}
import com.dyonovan.neotech.registries.SolidifierRegistry
import com.teambr.bookshelf.client.gui.{GuiColor, GuiTextFormat}
import com.teambr.bookshelf.common.tiles.traits.FluidHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{EnumFacing, EnumParticleTypes, StatCollector}
import net.minecraft.world.World
import net.minecraftforge.fluids.{Fluid, FluidStack, FluidTank, IFluidHandler}

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
    lazy val INPUT_TANK       = 0

    val BASE_ENERGY_TICK      = 100

    lazy val UPDATE_MODE      = 4

    var currentMode : SOLIDIFY_MODE = BLOCK_MODE

    /**
      * The initial size of the inventory
      *
      * @return
      */
    override def initialSize: Int = 1


    override def setupTanks(): Unit = {
        tanks += new FluidTank(MetalManager.BLOCK_MB * 10)
    }

    override def onTankChanged(tank: FluidTank): Unit = worldObj.markBlockForUpdate(pos)

    /**
      * Used to get how long it takes to cook things, you should check for upgrades at this point
      *
      * @return The time it takes in ticks to cook the current item
      */
    override def getCookTime : Int = {
        if(getUpgradeBoard != null && getUpgradeBoard.getProcessorCount > 0)
            1000 - (getUpgradeBoard.getProcessorCount * 112)
        else
            1000
    }

    /**
      * Used to tell if this tile is able to process
      *
      * @return True if you are able to process
      */
    override def canProcess: Boolean = {
        if(tanks(INPUT_TANK).getFluid != null) {
            val requiredMB = getRequiredMB(currentMode)
            if(getStackInSlot(OUTPUT_SLOT) == null) {
                if (tanks(INPUT_TANK).getFluidAmount >= requiredMB)
                    return true
            } else {
                val output = RecipeManager.getHandler[SolidifierRegistry](RecipeManager.Solidifier).getOutput(new FluidStack(tanks(INPUT_TANK).getFluid, getRequiredMB(currentMode)))
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
        false
    }

    /**
      * Get the output of the recipe
      *
      * @param stack The input
      * @return The output
      */
    override def getOutput(stack: FluidStack): ItemStack = {
        if(RecipeManager.getHandler[SolidifierRegistry](RecipeManager.Solidifier).getOutput(stack).isDefined)
            RecipeManager.getHandler[SolidifierRegistry](RecipeManager.Solidifier).getOutput(stack).get
        else
            null
    }

    /**
      * Used to actually cook the item
      */
    override def cook(): Unit = cookTime += 1

    /**
      * Called when the tile has completed the cook process
      */
    override def completeCook(): Unit = {
        if (tanks(INPUT_TANK).getFluid != null) {
            val amount = drain(EnumFacing.UP, getRequiredMB(currentMode), doDrain = false)
            val output = RecipeManager.getHandler[SolidifierRegistry](RecipeManager.Solidifier).getOutput(new FluidStack(tanks(INPUT_TANK).getFluid, getRequiredMB(currentMode)))
            // In case the user changed while going and there now isn't enough, fail
            if(amount != null && amount.amount == getRequiredMB(currentMode) &&
                    output.isDefined &&
                    (if(getStackInSlot(OUTPUT_SLOT) != null)
                        getStackInSlot(OUTPUT_SLOT).stackSize + output.get.stackSize <= getStackInSlot(OUTPUT_SLOT).getMaxStackSize
                    else true)) {
                //Drain the tank
                drain(EnumFacing.UP, getRequiredMB(currentMode), doDrain = true)
                if(getStackInSlot(OUTPUT_SLOT) != null)
                    getStackInSlot(OUTPUT_SLOT).stackSize += output.get.stackSize
                else
                    setStackInSlot(OUTPUT_SLOT, output.get)
            }
        }
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
      * Used to get what slots are allowed to be input
      *
      * @return The slots to input from
      */
    override def getInputSlots: Array[Int] = Array()

    /**
      * Used to get what slots are allowed to be output
      *
      * @return The slots to output from
      */
    override def getOutputSlots: Array[Int] = Array(OUTPUT_SLOT)


    override def getInputTanks: Array[Int] = Array(INPUT_TANK)

    override def getOutputTanks: Array[Int] = Array(INPUT_TANK)

    /**
      * Returns true if automation can extract the given item in the given slot from the given side. Args: slot, item,
      * side
      */
    override def canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean = index == OUTPUT_SLOT

    override def writeToNBT(tag : NBTTagCompound) : Unit = {
        super[MachineProcessor].writeToNBT(tag)
        super[FluidHandler].writeToNBT(tag)
        tag.setInteger("ProcessMode", processModeToInt(currentMode))
    }

    override def readFromNBT(tag : NBTTagCompound) : Unit = {
        super[MachineProcessor].readFromNBT(tag)
        super[FluidHandler].readFromNBT(tag)
        currentMode = processModeFromInt(tag.getInteger("ProcessMode"))
    }

    /**
      * Returns true if the given fluid can be inserted into the given direction.
      *
      * More formally, this should return true if fluid is able to enter from the given direction.
      */
    override def canFill(from: EnumFacing, fluid: Fluid): Boolean = {
        if(fluid == null) return false
        if(tanks(INPUT_TANK).getFluid == null)
            return RecipeManager.getHandler[SolidifierRegistry](RecipeManager.Solidifier).isValidInput(new FluidStack(fluid, 1000))
        else {
            if(fluid == tanks(INPUT_TANK).getFluid.getFluid)
                return true
            else
                return false
        }
        false
    }

    /**
      * This will try to take things from other inventories and put it into ours
      */
    override def tryInput() : Unit = {
        super.tryInput()
        for(dir <- EnumFacing.values) {
            if(canInputFromSide(dir)) {
                worldObj.getTileEntity(pos.offset(dir)) match {
                    case otherTank : IFluidHandler =>
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
    }

    override def getDescription : String = {
        GuiColor.YELLOW + "" + GuiTextFormat.BOLD + StatCollector.translateToLocal("tile.neotech:electricSolidifier.name") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.electricSolidifier.desc") + "\n\n" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + StatCollector.translateToLocal("neotech.text.upgrades") + ":\n" + GuiTextFormat.RESET +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.processors") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.electricCrucible.processorUpgrade.desc") + "\n\n" +
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

    override def getOutputForStack(stack : ItemStack) : ItemStack = null

    sealed trait SOLIDIFY_MODE { def name : String }
    case object BLOCK_MODE  extends SOLIDIFY_MODE { val name = "BLOCK_MODE" }
    case object INGOT_MODE  extends SOLIDIFY_MODE { val name = "INGOT_MODE" }
    case object NUGGET_MODE extends SOLIDIFY_MODE { val name = "NUGGET_MODE" }

    def processModeToInt(mode : SOLIDIFY_MODE) : Int = {
        mode match {
            case BLOCK_MODE  => 0
            case INGOT_MODE  => 1
            case NUGGET_MODE => 2
            case _ => -1
        }
    }

    def processModeFromInt(value : Int) : SOLIDIFY_MODE = {
        value match {
            case 0 => BLOCK_MODE
            case 1 => INGOT_MODE
            case 2 => NUGGET_MODE
            case _ => BLOCK_MODE
        }
    }

    def getDisplayNameForProcessMode(mode : SOLIDIFY_MODE) : String = {
        mode match {
            case BLOCK_MODE  => StatCollector.translateToLocal("neotech.text.blockMode")
            case INGOT_MODE  => StatCollector.translateToLocal("neotech.text.ingotMode")
            case NUGGET_MODE => StatCollector.translateToLocal("neotech.text.nuggetMode")
            case _ => "ERROR"
        }
    }

    def getDisplayStackForProcessMode(mode : SOLIDIFY_MODE) : ItemStack = {
        mode match {
            case BLOCK_MODE  => new ItemStack(Blocks.iron_block)
            case INGOT_MODE  => new ItemStack(Items.iron_ingot)
            case NUGGET_MODE => new ItemStack(MetalManager.getMetal("iron").get.nugget.get)
            case _ => new ItemStack(Blocks.stone)
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
