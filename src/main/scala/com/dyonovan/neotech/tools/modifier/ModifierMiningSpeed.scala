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
  * @since 2/23/2016
  */
object ModifierMiningSpeed extends Modifier("miningSpeed") {

    lazy val SPEED = "MiningSpeed"

    def getMiningSpeed(stack: ItemStack): Float = {
        val tag = getModifierTagFromStack(stack)
        if (tag != null && tag.hasKey(SPEED))
            return tag.getFloat(SPEED)
        4.0F
    }

    def writeToNBT(tag: NBTTagCompound, stack : ItemStack, miningSpeed : Float): NBTTagCompound = {
        tag.setFloat(SPEED, miningSpeed)
        super.writeToNBT(tag, stack)
        tag
    }

    /**
      * Used to get the level for this modifier
      *
      * @param tag The tag that the level is stored on
      * @return The level
      */
    override def getLevel(tag : NBTTagCompound) = (tag.getFloat(SPEED) / 4).toInt

    /**
      * Used to get the tool tip for this modifier
      *
      * @param stack The stack in
      * @return A list of tips
      */
    override def getToolTipForWriting(stack: ItemStack, tag : NBTTagCompound): ArrayBuffer[String] = {
        ArrayBuffer("Mining Speed: " + tag.getFloat(SPEED) * 100 / 4 + "%")
    }
}
