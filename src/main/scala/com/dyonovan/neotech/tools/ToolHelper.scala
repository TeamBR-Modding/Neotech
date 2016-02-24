package com.dyonovan.neotech.tools

import java.util

import com.google.common.collect.Lists
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.{EnumFacing, BlockPos, MovingObjectPosition}
import net.minecraftforge.common.util.EnumHelper

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

    def getBlockList(level: Int, mop: MovingObjectPosition, pos: BlockPos, player : EntityPlayer): java.util.List[BlockPos] = {
        if(!player.isSneaking) {
            var pos1: BlockPos = null
            var pos2: BlockPos = null
            if (mop.sideHit.getAxis.isHorizontal) {
                pos1 = pos.offset(EnumFacing.UP).offset(mop.sideHit)
                pos2 = pos.offset(EnumFacing.DOWN).offset(mop.sideHit)
            } else {
                pos1 = pos.offset(EnumFacing.NORTH).offset(EnumFacing.WEST)
                pos2 = pos.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST)
            }
            val list: java.util.List[BlockPos] = Lists.newArrayList(BlockPos.getAllInBox(pos1, pos2).iterator())
            list
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
