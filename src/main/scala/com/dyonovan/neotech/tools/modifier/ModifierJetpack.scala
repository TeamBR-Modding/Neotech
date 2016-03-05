package com.dyonovan.neotech.tools.modifier

import com.dyonovan.neotech.tools.upgradeitems.BaseUpgradeItem
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
  * @since 3/3/2016
  */
object ModifierJetpack extends Modifier("jetpack") {

    lazy val JETPACK = "Jetpack"
    lazy val ACTIVE = "Active"

    /**
      * Checks for silk touch
      */
    def hasJetpack(stack: ItemStack): Boolean = {
        val tag = getModifierTagFromStack(stack)
        // TODO: Remove this after the update after this update
        if(tag != null && !tag.hasKey(ACTIVE))
            tag.setBoolean(ACTIVE, true)
        if (tag != null && tag.hasKey(JETPACK))
            return tag.getBoolean(JETPACK) && tag.getBoolean("Active")
        false
    }

    /**
      * Write info to tag
      *
      * @return
      */
    def writeToNBT(tag: NBTTagCompound, stack: ItemStack, hasJetpack: Boolean): NBTTagCompound = {
        tag.setBoolean(JETPACK, hasJetpack)
        tag.setBoolean(ACTIVE, true)
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
         ArrayBuffer[String](ClientUtils.translate("neotech.text.jetpack"))

    class ItemModifierJetpack extends BaseUpgradeItem("jetpack", 1) {

        /**
          * Can this upgrade item allow more to be applied to the item
          *
          * @param stack The stack we want to apply to, get count from there
          * @param count The stack size of the input
          * @return True if there is space for the entire count
          */
        override def canAcceptLevel(stack: ItemStack, count: Int, name: String): Boolean =
            !ModifierJetpack.hasJetpack(stack)

        /**
          * Use this to put information onto the stack, called when put onto the stack
          *
          * @param stack The stack to put onto
          * @return The tag passed
          */
        override def writeInfoToNBT(stack: ItemStack, tag: NBTTagCompound,  writingStack : ItemStack): Unit = {
            var localTag = ModifierJetpack.getModifierTagFromStack(stack)
            if(localTag == null)
                localTag = new NBTTagCompound
            ModifierJetpack.writeToNBT(localTag, stack, if(writingStack.stackSize > 0) true else false)
            ModifierJetpack.overrideModifierTag(stack, localTag)
        }
    }
}