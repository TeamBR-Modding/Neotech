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
  * @author Dyonovan
  * @since 2/23/2016
  */
object ModifierFortune extends Modifier("fortune") {

    lazy val FORTUNE = "Fortune"

    /**
      * Get fortune level
      */
    def getFortuneLevel(stack: ItemStack): Int = {
        val tag = getModifierTagFromStack(stack)
        if (tag != null && tag.hasKey(FORTUNE))
            return tag.getInteger(FORTUNE)
        0
    }

    /**
      * Used to get the level for this modifier
      *
      * @param tag The tag that the level is stored on
      * @return The level
      */
    override def getLevel(tag : NBTTagCompound) = tag.getInteger(FORTUNE)

    /**
      * Write info to the tag
      */
    def writeToNBT(tag: NBTTagCompound, stack: ItemStack, fortune: Int): NBTTagCompound = {
        ToolHelper.writeVanillaEnchantment(tag, stack, Enchantment.fortune.effectId, getFortuneLevel(stack) + fortune)
        tag.setInteger(FORTUNE, fortune)
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
