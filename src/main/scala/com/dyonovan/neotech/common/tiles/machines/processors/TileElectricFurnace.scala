package com.dyonovan.neotech.common.tiles.machines.processors

import com.dyonovan.neotech.client.gui.machines.processors.GuiElectricFurnace
import com.dyonovan.neotech.common.container.machines.processors.ContainerElectricFurnace
import com.dyonovan.neotech.common.tiles.MachineProcessor
import com.teambr.bookshelf.client.gui.{GuiTextFormat, GuiColor}
import com.teambr.bookshelf.util.InventoryUtils
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.util.{StatCollector, EnumParticleTypes}
import net.minecraft.world.World

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since August 11, 2015
  */
class TileElectricFurnace extends MachineProcessor {

    val INPUT_SLOT = 0
    val OUTPUT_SLOT = 1

    val BASE_ENERGY_TICK = 20

    /**
      * The initial size of the inventory
      *
      * @return
      */
    override def initialSize: Int = 2

    override def getDescription : String = {
        GuiColor.YELLOW + "" + GuiTextFormat.BOLD + StatCollector.translateToLocal("tile.neotech:electricFurnace.name") + ":\n" +
        GuiColor.WHITE + StatCollector.translateToLocal("neotech.electricFurnace.desc") + "\n\n" +
        GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + StatCollector.translateToLocal("neotech.text.upgrades") + ":\n" + GuiTextFormat.RESET +
        GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.processors") + ":\n" +
        GuiColor.WHITE + StatCollector.translateToLocal("neotech.electricFurnace.processorUpgrade.desc") + "\n\n" +
        GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.hardDrives") + ":\n" +
        GuiColor.WHITE + StatCollector.translateToLocal("neotech.electricFurnace.hardDriveUpgrade.desc") + "\n\n" +
        GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.control") + ":\n" +
        GuiColor.WHITE + StatCollector.translateToLocal("neotech.electricFurnace.controlUpgrade.desc") + "\n\n" +
        GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.expansion") + ":\n" +
        GuiColor.WHITE +  StatCollector.translateToLocal("neotech.electricFurnace.expansionUpgrade.desc")
    }

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
            else if(getStackInSlot(OUTPUT_SLOT) == null)
                return true
            else if(!getStackInSlot(OUTPUT_SLOT).isItemEqual(getOutputForStack(getStackInSlot(INPUT_SLOT))))
                return false
            else {
                val minStackSize = getStackInSlot(OUTPUT_SLOT).stackSize - getOutputForStack(getStackInSlot(INPUT_SLOT)).stackSize
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
        if (stack != null)
            FurnaceRecipes.instance().getSmeltingResult(stack)
        else
            null
    }

    /**
      * Used to actually cook the item. You should reset values here if need be
      */
    override def cook(): Unit = cookTime += 1

    /**
      * Called when the tile has completed the cook process
      */
    override def completeCook(): Unit = {
        var recipeResult = getOutputForStack(getStackInSlot(0))
        decrStackSize(INPUT_SLOT, 1)
        if (getStackInSlot(OUTPUT_SLOT) == null) {
            recipeResult = recipeResult.copy
            recipeResult.stackSize = recipeResult.stackSize
            setInventorySlotContents(1, recipeResult)
        } else {
            getStackInSlot(1).stackSize += recipeResult.stackSize
        }

        worldObj.markBlockForUpdate(pos)
    }

    /**
      * Used to get how much energy to drain per tick, you should check for upgrades at this point
      *
      * @return How much energy to drain per tick
      */
    override def getEnergyCostPerTick: Int = {
        if(getUpgradeBoard != null && getUpgradeBoard.getProcessorCount > 0)
            BASE_ENERGY_TICK * (getUpgradeBoard.getProcessorCount * 0.4).toInt
        else
            BASE_ENERGY_TICK
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
            new ContainerElectricFurnace(player.inventory, this)

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
        new GuiElectricFurnace(player, this)

    /*******************************************************************************************************************
      ************************************************ Inventory methods ***********************************************
      ******************************************************************************************************************/

    /**
      * Used to get what slots are allowed to be output
      *
      * @return The slots to output from
      */
    override def getOutputSlots: Array[Int] = Array(OUTPUT_SLOT)

    /**
      * Used to get what slots are allowed to be input
      *
      * @return The slots to input from
      */
    override def getInputSlots: Array[Int] = Array(INPUT_SLOT)

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
        worldObj.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0.01, 0.49, 0.72)
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0, 0)
    }
}
