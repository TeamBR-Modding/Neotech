package com.teambrmodding.neotech.tools.upgradeitems

import com.teambrmodding.neotech.tools.ToolHelper
import com.teambrmodding.neotech.tools.ToolHelper.ToolType.ToolType
import net.minecraft.item.ItemStack

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/23/2016
  */
trait ThermalBinderItem {

    /**
      * The list of things that are accepted by this item
      */
    def acceptableUpgrades: java.util.ArrayList[String]

    /**
      * What type of tool is this?
      *
      * @return
      */
    def getToolType   : ToolType

    /**
      * Used to get the upgrade count on this item, mainly used in the motherboard to determine how long to cook
      *
      * @param stack ItemStack
      * @return
      */
    def getUpgradeCount(stack: ItemStack): Int = {
        if (stack.hasTagCompound && stack.getTagCompound.hasKey(ToolHelper.ModifierListTag)) {
            val tagList = stack.getTagCompound.getTagList(ToolHelper.ModifierListTag, 10)
            var count = 0
            for (x <- 0 until tagList.tagCount())
                count += tagList.getCompoundTagAt(x).getInteger("ModifierLevel")
            return count
        }
        0
    }

    /**
      * Gets the maximum count for upgrades, default 8
      *
      * @param stack The stack in
      * @return How many this can hold, check for modifiers here
      */
    def getMaximumUpgradeCount(stack : ItemStack) : Int = 10

    /**
      * Used to specify if the stack can accept more
      *
      * @return True if all items can be put on
      */
    def canAcceptCount(stack: ItemStack, stacksIn: (ItemStack, ItemStack, ItemStack, ItemStack)): Boolean = {
        if(getUpgradeCount(stack) < getMaximumUpgradeCount(stack)) {
            var upgradeCount = 0
            upgradeCount += (if(stacksIn._1 != null) stacksIn._1.stackSize else 0)
            upgradeCount += (if(stacksIn._2 != null) stacksIn._2.stackSize else 0)
            upgradeCount += (if(stacksIn._3 != null) stacksIn._3.stackSize else 0)
            upgradeCount += (if(stacksIn._4 != null) stacksIn._4.stackSize else 0)
            return getUpgradeCount(stack) + upgradeCount <= getMaximumUpgradeCount(stack)
        }
        false
    }

    /**
      * Defines if the upgrade can be applied to this
      *
      * @param upgradeName The upgrade name
      * @return
      */
    def isAcceptableUpgrade(upgradeName: String): Boolean = acceptableUpgrades.contains(upgradeName)
}
