package com.dyonovan.neotech.tools

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.EnumHelper

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
}
