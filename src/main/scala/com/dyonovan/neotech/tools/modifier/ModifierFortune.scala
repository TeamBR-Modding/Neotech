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
object ModifierFortune extends Modifier("fortune") {

    lazy val FORTUNE = "Fortune"

    def getFortuneLevel(stack: ItemStack): Int = {
        val tag = getModifierTagFromStack(stack)
        if (tag != null && tag.hasKey(FORTUNE))
            return tag.getInteger(FORTUNE)
        0
    }

    def writeToNBT(tag: NBTTagCompound, stack: ItemStack, fortune: Int): NBTTagCompound = {
        val list = EnchantmentHelper.getEnchantments(stack)
        list.put(Enchantment.fortune.effectId, fortune)
        EnchantmentHelper.setEnchantments(list, stack)
        tag.setInteger(FORTUNE, fortune)
        super.writeToNBT(tag, stack)
        tag
    }
}
