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
object ModifierSilkTouch extends Modifier("silkTouch") {

    lazy val SILK = "SilkTouch"

    /**
      * Checks for silk touch
      */
    def hasSilkTouch(stack: ItemStack): Boolean = {
        val tag = getModifierTagFromStack(stack)
        if (tag != null && tag.hasKey(SILK))
            return tag.getBoolean(SILK)
        false
    }

    /**
      * Write info to tag
      *
      * @return
      */
    def writeToNBT(tag: NBTTagCompound, stack: ItemStack, hasSilkTouch: Boolean): NBTTagCompound = {
        ToolHelper.writeVanillaEnchantment(tag, stack, Enchantment.silkTouch.effectId, 1)
        tag.setBoolean(SILK, hasSilkTouch)
        super.writeToNBT(tag, stack)
        tag
    }

    /**
      * Used to get the tool tip for this modifier
      *
      * @param stack The stack in
      * @return A list of tips
      */
    override def getToolTipForWriting(stack: ItemStack, tag : NBTTagCompound): ArrayBuffer[String]
    = new ArrayBuffer[String]() //Vanilla handles this
}
