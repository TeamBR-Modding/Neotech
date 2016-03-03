package com.dyonovan.neotech.tools.modifier

import com.dyonovan.neotech.tools.ToolHelper
import net.minecraft.enchantment.Enchantment
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
  * @since 3/2/2016
  */
object ModifierLooting extends Modifier("looting") {

    lazy val LOOTING = "Looting"

    /**
      * Get looting level
      */
    def getLootingLevel(stack: ItemStack): Int = {
        val tag = getModifierTagFromStack(stack)
        if (tag != null && tag.hasKey(LOOTING))
            return tag.getInteger(LOOTING)
        0
    }

    /**
      * Used to get the level for this modifier
      *
      * @param tag The tag that the level is stored on
      * @return The level
      */
    override def getLevel(tag : NBTTagCompound) = tag.getInteger(LOOTING)

    /**
      * Write info to the tag
      */
    def writeToNBT(tag: NBTTagCompound, stack: ItemStack, looting: Int): NBTTagCompound = {
        ToolHelper.writeVanillaEnchantment(tag, stack, Enchantment.looting.effectId, getLootingLevel(stack) + looting)
        tag.setInteger(LOOTING, looting)
        super.writeToNBT(tag, stack)
        tag
    }

    /**
      * Used to get the tool tip for this modifier
      *
      * @param stack The stack in
      * @return A list of tips
      */
    override def getToolTipForWriting(stack: ItemStack, tag : NBTTagCompound): ArrayBuffer[String] =
        new ArrayBuffer[String]()  //Vanilla handles this
}