package com.dyonovan.neotech.tools.modifier

import com.teambr.bookshelf.traits.NBTSavable
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
class Modifier(var name : String) extends NBTSavable {

    def getIdentifier = name

    def textureLocation = "electric_pickaxe_mod_diamond"

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        val modifierList = new NBTTagCompound
        modifierList.setString("ModifierID", name)
        tag.setTag("ModifierTag", modifierList)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        val modifierList = tag.getCompoundTag("ModifierTag")
        name = modifierList.getString("ModifierID")
    }
}
