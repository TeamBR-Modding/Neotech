package com.dyonovan.neotech.common.tiles.machines

import com.dyonovan.neotech.managers.ItemManager
import com.dyonovan.neotech.common.tiles.AbstractMachine
import net.minecraft.inventory.Container
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

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
class TileThermalBinder extends AbstractMachine {

    override def initialSize: Int = 6
    final val INPUT1 = 0
    final val INPUT2 = 1
    final val INPUT3 = 2
    final val INPUT4 = 3
    final val MB_INPUT = 4
    final val MB_OUTPUT = 5



    override def doWork(): Unit = {

    }

    override def spawnActiveParticles(x: Double, y: Double, z: Double): Unit = {}

    /**
     * Get the output of the recipe
     * @param stack The input
     * @return The output
     */
    override def recipe(stack: ItemStack): ItemStack = null

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
    override def getRedstoneOutput: Int = Container.calcRedstoneFromInventory(this)

    override def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean = {
        val item = stack.getItem
        if (slot >= 0 && slot <= 3)
            item.isInstanceOf[ItemManager.upgradeControl.type] || item.isInstanceOf[ItemManager.upgradeExpansion.type] ||
                    item.isInstanceOf[ItemManager.upgradeHardDrive.type] || item.isInstanceOf[ItemManager.upgradeProcessor.type]
        else if (slot == 4) item.isInstanceOf[ItemManager.upgradeMBEmpty.type] || item.isInstanceOf[ItemManager.upgradeMBFull.type]
        else false
    }

    override def canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean = index == 5

    override def canInsertItem(slot: Int, itemStackIn: ItemStack, direction: EnumFacing): Boolean = {
        isItemValidForSlot(slot, itemStackIn)
    }
}
