package com.dyonovan.neotech.common.tiles.machines

import com.dyonovan.neotech.common.tiles.AbstractMachine
import com.dyonovan.neotech.registries.CrusherRecipeRegistry
import net.minecraft.inventory.Container
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

import scala.util.Random

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 12, 2015
 */
class TileElectricCrusher extends AbstractMachine {

    override def smeltItem() {
        var recipeResult: ItemStack = recipe(getStackInSlot(0))
        decrStackSize(0, 1)
        if (getStackInSlot(1) == null) {
            recipeResult = recipeResult.copy
            recipeResult.stackSize = recipeResult.stackSize
            setInventorySlotContents(1, recipeResult)
        }
        else {
            getStackInSlot(1).stackSize += recipeResult.stackSize
        }
        extraOutput(getStackInSlot(0))
    }

    def extraOutput(input: ItemStack): Unit = {
        if (getCrusherExtraCount > 0) {
            val recipeResult = recipeCrusher(input)
            if (recipeResult != null && recipeResult._2 != null) {
                var extraCount = 0
                val random = Random.nextInt(100)
                if (getCrusherExtraCount >= random) {
                    extraCount += 1
                }

                val extra = recipeResult._2.copy

                if (getStackInSlot(2) == null && extraCount > 0) {
                    if (extraCount > extra.getMaxStackSize)
                        extra.stackSize = extra.getMaxStackSize
                    else
                        extra.stackSize = extraCount
                    setInventorySlotContents(2, extra)
                } else if (extraCount > 0 && getStackInSlot(2).isItemEqual(extra)) {
                    if (extraCount + getStackInSlot(2).stackSize > extra.getMaxStackSize)
                        getStackInSlot(2).stackSize = extra.getMaxStackSize
                    else
                        getStackInSlot(2).stackSize += extraCount
                }
            }
        }
    }

    def getCrusherExtraCount: Int = 0

    /**
     * Get the output of the recipe
     * @param stack The input
     * @return The output
     */
    override def recipe(stack: ItemStack): ItemStack = if (recipeCrusher(stack) == null) null else recipeCrusher(stack)._1

    def recipeCrusher(stack: ItemStack): (ItemStack, ItemStack) = {
        CrusherRecipeRegistry.getOutput(stack).orNull
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
    override def getRedstoneOutput: Int = Container.calcRedstoneFromInventory(this)

    override def initialSize: Int = 3

    override def canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean = {
        index == 1 || index == 2
    }

    override def getSlotsForFace(side: EnumFacing): Array[Int] = {
        Array[Int](0, 1, 2)
    }
}
