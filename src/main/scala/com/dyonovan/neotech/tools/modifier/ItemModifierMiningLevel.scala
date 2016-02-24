package com.dyonovan.neotech.tools.modifier

import com.dyonovan.neotech.tools.ToolHelper
import com.dyonovan.neotech.tools.upgradeitems.BaseUpgradeItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}

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
class ItemModifierMiningLevel extends BaseUpgradeItem("miningLevel", 2) {

    /**
      * Can this upgrade item allow more to be applied to the item
      *
      * @param stack The stack we want to apply to, get count from there
      * @param count The stack size of the input
      * @return True if there is space for the entire count
      */
    override def canAcceptLevel(stack: ItemStack, count: Int, name : String): Boolean = {
        if(count > getMaximumLevel)
            return false

        var currentLevelOnStack : NBTTagCompound = null

        if(stack.hasTagCompound && stack.getTagCompound.hasKey(ToolHelper.ModifierListTag)) {
            val tagList = stack.getTagCompound.getTagList(ToolHelper.ModifierListTag, 10)
            for(x <- 0 until tagList.tagCount())
                if(tagList.getCompoundTagAt(x).getString("ModifierID").equalsIgnoreCase(ModifierMiningLevel.name))
                    currentLevelOnStack = tagList.getCompoundTagAt(x)
        }

        if(currentLevelOnStack.getInteger(ModifierMiningLevel.LEVEL) + count <= getMaximumLevel + 1)
            return true // The count is correct
        false
    }

    /**
      * Use this to put information onto the stack, called when put onto the stack
      *
      * @param stack The stack to put onto
      * @return The tag passed
      */
    override def writeInfoToNBT(stack: ItemStack, tag: NBTTagCompound, count: Int): Unit = {
        var localTag = new NBTTagCompound
        if(stack.hasTagCompound && stack.getTagCompound.hasKey(ToolHelper.ModifierListTag)) {
            val tagList = stack.getTagCompound.getTagList(ToolHelper.ModifierListTag, 10)
            for(x <- 0 until tagList.tagCount())
                if(tagList.getCompoundTagAt(x).getString("ModifierID").equalsIgnoreCase(ModifierMiningLevel.name))
                    localTag = tagList.getCompoundTagAt(x)
        }
        ModifierMiningLevel.writeToNBT(localTag, stack, ModifierMiningLevel.getLevel(localTag) + count)
        if(!stack.hasTagCompound || !stack.getTagCompound.hasKey(ToolHelper.ModifierListTag)) { // Write the new list
        val tagList = new NBTTagList
            tagList.appendTag(localTag)
            stack.getTagCompound.setTag(ToolHelper.ModifierListTag, tagList)
        } else {
            val tagList = stack.getTagCompound.getTagList(ToolHelper.ModifierListTag, 10)
            var added = false
            for(x <- 0 until tagList.tagCount())
                if(tagList.getCompoundTagAt(x).getString("ModifierID").equalsIgnoreCase(ModifierMiningLevel.name)) {
                    tagList.set(x, localTag)
                    added = true
                }
            if(!added)
                tagList.appendTag(localTag)
        }
    }
}
