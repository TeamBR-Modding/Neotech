package com.dyonovan.neotech.tools

import java.util

import com.dyonovan.neotech.tools.modifier.ModifierAOE
import com.dyonovan.neotech.tools.tools.BaseElectricTool
import com.dyonovan.neotech.tools.upgradeitems.ThermalBinderItem
import com.dyonovan.neotech.utils.ClientUtils
import com.teambr.bookshelf.client.gui.{GuiColor, GuiTextFormat}
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.util.{BlockPos, EnumFacing, MovingObjectPosition}
import net.minecraft.world.World
import net.minecraftforge.common.util.EnumHelper
import net.minecraftforge.fluids.FluidRegistry

import scala.collection.mutable.ArrayBuffer

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/23/2016
  */
object ToolHelper {

    // The enumeration for tool types
    object ToolType extends Enumeration {
        type ToolType = Value
        val Pickaxe, Axe, Shovel, Hoe, Sword, Empty_MB, Filled_MB = Value
    }

    lazy val NEOTECH         = EnumHelper.addToolMaterial("NEOTECH", 1, 1, 4.0F, 1.0F, 0)
    lazy val ModifierListTag = "ModifierList"

    /**
      * Used to get the tag list containing the Modifier Tags
      *
      * @param stack The stack to check
      * @return The tag list
      */
    def getModifierTagList(stack : ItemStack) : NBTTagList = {
        if(stack.hasTagCompound && stack.getTagCompound.hasKey(ModifierListTag))
            stack.getTagCompound.getTagList(ModifierListTag, 10)
        else
            null
    }

    /**
      * Gets the upgrade count on the stack
      *
      * @param stack The stack to check
      * @return
      */
    def getCurrentUpgradeCount(stack : ItemStack) : Int = {
        if(stack == null || !stack.getItem.isInstanceOf[ThermalBinderItem])
            0
        else
            stack.getItem.asInstanceOf[ThermalBinderItem].getUpgradeCount(stack)
    }

    /**
      * Used to put vanilla enchant on item
      */
    def writeVanillaEnchantment(tag: NBTTagCompound, stack: ItemStack, id : Int, level: Int) : Unit = {
        val list = EnchantmentHelper.getEnchantments(stack)
        list.put(id, level)
        EnchantmentHelper.setEnchantments(list, stack)
    }

    /**
      * Gets the list of blocks for the tool to mine, taking into account AOE
      *
      * @param level    The level of AOE
      * @param mop      The {MovingObjectPosition} of the block targeted
      * @param player   The player
      * @param world    The World
      * @param stack    The stack mining
      * @return         An List of blocks that are to be mined
      */
    def getBlockList(level: Int, mop: MovingObjectPosition, player : EntityPlayer, world: World, stack: ItemStack)
        : java.util.List[BlockPos] = {
        // Has to be able to harvest the block targeted to add more, plus has AOE, and effective
        if(world.getBlockState(mop.getBlockPos).getBlock.canHarvestBlock(world, mop.getBlockPos, player)
                && ModifierAOE.isAOEActive(stack)) {
            var pos1: BlockPos = null
            var pos2: BlockPos = null
            if (mop.sideHit.getAxis.isHorizontal) { // Rotate for Horizontal
                if (mop.sideHit == EnumFacing.NORTH || mop.sideHit == EnumFacing.SOUTH) {
                    pos1 = mop.getBlockPos.offset(EnumFacing.UP, level).offset(EnumFacing.EAST, level)
                    pos2 = mop.getBlockPos.offset(EnumFacing.DOWN, level).offset(EnumFacing.WEST, level)
                } else {
                    pos1 = mop.getBlockPos.offset(EnumFacing.UP, level).offset(EnumFacing.SOUTH, level)
                    pos2 = mop.getBlockPos.offset(EnumFacing.DOWN, level).offset(EnumFacing.NORTH, level)
                }

                while(pos2.getY < mop.getBlockPos.getY - 1) {
                    pos1 = pos1.offset(EnumFacing.UP)
                    pos2 = pos2.offset(EnumFacing.UP)
                }

            } else { // Easy, Vertical plane is just flat either way
                pos1 = mop.getBlockPos.offset(EnumFacing.NORTH, level).offset(EnumFacing.WEST, level)
                pos2 = mop.getBlockPos.offset(EnumFacing.SOUTH, level).offset(EnumFacing.EAST, level)
            }

            // Get the list iterator
            val iterator = BlockPos.getAllInBox(pos1, pos2).iterator()
            // Create a list for what we want to mine
            val actualList = new java.util.ArrayList[BlockPos]()

            // Iterate the given list
            while(iterator.hasNext) {
                val pos = iterator.next()
                val block = world.getBlockState(pos).getBlock
                if (player.capabilities.isCreativeMode) actualList.add(pos) // Creative, add it anyway
                else if (!block.isAir(world, pos) && block.canHarvestBlock(world, pos, player) &&
                        block.getBlockHardness(world, pos) >= 0 && FluidRegistry.lookupFluidForBlock(block) == null) { // Check if not air, isn't too hard, fluid, or non effective
                    actualList.add(pos)
                }
            }
            actualList // Return our built list
        } else util.Arrays.asList(mop.getBlockPos) // Return your own list
    }

    /**
      * Used to get the tool tip for this modifier, you should read from the tag here
      *
      * @param stack The stack in
      * @return A list of tips
      */
    def getToolTipForDisplay(stack : ItemStack) : ArrayBuffer[String] = {
        val buffer = new ArrayBuffer[String]()
        if(stack.hasTagCompound) {
            buffer += GuiColor.ORANGE + ClientUtils.translate("neotech.text.redstoneFlux")
            buffer += ClientUtils.formatNumber(
                stack.getItem.asInstanceOf[BaseElectricTool].getEnergyStored(stack)) + " / " +
                    ClientUtils.formatNumber(stack.getItem.asInstanceOf[BaseElectricTool].getMaxEnergyStored(stack))
            buffer += ""
            buffer += GuiColor.YELLOW + ClientUtils.translate("neotech.text.upgrades") + ": " + GuiTextFormat.RESET +
                    ToolHelper.getCurrentUpgradeCount(stack) + " / " +
                    stack.getItem.asInstanceOf[BaseElectricTool].getMaximumUpgradeCount(stack)
            buffer += ""

            val tagList = getModifierTagList(stack)
            if (tagList != null) {
                for (x <- 0 until tagList.tagCount()) {
                    if (stack.hasTagCompound && tagList.getCompoundTagAt(x) != null) {
                        val ourTag = tagList.getCompoundTagAt(x)
                        val tipList = ourTag.getTagList("ModifierTipList", 8)
                        for (x <- 0 until tipList.tagCount())
                            buffer += tipList.getStringTagAt(x)
                    }
                }
            }
        } else {
            buffer += GuiTextFormat.ITALICS + ClientUtils.translate("neotech.text.placeInBinder")
        }
        buffer
    }
}
