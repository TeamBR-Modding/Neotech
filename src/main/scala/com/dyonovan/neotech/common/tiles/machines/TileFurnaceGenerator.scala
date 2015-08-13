package com.dyonovan.neotech.common.tiles.machines

import cofh.api.energy.EnergyStorage
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
 * @since August 13, 2015
 */
class TileFurnaceGenerator extends AbstractMachine {

    final val RF_TICK = 40

    override val energy = new EnergyStorage(32000)

    override def doWork(): Unit = {
        //Generate

        //Transfer
    }
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

    override def initialSize: Int = 3

    override def canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean = false

    override def getSlotsForFace(side: EnumFacing): Array[Int] = Array[Int](0)
}
