package com.dyonovan.neotech.tools.upgradeitems

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.lib.Reference
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound

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
abstract class BaseUpgradeItem(name: String, stackSize: Int,
                      tab : CreativeTabs = NeoTech.tabTools) extends Item {

    setMaxStackSize(stackSize)
    if(tab != null)
        setCreativeTab(tab)
    setUnlocalizedName(Reference.MOD_ID + ":" + name)

    def getUpgradeName: String = name

    /**
      * Used to get the max level of this item
 *
      * @return
      */
    def getMaximumLevel : Int = stackSize

    /**
      * Can this upgrade item allow more to be applied to the item
 *
      * @param stack The stack we want to apply to, get count from there
      * @param count The stack size of the input
      * @return True if there is space for the entire count
      */
    def canAcceptLevel(stack : ItemStack, count : Int, name : String) : Boolean

    /**
      * Use this to put information onto the stack, called when put onto the stack
 *
      * @param stack The stack to put onto
      * @return The tag passed
      */
    def writeInfoToNBT(stack : ItemStack, tag: NBTTagCompound, writingStack : ItemStack) : Unit
}
