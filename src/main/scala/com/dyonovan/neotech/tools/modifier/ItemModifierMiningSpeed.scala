package com.dyonovan.neotech.tools.modifier

import com.dyonovan.neotech.tools.upgradeitems.BaseUpgradeItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

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
class ItemModifierMiningSpeed extends BaseUpgradeItem("miningSpeed", 4) {
    /**
      * Can this upgrade item allow more to be applied to the item
      *
      * @param stack The stack we want to apply to, get count from there
      * @param count The stack size of the input
      * @return True if there is space for the entire count
      */
    override def canAcceptLevel(stack: ItemStack, count: Int, name: String): Boolean = {
        val speed = ModifierMiningSpeed.getMiningSpeed(stack)
        speed + (count * 4.0F) <= 20.0F
    }

    /**
      * Use this to put information onto the stack, called when put onto the stack
      *
      * @param stack The stack to put onto
      * @return The tag passed
      */
    override def writeInfoToNBT(stack: ItemStack, tag: NBTTagCompound, count: Int): Unit = {
        var localTag = ModifierMiningSpeed.getModifierTagFromStack(stack)
        if(localTag == null)
            localTag = new NBTTagCompound
        ModifierMiningSpeed.writeToNBT(localTag, stack, ModifierMiningSpeed.getMiningSpeed(stack) + (count * 4.0F))
        ModifierMiningSpeed.overrideModifierTag(stack, localTag)
    }
}
