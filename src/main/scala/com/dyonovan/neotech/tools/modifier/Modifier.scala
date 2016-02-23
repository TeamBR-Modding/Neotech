package com.dyonovan.neotech.tools.modifier

import com.dyonovan.neotech.tools.ToolHelper
import com.dyonovan.neotech.tools.tools.BaseElectricTool
import com.dyonovan.neotech.utils.ClientUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/22/2016
  */
abstract class Modifier(var name : String) {

    /**
      * The name of this modifier. This should be standardized to be the name for everything, id, texture mod, etc
      *
      * @return The name of the modifier eg. "fortify"
      */
    def getIdentifier = name

    /**
      * Allows you to specify a specific texture based on the stack, this probably won't be used often but its there
      *
      * @return The texture location, defaulted to the identifier (this should be the standard)
      */
    def textureLocation(stack : ItemStack, tag : NBTTagCompound) = ClientUtils.prefixResource("items/tools/"
            + stack.getItem.asInstanceOf[BaseElectricTool].getToolName + "/" + getIdentifier, doLowerCase = false)

    /**
      * Used to get the modifier tag compound from the stack
      *
      * @param stack The stack in
      * @return The tag that modifier has written
      */
    def getModifierTagFromStack(stack : ItemStack) : NBTTagCompound = {
        if(stack.hasTagCompound && stack.getTagCompound.hasKey(ToolHelper.ModifierListTag)) {
            val tagList = stack.getTagCompound.getTagList(ToolHelper.ModifierListTag, 10) // Grab the tag list
            for(x <- 0 until tagList.tagCount()) { // Iterate the list
            val tag = tagList.get(x) // Get tag at location
                tag match { // Check it matches
                    case compound: NBTTagCompound if compound.getString("ModifierID").equalsIgnoreCase(name) =>
                        return compound // Return tag, we found it
                    case _ =>
                }
            }
        }
        null // Found nothing
    }

    /**
      * Writes the info to the tag, store things you need here
      *
      * @param tag The incoming tag compound
      */
    def writeToNBT(tag: NBTTagCompound, stack : ItemStack): NBTTagCompound = {
        tag.setString("ModifierID", name)
        tag.setString("TextureLocation", textureLocation(stack, tag))
        tag
    }
}
