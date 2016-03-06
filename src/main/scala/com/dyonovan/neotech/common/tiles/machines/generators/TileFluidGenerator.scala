package com.dyonovan.neotech.common.tiles.machines.generators

import com.dyonovan.neotech.client.gui.machines.generators.GuiFluidGenerator
import com.dyonovan.neotech.common.container.machines.generators.ContainerFluidGenerator
import com.dyonovan.neotech.common.tiles.MachineGenerator
import com.dyonovan.neotech.managers.RecipeManager
import com.dyonovan.neotech.registries.FluidFuelRecipeHandler
import com.dyonovan.neotech.utils.ClientUtils
import com.teambr.bookshelf.client.gui.{GuiColor, GuiTextFormat}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{EnumFacing, StatCollector}
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
class TileFluidGenerator extends MachineGenerator with IFluidHandler {

    final val BASE_ENERGY_TICK = 80
    final val INPUT_SLOT       = 0
    final val OUTPUT_SLOT      = 1

    val tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 10)

    /**
      * The initial size of the inventory
      *
      * @return
      */
    override def initialSize: Int = 2

    /**
      * This method handles how much energy to produce per tick
      *
      * @return How much energy to produce per tick
      */
    override def getEnergyProduced: Int = {
        if(getUpgradeBoard != null && getUpgradeBoard.getProcessorCount > 0)
            BASE_ENERGY_TICK + (getUpgradeBoard.getProcessorCount * 10)
        else
            BASE_ENERGY_TICK
    }

    /**
      * Called to tick generation. This is where you add power to the generator
      */
    override def generate(): Unit =
        energyStorage.receiveEnergy(getEnergyProduced, false)

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
            if(tank.getFluidAmount + FluidContainerRegistry.getFluidForFilledItem(getStackInSlot(INPUT_SLOT)).amount < tank.getCapacity &&
                    FluidContainerRegistry.drainFluidContainer(getStackInSlot(INPUT_SLOT)) != null) {
                tank.fill(fluidStackCopy, true)

                if(getStackInSlot(OUTPUT_SLOT) == null) {
                    setInventorySlotContents(OUTPUT_SLOT, FluidContainerRegistry.drainFluidContainer(getStackInSlot(INPUT_SLOT)))
                    setInventorySlotContents(INPUT_SLOT, null)
                }
            }
        }

        //Do burntime
        if(energyStorage.getEnergyStored < energyStorage.getMaxEnergyStored  && burnTime <= 1) {
            val fluidDrained = tank.drain(FluidContainerRegistry.BUCKET_VOLUME / 10, true)
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
        burnTime -= 1
        burnTime > 0
    }

    override def getDescription : String = {
        "" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + ClientUtils.translate("neotech.text.stats") + ":\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.generating") + ":\n" +
                GuiColor.WHITE + "  " + getEnergyProduced.toString + " \n\n" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + StatCollector.translateToLocal("neotech.text.upgrades") + ":\n" + GuiTextFormat.RESET +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.processors") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.furnaceGenerator.processorUpgrade.desc") + "\n\n" +
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

    /*******************************************************************************************************************
      **************************************************  Tile Methods  ************************************************
      ******************************************************************************************************************/

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

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super.writeToNBT(tag)
        tank.writeToNBT(tag)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super.readFromNBT(tag)
        tank.readFromNBT(tag)
    }

    /*******************************************************************************************************************
      ********************************************* FluidHandler methods ***********************************************
      ******************************************************************************************************************/

    override def drain(from: EnumFacing, resource: FluidStack, doDrain: Boolean): FluidStack = drain(from, resource, doDrain)

    override def drain(from: EnumFacing, maxDrain: Int, doDrain: Boolean): FluidStack = {
        val fluidAmount = tank.drain(maxDrain, false)
        if (fluidAmount != null && doDrain)
            tank.drain(maxDrain, true)
        worldObj.markBlockForUpdate(pos)

        fluidAmount
    }

    override def canFill(from: EnumFacing, fluid: Fluid): Boolean =
        (tank.getFluid == null || tank.getFluid.getFluid == fluid) && RecipeManager.getHandler[FluidFuelRecipeHandler](RecipeManager.FluidFuels).isValidInput(fluid)


    override def canDrain(from: EnumFacing, fluid: Fluid): Boolean = false

    override def fill(from: EnumFacing, resource: FluidStack, doFill: Boolean): Int = {
        if(resource == null)
            return 0
        if (canFill(from, resource.getFluid)) {
            if (tank.fill(resource, false) > 0) {
                val actual = tank.fill(resource, doFill)
                worldObj.markBlockForUpdate(pos)
                return actual
            }
        }
        0
    }

    override def getTankInfo(from: EnumFacing): Array[FluidTankInfo] = Array(tank.getInfo)

    /*******************************************************************************************************************
      ************************************************ Inventory methods ***********************************************
      ******************************************************************************************************************/

    /**
      * Used to get what slots are allowed to be input
      *
      * @return The slots to input from
      */
    override def getInputSlots: Array[Int] = Array(INPUT_SLOT)

    /**
      * Used to get what slots are allowed to be output
      *
      * @return The slots to output from
      */
    override def getOutputSlots: Array[Int] = Array(OUTPUT_SLOT)

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
      * Used to output the redstone single from this structure
      *
      * Use a range from 0 - 16.
      *
      * 0 Usually means that there is nothing in the tile, so take that for lowest level. Like the generator has no energy while
      * 16 is usually the flip side of that. Output 16 when it is totally full and not less
      *
      * @return int range 0 - 16
      */
    override def getRedstoneOutput: Int = (energyStorage.getEnergyStored * 16) / energyStorage.getMaxEnergyStored

    /**
      * Used to get what particles to spawn. This will be called when the tile is active
      */
    override def spawnActiveParticles(x: Double, y: Double, z: Double): Unit = {}
}
