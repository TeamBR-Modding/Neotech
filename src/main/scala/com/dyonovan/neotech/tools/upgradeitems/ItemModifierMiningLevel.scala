package com.dyonovan.neotech.tools.upgradeitems

import com.dyonovan.neotech.tools.modifier.ModifierMiningLevel
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
  * @since 2/23/2016
  */
class ItemModifierMiningLevel(level : Int) extends BaseUpgradeItem("miningLevel" + level, 1) {

    /**
      * Can this upgrade item allow more to be applied to the item
      *
      * @param stack The stack we want to apply to, get count from there
      * @param count The stack size of the input
      * @return True if there is space for the entire count
      */
    override def canAcceptLevel(stack: ItemStack, count: Int, name : String): Boolean = {
        true
    }

    /**
      * Used to get the mining level on an ItemModifierMiningLevel stack
      * @param stack The stack
      * @return The current mining level
      */
    def getMiningLevelOnThis(stack : ItemStack): Int = {
        if(stack == null)
            return 0
        stack.getItem.asInstanceOf[BaseUpgradeItem].getUpgradeName match {
            case "miningLevel2" => 2
            case "miningLevel3" => 3
            case "miningLevel4" => 4
            case _ => 0
        }
    }

    /**
      * Use this to put information onto the stack, called when put onto the stack
      *
      * @param stack The stack to put onto
      * @return The tag passed
      */
    override def writeInfoToNBT(stack: ItemStack, tag: NBTTagCompound, writingStack : ItemStack): Unit = {
        var localTag = ModifierMiningLevel.getModifierTagFromStack(stack)
        if(localTag == null)
            localTag = new NBTTagCompound
        ModifierMiningLevel.writeToNBT(localTag, stack, getMiningLevelOnThis(writingStack))
        ModifierMiningLevel.overrideModifierTag(stack, localTag)
    }
}
