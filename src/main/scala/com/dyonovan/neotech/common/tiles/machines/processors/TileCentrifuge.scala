package com.dyonovan.neotech.common.tiles.machines.processors

import com.dyonovan.neotech.client.gui.machines.processors.GuiCentrifuge
import com.dyonovan.neotech.common.container.machines.processors.ContainerCentrifuge
import com.dyonovan.neotech.common.tiles.MachineProcessor
import com.dyonovan.neotech.managers.{MetalManager, RecipeManager}
import com.dyonovan.neotech.registries.CentrifugeRecipeHandler
import com.teambr.bookshelf.client.gui.{GuiTextFormat, GuiColor}
import com.teambr.bookshelf.common.tiles.traits.FluidHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{EnumParticleTypes, StatCollector, EnumFacing}
import net.minecraft.world.World
import net.minecraftforge.fluids.{IFluidHandler, Fluid, FluidStack, FluidTank}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/21/2016
  */
class TileCentrifuge extends MachineProcessor[FluidStack, (FluidStack, FluidStack)] with FluidHandler {

    lazy val INPUT_TANK = 0
    lazy val OUTPUT_TANK_1 = 1
    lazy val OUTPUT_TANK_2  = 2

    lazy val BASE_ENERGY_TICK = 100

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
        if (energyStorage.getEnergyStored >= getEnergyCostPerTick && tanks(INPUT_TANK).getFluid != null) {
            val recipeTest = RecipeManager.getHandler[CentrifugeRecipeHandler](RecipeManager.Centrifuge).getOutput(tanks(INPUT_TANK).getFluid)
            if(recipeTest.isDefined) { // Not worth checking if not valid input
            val out1Empty = tanks(OUTPUT_TANK_1).getFluid == null
                val out2Empty = tanks(OUTPUT_TANK_2).getFluid == null
                if (out1Empty && out2Empty) //Nothing to check
                    return true
                else if (!out1Empty && !out2Empty) { //Must have something in both, can't handle single
                val recipe = recipeTest.get
                    val fluidOne = recipe._1
                    val fluidTwo = recipe._2
                    if(fluidOne.getFluid != null && fluidTwo.getFluid != null) { // Check name and amount for each or swap
                        return (fluidOne.getFluid.getName.equalsIgnoreCase(tanks(OUTPUT_TANK_1).getFluid.getFluid.getName) &&
                                fluidTwo.getFluid.getName.equalsIgnoreCase(tanks(OUTPUT_TANK_2).getFluid.getFluid.getName) &&
                                fluidOne.amount + tanks(OUTPUT_TANK_1).getFluidAmount <= tanks(OUTPUT_TANK_1).getCapacity &&
                                fluidTwo.amount + tanks(OUTPUT_TANK_2).getFluidAmount <= tanks(OUTPUT_TANK_2).getCapacity) ||
                                (fluidOne.getFluid.getName.equalsIgnoreCase(tanks(OUTPUT_TANK_2).getFluid.getFluid.getName) &&
                                        fluidTwo.getFluid.getName.equalsIgnoreCase(tanks(OUTPUT_TANK_1).getFluid.getFluid.getName) &&
                                        fluidOne.amount + tanks(OUTPUT_TANK_2).getFluidAmount <= tanks(OUTPUT_TANK_2).getCapacity &&
                                        fluidTwo.amount + tanks(OUTPUT_TANK_1).getFluidAmount <= tanks(OUTPUT_TANK_1).getCapacity)
                    }
                }
            }
        }
        false
    }
    /**
      * Get the output of the recipe
      *
      * @param input The input
      * @return The output
      */
    override def getOutput(input: FluidStack): (FluidStack, FluidStack) =
        if(RecipeManager.getHandler[CentrifugeRecipeHandler](RecipeManager.Centrifuge).getOutput(input).isDefined)
            RecipeManager.getHandler[CentrifugeRecipeHandler](RecipeManager.Centrifuge).getOutput(input).get
        else
            null

    /**
      * Get the output of the recipe (used in insert options)
      *
      * @param input The input
      * @return The output
      */
    override def getOutputForStack(input: ItemStack): ItemStack = null

    /**
      * Used to actually cook the item
      */
    override def cook(): Unit = cookTime += 1

    /**
      * Called when the tile has completed the cook process
      */
    override def completeCook(): Unit = {
        if(canProcess) { //Just to be safe
        val recipeTest = RecipeManager.getHandler[CentrifugeRecipeHandler](RecipeManager.Centrifuge).getRecipe(tanks(INPUT_TANK).getFluid)
            if(recipeTest.isDefined) {
                val recipe = recipeTest.get
                val drained = tanks(INPUT_TANK).drain(recipe.getFluidFromString(recipe.fluidIn).amount, false)
                if(drained != null && drained.amount > 0) {
                    tanks(INPUT_TANK).drain(drained.amount, true)
                    tanks(OUTPUT_TANK_1).fill(recipe.getFluidFromString(recipe.fluidOne), true)
                    tanks(OUTPUT_TANK_2).fill(recipe.getFluidFromString(recipe.fluidTwo), true)
                }
            }
        }
    }

    /**
      * Used to get how much energy to drain per tick, you should check for upgrades at this point
      *
      * @return How much energy to drain per tick
      */
    override def getEnergyCostPerTick: Int =
        if(getUpgradeBoard != null && getUpgradeBoard.getProcessorCount > 0)
            BASE_ENERGY_TICK * getUpgradeBoard.getProcessorCount
        else
            BASE_ENERGY_TICK

    /**
      * The initial size of the inventory
      *
      * @return
      */
    override def initialSize: Int = 0

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
    override def getOutputSlots: Array[Int] = Array()

    override def writeToNBT(tag : NBTTagCompound) : Unit = {
        super[MachineProcessor].writeToNBT(tag)
        super[FluidHandler].writeToNBT(tag)
    }

    override def readFromNBT(tag : NBTTagCompound) : Unit = {
        super[MachineProcessor].readFromNBT(tag)
        super[FluidHandler].readFromNBT(tag)
    }

    /*******************************************************************************************************************
      **************************************************** Fluid methods ***********************************************
      ******************************************************************************************************************/

    override def setupTanks(): Unit = {
        tanks += new FluidTank(10 * MetalManager.BLOCK_MB) // IN
        tanks += new FluidTank(10 * MetalManager.BLOCK_MB) // OUT 1
        tanks += new FluidTank(10 * MetalManager.BLOCK_MB) // OUT 2
    }

    override def onTankChanged(tank: FluidTank): Unit = worldObj.markBlockForUpdate(pos)

    override def getOutputTanks: Array[Int] = Array(OUTPUT_TANK_1, OUTPUT_TANK_2)

    override def getInputTanks: Array[Int] = Array(INPUT_TANK)

    /**
      * Returns true if the given fluid can be inserted into the given direction.
      *
      * More formally, this should return true if fluid is able to enter from the given direction.
      */
    override def canFill(from: EnumFacing, fluid: Fluid): Boolean = {
        if(fluid == null) return false
        if(tanks(INPUT_TANK).getFluid == null)
            return RecipeManager.getHandler[CentrifugeRecipeHandler](RecipeManager.Centrifuge).isValidInput(new FluidStack(fluid, 1000))
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

    /**
      * This will try to take things from other inventories and put it into ours
      */
    override def tryOutput() : Unit = {
        super.tryOutput()
        for(dir <- EnumFacing.values) {
            if(canOutputFromSide(dir)) {
                worldObj.getTileEntity(pos.offset(dir)) match {
                    case otherTank : IFluidHandler =>
                        val fluid = otherTank.getTankInfo(dir.getOpposite)(0).fluid

                        if((if(fluid == null) true else
                        if (tanks(OUTPUT_TANK_1).getFluid != null)
                            otherTank.getTankInfo(dir.getOpposite)(0).fluid.getFluid == tanks(OUTPUT_TANK_1).getFluid.getFluid
                        else false)
                                && canDrain(dir.getOpposite, if(fluid != null) fluid.getFluid else null)) {
                            val amount1 = otherTank.fill(dir.getOpposite, tanks(OUTPUT_TANK_1).drain(1000, false), false)
                            if (amount1 > 0)
                                otherTank.fill(dir, tanks(OUTPUT_TANK_1).drain(amount1, true),  true)
                        }

                        if((if(fluid == null) true else
                        if (tanks(OUTPUT_TANK_2).getFluid != null)
                            otherTank.getTankInfo(dir.getOpposite)(0).fluid.getFluid == tanks(OUTPUT_TANK_2).getFluid.getFluid
                        else false)
                                && canDrain(dir.getOpposite, if(fluid != null) fluid.getFluid else null)) {
                            val amount = otherTank.fill(dir.getOpposite, tanks(OUTPUT_TANK_2).drain(1000, false), false)
                            if (amount > 0)
                                otherTank.fill(dir, tanks(OUTPUT_TANK_2).drain(amount, true),  true)
                        }
                    case _ =>
                }
            }
        }
    }

    /*******************************************************************************************************************
      ***************************************************** Misc methods ***********************************************
      ******************************************************************************************************************/

    override def getDescription : String = {
        GuiColor.YELLOW + "" + GuiTextFormat.BOLD + StatCollector.translateToLocal("tile.neotech:centrifuge.name") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.centrifuge.desc") + "\n\n" +
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
        new ContainerCentrifuge(player.inventory, this)

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
        new GuiCentrifuge(player, this)

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
}
