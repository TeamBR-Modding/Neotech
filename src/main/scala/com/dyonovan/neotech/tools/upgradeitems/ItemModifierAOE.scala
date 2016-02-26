package com.dyonovan.neotech.tools.upgradeitems

import com.dyonovan.neotech.tools.modifier.ModifierAOE
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/24/2016
  */
class ItemModifierAOE extends BaseUpgradeItem("aoe", 4) {

    /**
      * Can this upgrade item allow more to be applied to the item
      *
      * @param stack The stack we want to apply to, get count from there
      * @param count The stack size of the input
      * @return True if there is space for the entire count
      */
    override def canAcceptLevel(stack: ItemStack, count: Int, name: String): Boolean =
        ModifierAOE.getAOELevel(stack) + count <= getMaximumLevel

    /**
      * Use this to put information onto the stack, called when put onto the stack
      *
      * @param stack The stack to put onto
      * @return The tag passed
      */
    override def writeInfoToNBT(stack: ItemStack, tag: NBTTagCompound, writingStack : ItemStack): Unit = {
        var localTag = ModifierAOE.getModifierTagFromStack(stack)
        if (localTag == null) localTag = new NBTTagCompound
        ModifierAOE.writeToNBT(localTag, stack, writingStack.stackSize)
        ModifierAOE.overrideModifierTag(stack, localTag)
    }
}
