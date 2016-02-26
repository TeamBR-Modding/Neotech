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
    lazy val ACTIVE = "Active"

    /**
      * Get the AOE Level on this stack, 0 if none
      */
    def getAOELevel(stack: ItemStack): Int = {
        val tag = getModifierTagFromStack(stack)
        if (tag != null && tag.hasKey(AOE))
            return tag.getInteger(AOE)
        0
    }

    /**
      * Checks if AOE is active
      */
    def isAOEActive(stack: ItemStack): Boolean = {
        val tag = getModifierTagFromStack(stack)
        if (tag != null && tag.hasKey(ACTIVE))
            tag.getBoolean(ACTIVE)
        else
            false
    }

    /**
      * Get the upgrade level
      * @param tag The tag that the level is stored on
      * @return The level
      */
    override def getLevel(tag : NBTTagCompound) = tag.getInteger(AOE)

    /**
      * Write the info to that tag
      */
    def writeToNBT(tag: NBTTagCompound, stack: ItemStack, levelAOE: Int): NBTTagCompound = {
        tag.setInteger(AOE, getAOELevel(stack) + levelAOE)
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
    override def getToolTipForWriting(stack: ItemStack, tag : NBTTagCompound): ArrayBuffer[String] = {
        var size: String = ""
        tag.getInteger(AOE) match {
            case 1 => size = "3x3"
            case 2 => size = "5x5"
            case 3 => size = "7x7"
            case 4 => size = "9x9"
            case _ => size = "1x1"
        }
        ArrayBuffer("AOE: " + size)
    }
}
