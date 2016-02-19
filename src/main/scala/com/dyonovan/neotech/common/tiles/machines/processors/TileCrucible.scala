package com.dyonovan.neotech.common.tiles.machines.processors

import com.dyonovan.neotech.client.gui.machines.processors.GuiCrucible
import com.dyonovan.neotech.common.container.machines.processors.ContainerCrucible
import com.dyonovan.neotech.common.tiles.MachineProcessor
import com.dyonovan.neotech.managers.MetalManager
import com.dyonovan.neotech.registries.CrucibleRecipeRegistry
import com.teambr.bookshelf.client.gui.{GuiColor, GuiTextFormat}
import com.teambr.bookshelf.common.tiles.traits.FluidHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{EnumParticleTypes, EnumFacing, StatCollector}
import net.minecraft.world.World
import net.minecraftforge.fluids.{FluidStack, FluidTank, IFluidHandler}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/17/2016
  */
class TileCrucible extends MachineProcessor[ItemStack, FluidStack] with FluidHandler {

    lazy val ITEM_INPUT_SLOT   = 0
    lazy val OUTPUT_TANK       = 0

    val BASE_ENERGY_TICK = 100

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
        if(energyStorage.getEnergyStored >= getEnergyCostPerTick) {
            if(getStackInSlot(ITEM_INPUT_SLOT) == null)
                return false
            else {
                if(getOutput(getStackInSlot(ITEM_INPUT_SLOT)) != null) {
                    val recipeOutput = getOutput(getStackInSlot(0))
                    if(recipeOutput.getFluid != null) {
                        if(tanks(OUTPUT_TANK).getFluid == null && recipeOutput.amount <= tanks(OUTPUT_TANK).getCapacity)
                            return true
                        else if((tanks(OUTPUT_TANK).getFluid.amount + recipeOutput.amount) <= tanks(OUTPUT_TANK).getCapacity)
                            return true
                        return false
                    }
                    return false
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
    override def getOutput(stack: ItemStack): FluidStack = {
        if(CrucibleRecipeRegistry.getOutput(stack).isDefined)
            CrucibleRecipeRegistry.getOutput(stack).get
        else
            null
    }

    override def getOutputForStack(stack : ItemStack) : ItemStack = null

    /**
      * Used to actually cook the item
      */
    override def cook(): Unit = cookTime += 1

    /**
      * Called when the tile has completed the cook process
      */
    override def completeCook(): Unit = {
        val recipeOutput = getOutput(getStackInSlot(0))
        if(recipeOutput != null) {
            val drain = tanks(OUTPUT_TANK).fill(recipeOutput, false)
            if(drain == recipeOutput.amount) {
                tanks(OUTPUT_TANK).fill(recipeOutput, true)
                getStackInSlot(ITEM_INPUT_SLOT).stackSize -= 1
                if(getStackInSlot(ITEM_INPUT_SLOT).stackSize <= 0)
                    setStackInSlot(ITEM_INPUT_SLOT, null)
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
    override def getInputSlots: Array[Int] = Array(ITEM_INPUT_SLOT)

    /**
      * Used to get what slots are allowed to be output
      *
      * @return The slots to output from
      */
    override def getOutputSlots: Array[Int] = Array()

    override def getInputTanks: Array[Int] = Array()

    override def getOutputTanks: Array[Int] = Array(OUTPUT_TANK)

    override def writeToNBT(tag : NBTTagCompound) : Unit = {
        super[MachineProcessor].writeToNBT(tag)
        super[FluidHandler].writeToNBT(tag)
    }

    override def readFromNBT(tag : NBTTagCompound) : Unit = {
        super[MachineProcessor].readFromNBT(tag)
        super[FluidHandler].readFromNBT(tag)
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
                        if (tanks(OUTPUT_TANK).getFluid != null)
                            otherTank.getTankInfo(dir.getOpposite)(0).fluid.getFluid == tanks(OUTPUT_TANK).getFluid.getFluid
                        else false)
                                && canDrain(dir.getOpposite, if(fluid != null) fluid.getFluid else null))
                            otherTank.fill(dir.getOpposite, drain(dir, 1000, doDrain = true), true)
                    case _ =>
                }
            }
        }
    }

    override def getDescription : String = {
        GuiColor.YELLOW + "" + GuiTextFormat.BOLD + StatCollector.translateToLocal("tile.neotech:electricCrucible.name") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.electricCrucible.desc") + "\n\n" +
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
        new ContainerCrucible(player.inventory, this)

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
        new GuiCrucible(player, this)

    /**
      * Returns true if automation can insert the given item in the given slot from the given side. Args: slot, item,
      * side
      */
    override def canInsertItem(slot: Int, itemStackIn: ItemStack, direction: EnumFacing): Boolean = {
        if (slot == ITEM_INPUT_SLOT) {
            if (getStackInSlot(ITEM_INPUT_SLOT) == null) return true
            if (getStackInSlot(ITEM_INPUT_SLOT).isItemEqual(itemStackIn)) {
                if (getStackInSlot(ITEM_INPUT_SLOT).getMaxStackSize >= getStackInSlot(ITEM_INPUT_SLOT).stackSize + itemStackIn.stackSize)
                    return true
            }
        }
        false
    }

    /**
      * Returns true if automation can extract the given item in the given slot from the given side. Args: slot, item,
      * side
      */
    override def canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean = false

    /**
      * Used to define if an item is valid for a slot
      *
      * @param slot The slot id
      * @param itemStackIn The stack to check
      * @return True if you can put this there
      */
    override def isItemValidForSlot(slot: Int, itemStackIn: ItemStack): Boolean =
        slot == ITEM_INPUT_SLOT && getOutput(itemStackIn) != null

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
}
