package com.teambrmodding.neotech.common.tiles.machines.processors

import java.util

import com.teambrmodding.neotech.client.gui.machines.processors.GuiElectricCrusher
import com.teambrmodding.neotech.collections.EnumInputOutputMode
import com.teambrmodding.neotech.common.container.machines.processors.ContainerElectricCrusher
import com.teambrmodding.neotech.common.tiles.MachineProcessor
import com.teambrmodding.neotech.managers.RecipeManager
import com.teambrmodding.neotech.registries.CrusherRecipeHandler
import com.teambr.bookshelf.client.gui.{GuiColor, GuiTextFormat}
import com.teambr.bookshelf.util.InventoryUtils
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.text.translation.I18n
import net.minecraft.util.{EnumFacing, EnumParticleTypes}
import net.minecraft.world.World

import scala.util.Random
import scala.util.control.Breaks._

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
class TileElectricCrusher extends MachineProcessor[ItemStack, ItemStack] {

    val INPUT_SLOT = 0
    val OUTPUT_SLOT_1 : Int = 1
    val OUTPUT_SLOT_2  : Int = 2

    val BASE_ENERGY_TICK = 100

    /**
      * The initial size of the inventory
      *
      * @return
      */
    override def initialSize: Int = 3

    /**
      * Add all modes you want, in order, here
      */
    def addValidModes() : Unit = {
        validModes += EnumInputOutputMode.INPUT_ALL
        validModes += EnumInputOutputMode.OUTPUT_ALL
        validModes += EnumInputOutputMode.OUTPUT_PRIMARY
        validModes += EnumInputOutputMode.OUTPUT_SECONDARY
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
        list.add(IUpgradeItem.EXPANSION_CARD)
        list
    }

    /**
      * Used to get how much energy to drain per tick, you should check for upgrades at this point
      *
      * @return How much energy to drain per tick
      */
    override def getEnergyCostPerTick: Int = {
        BASE_ENERGY_TICK * getMultiplierByCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY) +
                ((getMultiplierByCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU) - 1) * 12)
    }

    /**
      * Used to get how long it takes to cook things, you should check for upgrades at this point
      *
      * @return The time it takes in ticks to cook the current item
      */
    override def getCookTime : Int = {
        200 - (12 * getMultiplierByCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU))
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
            else if(getStackInSlot(OUTPUT_SLOT_1) == null)
                return true
            else if(!getStackInSlot(OUTPUT_SLOT_1).isItemEqual(getOutput(getStackInSlot(INPUT_SLOT))))
                return false
            else {
                val minStackSize = getStackInSlot(OUTPUT_SLOT_1).stackSize + getOutput(getStackInSlot(INPUT_SLOT)).stackSize
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
    override def completeCook() {
        breakable {
            for (cook <- 0 until getMultiplierByCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY)) {
                if (canProcess) {
                    val input = getStackInSlot(INPUT_SLOT)
                    var recipeResult: ItemStack = getOutput(getStackInSlot(INPUT_SLOT))
                    decrStackSize(INPUT_SLOT, 1)
                    if (getStackInSlot(OUTPUT_SLOT_1) == null) {
                        recipeResult = recipeResult.copy
                        recipeResult.stackSize = recipeResult.stackSize
                        setInventorySlotContents(OUTPUT_SLOT_1, recipeResult)
                    }
                    else
                        getStackInSlot(OUTPUT_SLOT_1).stackSize += recipeResult.stackSize

                    if (hasUpgradeByID(IUpgradeItem.EXPANSION_CARD))
                        extraOutput(input)
                } else break
            }
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
        if (recipeCrusher(stack) == null)
            null
        else
            recipeCrusher(stack)._1
    }

    /**
      * Get the output of the recipe
      *
      * @param input The input
      * @return The output
      */
    override def getOutput(input: ItemStack): ItemStack = getOutputForStack(input)

    /**
      * Used to get the recipe for the crusher
      */
    def recipeCrusher(stack: ItemStack): (ItemStack, ItemStack, Int) = {
        RecipeManager.getHandler[CrusherRecipeHandler](RecipeManager.Crusher).getOutput(stack).orNull
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
                InventoryUtils.moveItemInto(this, OUTPUT_SLOT_1, worldObj.getTileEntity(pos.offset(dir)), -1, 64,
                    dir.getOpposite, doMove = true, checkSidedSource = false)
            if(canOutputFromSide(dir, isPrimary = false))
                InventoryUtils.moveItemInto(this, OUTPUT_SLOT_2, worldObj.getTileEntity(pos.offset(dir)), -1, 64,
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
    override def getOutputSlots(mode : EnumInputOutputMode) : Array[Int] = Array(OUTPUT_SLOT_1, OUTPUT_SLOT_2)

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
        if (slot == INPUT_SLOT && getOutput(itemStackIn) != null) {
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

    override def getDescription : String = {
        "" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + ClientUtils.translate("neotech.text.stats") + ":\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.energyUsage") + ":\n" +
                GuiColor.WHITE + "  " + ClientUtils.formatNumber(getEnergyCostPerTick) + " RF/tick\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.processTime") + ":\n" +
                GuiColor.WHITE + "  " + getCookTime + " ticks\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.operations") + ":\n" +
                GuiColor.WHITE + "  " + getMultiplierByCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY) + "\n\n" +
                GuiColor.WHITE + I18n.translateToLocal("neotech.electricCrusher.desc") + "\n\n" +
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
                GuiColor.WHITE +  I18n.translateToLocal("neotech.electricFurnace.networkUpgrade.desc")  + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + I18n.translateToLocal("neotech.text.expansion") + ":\n" +
                GuiColor.WHITE +  I18n.translateToLocal("neotech.electricCrusher.expansionUpgrade.desc")
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
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y + 0.4, z, 0, 0, 0)
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y + 0.4, z, 0, 0, 0)
    }
}
