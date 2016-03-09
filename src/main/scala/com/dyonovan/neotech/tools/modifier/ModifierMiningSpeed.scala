package com.dyonovan.neotech.tools.modifier

import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.tools.upgradeitems.BaseUpgradeItem
import com.teambr.bookshelf.annotations.ModItem
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

    /**
      * Get the mining speed
      */
    def getMiningSpeed(stack: ItemStack): Float = {
        val tag = getModifierTagFromStack(stack)
        if (tag != null && tag.hasKey(SPEED))
            return tag.getFloat(SPEED)
        4.0F
    }

    /**
      * Write info to tag
      */
    def writeToNBT(tag: NBTTagCompound, stack : ItemStack, count : Float): NBTTagCompound = {
        tag.setFloat(SPEED, getMiningSpeed(stack) + (count * 4.0F))
        super.writeToNBT(tag, stack)
        tag
    }

    /**
      * Used to get the level for this modifier
      *
      * @param tag The tag that the level is stored on
      * @return The level
      */
    override def getLevel(tag : NBTTagCompound) = (tag.getFloat(SPEED) / 4).toInt - 1

    /**
      * Used to get the tool tip for this modifier
      *
      * @param stack The stack in
      * @return A list of tips
      */
    override def getToolTipForWriting(stack: ItemStack, tag : NBTTagCompound): ArrayBuffer[String] = {
        ArrayBuffer("Mining Speed: " + (tag.getFloat(SPEED) * 100 / 4).toInt + "%")
    }

    @ModItem(modid = Reference.MOD_ID)
    class ItemModifierMiningSpeed extends BaseUpgradeItem("miningSpeed", 6) {

        /**
          * Can this upgrade item allow more to be applied to the item
          *
          * @param stack The stack we want to apply to, get count from there
          * @param count The stack size of the input
          * @return True if there is space for the entire count
          */
        override def canAcceptLevel(stack: ItemStack, count: Int, name: String): Boolean = {
            val speed = ModifierMiningSpeed.getMiningSpeed(stack)
            speed + (count * 4.0F) <= 4 + (maxStackSize * 4)
        }

        /**
          * Use this to put information onto the stack, called when put onto the stack
          *
          * @param stack The stack to put onto
          * @return The tag passed
          */
        override def writeInfoToNBT(stack: ItemStack, tag: NBTTagCompound,  writingStack : ItemStack): Unit = {
            var localTag = ModifierMiningSpeed.getModifierTagFromStack(stack)
            if(localTag == null)
                localTag = new NBTTagCompound
            ModifierMiningSpeed.writeToNBT(localTag, stack, writingStack.stackSize)
            ModifierMiningSpeed.overrideModifierTag(stack, localTag)
        }
    }
}
