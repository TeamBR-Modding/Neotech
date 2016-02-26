package com.dyonovan.neotech.tools.modifier

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
  * @author Paul Davis "pauljoda"
  * @since 2/25/2016
  */
object ModifierShovel extends Modifier("shovelPick") {

    lazy val SHOVEL = "ShovelPick"

    /**
      * Checks for silk touch
      */
    def hasShovelUpgrade(stack: ItemStack): Boolean = {
        val tag = getModifierTagFromStack(stack)
        if (tag != null && tag.hasKey(SHOVEL))
            return tag.getBoolean(SHOVEL)
        false
    }

    /**
      * Write info to tag
      *
      * @return
      */
    def writeToNBT(tag: NBTTagCompound, stack: ItemStack, hasShovel: Boolean): NBTTagCompound = {
        tag.setBoolean(SHOVEL, hasShovel)
        super.writeToNBT(tag, stack)
        tag
    }

    /**
      * Used to get the tool tip for this modifier
      *
      * @param stack The stack in
      * @return A list of tips
      */
    override def getToolTipForWriting(stack: ItemStack, tag: NBTTagCompound): ArrayBuffer[String]
        = ArrayBuffer("Also a shovel!")
}
