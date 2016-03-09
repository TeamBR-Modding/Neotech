package com.dyonovan.neotech.tools.upgradeitems

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.lib.Reference
import com.teambr.bookshelf.common.items.traits.SimpleItemModelProvider
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

import scala.collection.mutable.ArrayBuffer

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
                               tab : CreativeTabs = NeoTech.tabTools) extends SimpleItemModelProvider {

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
      * Provide the manager with a list of upgrades to create
      * @return
      */
    override def getTextures: ArrayBuffer[String] = ArrayBuffer("neotech:items/tools/upgrades/" + name + "Upgrade")

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
