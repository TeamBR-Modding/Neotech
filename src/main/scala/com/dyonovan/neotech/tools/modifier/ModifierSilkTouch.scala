package com.dyonovan.neotech.tools.modifier

import net.minecraft.enchantment.{Enchantment, EnchantmentHelper}
import net.minecraft.item.ItemStack
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
object ModifierSilkTouch extends Modifier("silkTouch") {

    lazy val SILK = "SilkTouch"

    def hasSilkTouch(stack: ItemStack): Boolean = {
        val tag = getModifierTagFromStack(stack)
        if (tag != null && tag.hasKey(SILK))
            return tag.getBoolean(SILK)
        false
    }

    def writeToNBT(tag: NBTTagCompound, stack: ItemStack, hasSilkTouch: Boolean): NBTTagCompound = {
        val list = EnchantmentHelper.getEnchantments(stack)
        list.put(Enchantment.silkTouch.effectId, if (hasSilkTouch) 1 else 0)
        EnchantmentHelper.setEnchantments(list, stack)
        tag.setBoolean(SILK, hasSilkTouch)
        super.writeToNBT(tag, stack)
        tag
    }
}
