package com.dyonovan.neotech.common.tiles.machines.processors

import com.dyonovan.neotech.client.gui.machines.processors.GuiElectricCrusher
import com.dyonovan.neotech.common.container.machines.processors.ContainerElectricCrusher
import com.dyonovan.neotech.common.tiles.MachineProcessor
import com.dyonovan.neotech.registries.CrusherRecipeRegistry
import com.teambr.bookshelf.client.gui.{GuiTextFormat, GuiColor}
import com.teambr.bookshelf.util.InventoryUtils
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.{StatCollector, EnumFacing, EnumParticleTypes}
import net.minecraft.world.World

import scala.util.Random

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since August 12, 2015
  */
class TileElectricCrusher extends MachineProcessor {

    val INPUT_SLOT = 0
    val OUTPUT_SLOT_1 : Int = 1
    val OUTPUT_SLOT_2  : Int = 2

    val BASE_ENERGY_TICK = 20

    /**
      * The initial size of the inventory
      *
      * @return
      */
    override def initialSize: Int = 3

    /**
      * Used to get how long it takes to cook things, you should check for upgrades at this point
      *
      * @return The time it takes in ticks to cook the current item
      */
    override def getCookTime : Int = {
        if(getUpgradeBoard != null && getUpgradeBoard.getProcessorCount > 0)
            200 - (getUpgradeBoard.getProcessorCount * 24)
        else
            200
    }

    /**
      * Used to tell if this tile is able to process
      *
      * @return True if you are able to process
      */
    override def canProcess : Boolean = {
        if(energy.getEnergyStored >= getEnergyCostPerTick) {
            if(getStackInSlot(INPUT_SLOT) == null || getOutputForStack(getStackInSlot(INPUT_SLOT)) == null)
                return false
            else if(getStackInSlot(OUTPUT_SLOT_1) == null)
                return true
            else if(!getStackInSlot(OUTPUT_SLOT_1).isItemEqual(getOutputForStack(getStackInSlot(INPUT_SLOT))))
                return false
            else {
                val minStackSize = getStackInSlot(OUTPUT_SLOT_1).stackSize + getOutputForStack(getStackInSlot(INPUT_SLOT)).stackSize
                return minStackSize <= getInventoryStackLimit && minStackSize <= getOutputForStack(getStackInSlot(INPUT_SLOT)).getMaxStackSize
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
    override def getOutputForStack(stack: ItemStack): ItemStack = {
        if (recipeCrusher(stack) == null)
            null
        else
            recipeCrusher(stack)._1
    }

    /**
      * Used to actually cook the item. You should reset values here if need be
      */
    override def cook(): Unit = cookTime += 1

    override def completeCook() {
        val input = getStackInSlot(INPUT_SLOT)
        var recipeResult: ItemStack = getOutputForStack(getStackInSlot(INPUT_SLOT))
        decrStackSize(INPUT_SLOT, 1)
        if (getStackInSlot(OUTPUT_SLOT_1) == null) {
            recipeResult = recipeResult.copy
            recipeResult.stackSize = recipeResult.stackSize
            setInventorySlotContents(OUTPUT_SLOT_1, recipeResult)
        }
        else {
            getStackInSlot(OUTPUT_SLOT_1).stackSize += recipeResult.stackSize
        }
        if (getUpgradeBoard != null && getUpgradeBoard.hasExpansion) extraOutput(input)
    }

    /**
      * Used to get the extra output of a cook operation
      *
      * @param input The item in
      */
    def extraOutput(input: ItemStack): Unit = {
        val recipeResult = recipeCrusher(input)
        if (recipeResult != null && recipeResult._2 != null && recipeResult._3 > 0) {
            val random = Random.nextInt(100)
            if (recipeResult._3 >= random) {
                val extra = recipeResult._2.copy
                if (getStackInSlot(2) == null) {
                    setInventorySlotContents(2, extra)
                } else if (getStackInSlot(2).isItemEqual(extra)) {
                    if (getStackInSlot(2).stackSize + 1 > extra.getMaxStackSize)
                        getStackInSlot(2).stackSize = extra.getMaxStackSize
                    else
                        getStackInSlot(2).stackSize += 1
                }
            }
        }
    }

    /**
      * Used to get the recipe for the crusher
      */
    def recipeCrusher(stack: ItemStack): (ItemStack, ItemStack, Int) = {
        CrusherRecipeRegistry.getOutput(stack).orNull
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

    override def getDescription : String = {
        GuiColor.YELLOW + "" + GuiTextFormat.BOLD + StatCollector.translateToLocal("tile.neotech:electricCrusher.name") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.electricCrusher.desc") + "\n\n" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + StatCollector.translateToLocal("neotech.text.upgrades") + ":\n" + GuiTextFormat.RESET +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.processors") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.electricFurnace.processorUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.hardDrives") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.electricFurnace.hardDriveUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.control") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.electricFurnace.controlUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.expansion") + ":\n" +
                GuiColor.WHITE +  StatCollector.translateToLocal("neotech.electricCrusher.expansionUpgrade.desc")
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
        new ContainerElectricCrusher(player.inventory, this)

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
        new GuiElectricCrusher(player, this)

    /*******************************************************************************************************************
      ************************************************ Inventory methods ***********************************************
      ******************************************************************************************************************/

    /**
      * Used to get what slots are allowed to be output
      *
      * @return The slots to output from
      */
    override def getOutputSlots: Array[Int] = Array(OUTPUT_SLOT_1, OUTPUT_SLOT_2)

    /**
      * Used to get what slots are allowed to be input
      *
      * @return The slots to input from
      */
    override def getInputSlots: Array[Int] = Array(INPUT_SLOT)

    /**
      * Returns true if automation can extract the given item in the given slot from the given side. Args: slot, item,
      * side
      */
    override def canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean = {
        index == OUTPUT_SLOT_1 || index == OUTPUT_SLOT_2
    }

    /**
      * Returns true if automation can insert the given item in the given slot from the given side. Args: slot, item,
      * side
      */
    override def canInsertItem(slot: Int, itemStackIn: ItemStack, direction: EnumFacing): Boolean = {
        if (slot == INPUT_SLOT && getOutputForStack(itemStackIn) != null) {
            if (getStackInSlot(INPUT_SLOT) == null) return true
            if (getStackInSlot(INPUT_SLOT).isItemEqual(itemStackIn)) {
                if (getStackInSlot(INPUT_SLOT).getMaxStackSize >= getStackInSlot(INPUT_SLOT).stackSize + itemStackIn.stackSize)
                    return true
            }
        }
        false
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
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y + 0.4, z, 0, 0, 0)
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y + 0.4, z, 0, 0, 0)
    }
}
