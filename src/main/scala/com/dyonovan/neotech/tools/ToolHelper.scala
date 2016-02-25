package com.dyonovan.neotech.tools

import java.util

import com.dyonovan.neotech.tools.modifier.ModifierAOE
import com.google.common.collect.Lists
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.{EnumFacing, BlockPos, MovingObjectPosition}
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


    def getModifierTag(stack : ItemStack) : NBTTagList = {
        if(stack.hasTagCompound && stack.getTagCompound.hasKey(ModifierListTag))
            stack.getTagCompound.getTagList(ModifierListTag, 10)
        else
            null
    }

    def getCurrentUpgradeCount(stack : ItemStack) : Int = {
        var count = 0
        val tag = getModifierTag(stack)
        if(tag != null) {
            for(x <- 0 until tag.tagCount()) {
                count += tag.getCompoundTagAt(x).getInteger("ModifierLevel")
            }
        }
        count
    }

    def getBlockList(level: Int, mop: MovingObjectPosition, player : EntityPlayer, world: World, stack: ItemStack): java.util.List[BlockPos] = {
        if(!player.isSneaking && world.getBlockState(mop.getBlockPos).getBlock.canHarvestBlock(world, mop.getBlockPos, player) && ModifierAOE.getAOEActive(stack)) {
            var pos1: BlockPos = null
            var pos2: BlockPos = null
            if (mop.sideHit.getAxis.isHorizontal) {
                if (mop.sideHit == EnumFacing.NORTH || mop.sideHit == EnumFacing.SOUTH) {
                    pos1 = mop.getBlockPos.offset(EnumFacing.UP, level).offset(EnumFacing.EAST, level)
                    pos2 = mop.getBlockPos.offset(EnumFacing.DOWN, level).offset(EnumFacing.WEST, level)
                } else {
                    pos1 = mop.getBlockPos.offset(EnumFacing.UP, level).offset(EnumFacing.SOUTH, level)
                    pos2 = mop.getBlockPos.offset(EnumFacing.DOWN, level).offset(EnumFacing.NORTH, level)
                }
            } else {
                pos1 = mop.getBlockPos.offset(EnumFacing.NORTH, level).offset(EnumFacing.WEST, level)
                pos2 = mop.getBlockPos.offset(EnumFacing.SOUTH, level).offset(EnumFacing.EAST, level)
            }
            val list: java.util.List[BlockPos] = Lists.newArrayList(BlockPos.getAllInBox(pos1, pos2).iterator())
            val actualList = new java.util.ArrayList[BlockPos]()
            for (l <- list.toArray()) {
                val pos = l.asInstanceOf[BlockPos]
                val block = world.getBlockState(pos).getBlock
                if (!block.isAir(world, pos) && block.canHarvestBlock(world, pos, player) && !player.capabilities.isCreativeMode &&
                        block.getBlockHardness(world, pos) >= 0 && FluidRegistry.lookupFluidForBlock(block) == null) {
                    actualList.add(pos)
                } else if (player.capabilities.isCreativeMode) actualList.add(pos)
            }
            actualList
        } else util.Arrays.asList(mop.getBlockPos)
    }

    /**
      * Used to get the tool tip for this modifier, you should read from the tag here
      *
      * @param stack The stack in
      * @return A list of tips
      */
    def getToolTipForDisplay(stack : ItemStack) : ArrayBuffer[String] = {
        val buffer = new ArrayBuffer[String]()
        val tagList = getModifierTag(stack)
        if(tagList != null) {
            for (x <- 0 until tagList.tagCount()) {
                if (stack.hasTagCompound && tagList.getCompoundTagAt(x) != null) {
                    val ourTag = tagList.getCompoundTagAt(x)
                    val tipList = ourTag.getTagList("ModifierTipList", 8)
                    for (x <- 0 until tipList.tagCount())
                        buffer += tipList.getStringTagAt(x)
                }
            }
        }
        buffer
    }
}
