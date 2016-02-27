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
  * @since 2/27/2016
  */
object ModifierLighting extends Modifier("lighting"){
    lazy val LIGHTING = "Lighting"
    lazy val ACTIVE = "Active"

    /**
      * Checks for silk touch
      */
    def hasLighting(stack: ItemStack): Boolean = {
        val tag = getModifierTagFromStack(stack)
        if (tag != null && tag.hasKey(LIGHTING))
            return tag.getBoolean(LIGHTING)
        false
    }

    /**
      * Checks if Lighting is active
      */
    def isLightingActive(stack: ItemStack): Boolean = {
        val tag = getModifierTagFromStack(stack)
        if (tag != null && tag.hasKey(ACTIVE))
            tag.getBoolean(ACTIVE)
        else
            false
    }

    /**
      * Write info to tag
      *
      * @return
      */
    def writeToNBT(tag: NBTTagCompound, stack: ItemStack, hasLighting: Boolean): NBTTagCompound = {
        tag.setBoolean(LIGHTING, hasLighting)
        tag.setBoolean(ACTIVE, false)
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
        ArrayBuffer(ClientUtils.translate("neotech.text.lighting"))
}
