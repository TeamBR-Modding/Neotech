package com.dyonovan.neotech.tools

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList
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
