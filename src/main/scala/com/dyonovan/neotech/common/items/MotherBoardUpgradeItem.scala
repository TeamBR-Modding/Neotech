package com.dyonovan.neotech.common.items

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.tools.upgradeitems.BaseUpgradeItem
import net.minecraft.creativetab.CreativeTabs
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
class MotherBoardUpgradeItem(name: String, stackSize: Int,
                             tab : CreativeTabs = NeoTech.tabTools) extends BaseUpgradeItem(name, stackSize, tab) {
    /**
      * Use this to put information onto the stack, called when put onto the stack
      *
      * @param stack The stack to put onto
      * @return The tag passed
      */
    override def writeInfoToNBT(stack: ItemStack, tag: NBTTagCompound, writingStack : ItemStack): Unit = {
        val count = writingStack.stackSize
        name match {
            case "upgradeHardDrive" => tag.setInteger("HardDrive", count)
            case "upgradeControl"   => tag.setBoolean("Control", true)
            case "upgradeProcessor" => tag.setInteger("Processor", count)
            case "upgradeExpansion" => tag.setBoolean("Expansion", true)
        }
    }

    /**
      * Can this upgrade item allow more to be applied to the item
      *
      * @param stack The stack we want to apply to, get count from there
      * @param count The stack size of the input
      * @return True if there is space for the entire count
      */
    override def canAcceptLevel(stack: ItemStack, count: Int, name : String): Boolean = {
        if(!stack.hasTagCompound && count <= 8)
            return true
        val tag = stack.getTagCompound
        name match {
            case "upgradeHardDrive" => tag.getInteger("HardDrive") + count <= 8
            case "upgradeControl"   => !tag.getBoolean("Control")
            case "upgradeProcessor" => tag.getInteger("Processor") + count <= 8
            case "upgradeExpansion" => !tag.getBoolean("Expansion")
        }
    }
}
