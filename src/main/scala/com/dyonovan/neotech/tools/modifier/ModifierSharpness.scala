package com.dyonovan.neotech.tools.modifier

import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.tools.ToolHelper
import com.dyonovan.neotech.tools.upgradeitems.BaseUpgradeItem
import com.teambr.bookshelf.annotations.ModItem
import net.minecraft.init.Enchantments
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
  * @author Paul Davis <pauljoda>
  * @since 2/24/2016
  */
object ModifierSharpness extends Modifier("sharpness") {
    lazy val SHARPNESS = "Sharpness"

    /**
      * Get the sharpness level
      */
    def getSharpnessLevel(stack : ItemStack) : Int = {
        val tag = getModifierTagFromStack(stack)
        if(tag != null && tag.hasKey(SHARPNESS))
            return tag.getInteger(SHARPNESS)
        0
    }

    /**
      * Used to get the level for this modifier
      *
      * @param tag The tag that the level is stored on
      * @return The level
      */
    override def getLevel(tag : NBTTagCompound) = tag.getInteger(SHARPNESS)

    /**
      * Write info to the tag
      */
    def writeToNBT(tag: NBTTagCompound, stack: ItemStack, count: Int): NBTTagCompound = {
        ToolHelper.writeVanillaEnchantment(tag, stack, Enchantments.SHARPNESS, getSharpnessLevel(stack) + count)
        tag.setInteger(SHARPNESS, getSharpnessLevel(stack) + count)
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
        new ArrayBuffer[String]() //Vanilla handles this

    @ModItem(modid = Reference.MOD_ID)
    class ItemModifierSharpness extends BaseUpgradeItem("sharpness", 10) {

        /**
          * Can this upgrade item allow more to be applied to the item
          *
          * @param stack The stack we want to apply to, get count from there
          * @param count The stack size of the input
          * @return True if there is space for the entire count
          */
        override def canAcceptLevel(stack: ItemStack, count: Int, name: String): Boolean =
            ModifierSharpness.getSharpnessLevel(stack) + count <= maxStackSize

        /**
          * Use this to put information onto the stack, called when put onto the stack
          *
          * @param stack The stack to put onto
          * @return The tag passed
          */
        override def writeInfoToNBT(stack: ItemStack, tag: NBTTagCompound,  writingStack : ItemStack): Unit = {
            var localTag = ModifierSharpness.getModifierTagFromStack(stack)
            if(localTag == null)
                localTag = new NBTTagCompound
            ModifierSharpness.writeToNBT(localTag, stack, writingStack.stackSize)
            ModifierSharpness.overrideModifierTag(stack, localTag)
        }
    }
}
