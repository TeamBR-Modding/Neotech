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
  * @author Dyonovan
  * @since 2/24/2016
  */
object ModifierAOE extends Modifier("aoe") {

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

    /**
      * Used to get the tool tip for this modifier
      *
      * @param stack The stack in
      * @return A list of tips
      */
    override def getToolTipForWriting(stack: ItemStack, tag : NBTTagCompound): ArrayBuffer[String] = new ArrayBuffer[String]()
}
