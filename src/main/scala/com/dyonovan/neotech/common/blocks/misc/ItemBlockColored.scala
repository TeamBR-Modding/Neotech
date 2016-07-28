package com.dyonovan.neotech.common.blocks.misc


import net.minecraft.block.Block
import net.minecraft.item.{EnumDyeColor, ItemStack, ItemBlock}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis pauljoda
  * @since August 20, 2015
  */
class ItemBlockColored(block : Block) extends ItemBlock(block) {
    setMaxDamage(0)
    setHasSubtypes(true)

    override def getMetadata(meta : Int) = meta

    override def getUnlocalizedName(stack : ItemStack) = {
        val color = EnumDyeColor.byMetadata(stack.getMetadata)
        super.getUnlocalizedName + "_" + color.toString
    }
}