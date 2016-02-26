package com.dyonovan.neotech.tools.modifier

import com.dyonovan.neotech.utils.ClientUtils
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
  * @since 2/24/2016
  */
object ModifierBeheading extends Modifier("beheading") {
    lazy val BEHEADING = "Beheading"

    /**
      * Get the beheading level
      */
    def getBeheadingLevel(stack : ItemStack) : Int = {
        val tag = getModifierTagFromStack(stack)
        if(tag != null && tag.hasKey(BEHEADING))
            return tag.getInteger(BEHEADING)
        0
    }

    /**
      * Used to get the level for this modifier
      *
      * @param tag The tag that the level is stored on
      * @return The level
      */
    override def getLevel(tag : NBTTagCompound) = tag.getInteger(BEHEADING)

    /**
      * Write info to the tag
      */
    def writeToNBT(tag: NBTTagCompound, stack : ItemStack, level : Int): NBTTagCompound = {
        tag.setFloat(BEHEADING, getBeheadingLevel(stack) + level)
        super.writeToNBT(tag, stack)
        tag
    }

    /**
      * Used to get the tool tip for this modifier
      *
      * @param stack The stack in
      * @return A list of tips
      */
    override def getToolTipForWriting(stack: ItemStack, tag: NBTTagCompound): ArrayBuffer[String] = {
        ArrayBuffer("Beheading: " + ClientUtils.translate("enchantment.level." + tag.getInteger(BEHEADING)))
    }
}
