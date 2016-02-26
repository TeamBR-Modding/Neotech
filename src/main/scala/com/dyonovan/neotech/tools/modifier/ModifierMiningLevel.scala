package com.dyonovan.neotech.tools.modifier

import com.teambr.bookshelf.client.gui.GuiTextFormat
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
  * @since 2/23/2016
  */
object ModifierMiningLevel extends Modifier("miningLevel") {

    lazy val LEVEL = "MiningLevel"

    /**
      * Allows you to specify a specific texture based on the stack, this probably won't be used often but its there
      *
      * @return The texture location, defaulted to the identifier (this should be the standard)
      */
    override def textureLocation(stack : ItemStack, tagCompound : NBTTagCompound) = {
        var baseName = super.textureLocation(stack, tagCompound)
        val level = tagCompound.getTag(LEVEL)
        baseName += level.toString
        baseName
    }

    /**
      * Used to get the mining level for the stack
      *
      * @param stack
      * @return
      */
    def getMiningLevel(stack : ItemStack) : Int = {
        val tag = getModifierTagFromStack(stack)
        if(tag != null) {
            return tag.getInteger(LEVEL)
        }
        1
    }

    /**
      * Used to get the level for this modifier
      *
      * @param tag The tag that the level is stored on
      * @return The level
      */
    override def getLevel(tag : NBTTagCompound) = if(tag.hasKey(LEVEL) && tag.getInteger(LEVEL) != 1) 1 else 0

    /**
      * Writes the info to the tag, store things you need here
      *
      * @param tag The incoming tag compound
      */
    def writeToNBT(tag: NBTTagCompound, stack : ItemStack, miningLevel : Int): NBTTagCompound = {
        tag.setInteger(LEVEL, miningLevel)
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
        val  harvestLevel = tag.getInteger(LEVEL)
        var strLevel = ""
        harvestLevel match {
            case 4 => strLevel = "Cobalt"
            case 3 => strLevel = "Obsidian"
            case 2 => strLevel = "Redstone"
            case 1 => strLevel = "Stone"
            case _ =>
        }
        ArrayBuffer("Mining Level: " + GuiTextFormat.ITALICS + strLevel)
    }
}
