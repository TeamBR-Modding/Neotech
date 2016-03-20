package com.dyonovan.neotech.common.tiles.machines.processors

import com.dyonovan.neotech.client.gui.machines.processors.GuiElectricFurnace
import com.dyonovan.neotech.collections.EnumInputOutputMode
import com.dyonovan.neotech.common.container.machines.processors.ContainerElectricFurnace
import com.dyonovan.neotech.common.tiles.MachineProcessor
import com.dyonovan.neotech.utils.ClientUtils
import com.teambr.bookshelf.client.gui.{GuiTextFormat, GuiColor}
import com.teambr.bookshelf.util.InventoryUtils
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.util.text.translation.I18n
import net.minecraft.util.{EnumFacing, EnumParticleTypes}
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
class TileElectricFurnace extends MachineProcessor[ItemStack, ItemStack] {

    val INPUT_SLOT = 0
    val OUTPUT_SLOT = 1

    val BASE_ENERGY_TICK = 20

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
        if(energyStorage.getEnergyStored >= getEnergyCostPerTick) {
            if(getStackInSlot(INPUT_SLOT) == null || getOutput(getStackInSlot(INPUT_SLOT)) == null)
                return false
            else if(getStackInSlot(OUTPUT_SLOT) == null)
                return true
            else if(!getStackInSlot(OUTPUT_SLOT).isItemEqual(getOutput(getStackInSlot(INPUT_SLOT))))
                return false
            else {
                val minStackSize = getStackInSlot(OUTPUT_SLOT).stackSize - getOutput(getStackInSlot(INPUT_SLOT)).stackSize
                return minStackSize <= getInventoryStackLimit && minStackSize <= getOutput(getStackInSlot(INPUT_SLOT)).getMaxStackSize
            }
        }
        failCoolDown = 40
        false
    }

    /**
      * Used to actually cook the item. You should reset values here if need be
      */
    override def cook(): Unit = cookTime += 1

    /**
      * Called when the tile has completed the cook process
      */
    override def completeCook(): Unit = {
        var recipeResult = getOutput(getStackInSlot(0))
        decrStackSize(INPUT_SLOT, 1)
        if (getStackInSlot(OUTPUT_SLOT) == null) {
            recipeResult = recipeResult.copy
            recipeResult.stackSize = recipeResult.stackSize
            setInventorySlotContents(1, recipeResult)
        } else {
            getStackInSlot(1).stackSize += recipeResult.stackSize
        }

        worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 6)
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
        for(dir <- EnumFacing.values())
            if(canInputFromSide(dir))
                InventoryUtils.moveItemInto(worldObj.getTileEntity(pos.offset(dir)), -1, this, INPUT_SLOT, 64,
                    dir.getOpposite, doMove = true, checkSidedTarget = false)
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

    override def getDescription : String = {
        "" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + ClientUtils.translate("neotech.text.stats") + ":\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.energyUsage") + ":\n" +
                GuiColor.WHITE + "  " + getEnergyCostPerTick + " RF/tick\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.processTime") + ":\n" +
                GuiColor.WHITE + "  " + getCookTime + " ticks\n\n" + GuiColor.WHITE + I18n.translateToLocal("neotech.electricFurnace.desc") + "\n\n" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + I18n.translateToLocal("neotech.text.upgrades") + ":\n" + GuiTextFormat.RESET +
                GuiColor.YELLOW + GuiTextFormat.BOLD + I18n.translateToLocal("neotech.text.processors") + ":\n" +
                GuiColor.WHITE + I18n.translateToLocal("neotech.electricFurnace.processorUpgrade.desc") + "\n\n" +
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
        worldObj.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0.01, 0.49, 0.72)
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0, 0)
    }
}
