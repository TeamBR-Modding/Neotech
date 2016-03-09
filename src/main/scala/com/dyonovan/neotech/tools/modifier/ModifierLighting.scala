package com.dyonovan.neotech.tools.modifier

import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.tools.upgradeitems.BaseUpgradeItem
import com.dyonovan.neotech.utils.ClientUtils
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
  * @author Paul Davis "pauljoda"
  * @since 2/27/2016
  */
object ModifierLighting extends Modifier("lighting") {
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

    @ModItem(modid = Reference.MOD_ID)
    class ItemModifierLighting extends BaseUpgradeItem("lighting", 1) {

        /**
          * Can this upgrade item allow more to be applied to the item
          *
          * @param stack The stack we want to apply to, get count from there
          * @param count The stack size of the input
          * @return True if there is space for the entire count
          */
        override def canAcceptLevel(stack: ItemStack, count: Int, name: String): Boolean =
            !ModifierLighting.hasLighting(stack)

        /**
          * Use this to put information onto the stack, called when put onto the stack
          *
          * @param stack The stack to put onto
          * @return The tag passed
          */
        override def writeInfoToNBT(stack: ItemStack, tag: NBTTagCompound, writingStack: ItemStack): Unit = {
            var localTag = ModifierLighting.getModifierTagFromStack(stack)
            if (localTag == null)
                localTag = new NBTTagCompound
            ModifierLighting.writeToNBT(localTag, stack, if (writingStack.stackSize > 0) true else false)
            ModifierLighting.overrideModifierTag(stack, localTag)
        }
    }
}
