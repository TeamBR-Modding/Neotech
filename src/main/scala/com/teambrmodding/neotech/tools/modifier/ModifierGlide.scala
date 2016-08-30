package com.teambrmodding.neotech.tools.modifier

import com.teambrmodding.neotech.lib.Reference
import com.teambrmodding.neotech.tools.upgradeitems.BaseUpgradeItem
import com.teambrmodding.neotech.utils.ClientUtils
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
  * @since 3/3/2016
  */
object ModifierGlide extends Modifier("glide") {

    lazy val GLIDE = "Glide"
    lazy val ACTIVE = "Active"

    /**
      * Checks for silk touch
      */
    def hasGlide(stack: ItemStack): Boolean = {
        val tag = getModifierTagFromStack(stack)
        // TODO: Remove this after the update after this update
        if(tag != null && !tag.hasKey(ACTIVE))
            tag.setBoolean(ACTIVE, true)
        if (tag != null && tag.hasKey(GLIDE))
            return tag.getBoolean(GLIDE) && tag.getBoolean("Active")
        false
    }

    /**
      * Write info to tag
      *
      * @return
      */
    def writeToNBT(tag: NBTTagCompound, stack: ItemStack, hasGlide: Boolean): NBTTagCompound = {
        tag.setBoolean(GLIDE, hasGlide)
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
        ArrayBuffer[String](ClientUtils.translate("neotech.text.glide"))

    @ModItem(modid = Reference.MOD_ID)
    class ItemModifierGlide extends BaseUpgradeItem("glide", 1) {

        /**
          * Can this upgrade item allow more to be applied to the item
          *
          * @param stack The stack we want to apply to, get count from there
          * @param count The stack size of the input
          * @return True if there is space for the entire count
          */
        override def canAcceptLevel(stack: ItemStack, count: Int, name: String): Boolean =
            !ModifierGlide.hasGlide(stack)

        /**
          * Use this to put information onto the stack, called when put onto the stack
          *
          * @param stack The stack to put onto
          * @return The tag passed
          */
        override def writeInfoToNBT(stack: ItemStack, tag: NBTTagCompound,  writingStack : ItemStack): Unit = {
            var localTag = ModifierGlide.getModifierTagFromStack(stack)
            if(localTag == null)
                localTag = new NBTTagCompound
            ModifierGlide.writeToNBT(localTag, stack, if(writingStack.stackSize > 0) true else false)
            ModifierGlide.overrideModifierTag(stack, localTag)
        }
    }
}