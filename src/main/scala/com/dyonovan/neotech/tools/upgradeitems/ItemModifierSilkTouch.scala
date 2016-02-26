package com.dyonovan.neotech.tools.upgradeitems

import com.dyonovan.neotech.tools.ToolHelper
import com.dyonovan.neotech.tools.modifier.ModifierSilkTouch
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
  * @since 2/24/2016
  */
class ItemModifierSilkTouch extends BaseUpgradeItem("silkTouch", 1) {

    /**
      * Can this upgrade item allow more to be applied to the item
      *
      * @param stack The stack we want to apply to, get count from there
      * @param count The stack size of the input
      * @return True if there is space for the entire count
      */
    override def canAcceptLevel(stack: ItemStack, count: Int, name: String): Boolean =
        !ModifierSilkTouch.hasSilkTouch(stack)

    /**
      * Use this to put information onto the stack, called when put onto the stack
      *
      * @param stack The stack to put onto
      * @return The tag passed
      */
    override def writeInfoToNBT(stack: ItemStack, tag: NBTTagCompound,  writingStack : ItemStack): Unit = {
        val localTag = new NBTTagCompound
        ModifierSilkTouch.writeToNBT(localTag, stack, if (writingStack.stackSize > 0) true else false)
        var tagList = new NBTTagList
        if(stack.getTagCompound.hasKey(ToolHelper.ModifierListTag))
            tagList = stack.getTagCompound.getTagList(ToolHelper.ModifierListTag, 10)
        tagList.appendTag(localTag)
    }
}
