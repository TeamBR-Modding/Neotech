package com.dyonovan.neotech.tools.modifier

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
  * @since 2/24/2016
  */
class ModifierAOE extends Modifier("aoe") {

    lazy val AOE = "AOE"

    def getAOELevel(stack: ItemStack): Int = {
        val tag = getModifierTagFromStack(stack)
        if (tag != null && tag.hasKey(AOE))
            return tag.getInteger(AOE)
        0
    }

    def writeToNBT(tag: NBTTagCompound, stack: ItemStack, levelAOE: Int): NBTTagCompound = {
        tag.setInteger(AOE, levelAOE)
        super.writeToNBT(tag, stack)
        tag
    }
}
