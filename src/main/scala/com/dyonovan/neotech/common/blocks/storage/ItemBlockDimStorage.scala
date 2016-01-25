package com.dyonovan.neotech.common.blocks.storage

import java.text.DecimalFormat

import com.teambr.bookshelf.client.gui.GuiColor
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 1/24/2016
  */
class ItemBlockDimStorage(block: Block) extends ItemBlock(block){

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, list: java.util.List[String], boolean: Boolean): Unit = {
        if (stack.hasTagCompound) {
            val tag = stack.getTagCompound
            val tagStack = ItemStack.loadItemStackFromNBT(tag.getTagList("Items:", 10).getCompoundTagAt(0))
            list.add(GuiColor.ORANGE + tagStack.getDisplayName + ": " + GuiColor.WHITE + tag.getInteger("Qty").toString)
            val total = tag.getInteger("Qty") / tagStack.getMaxStackSize.toFloat
            val format = new DecimalFormat("###.#")
            list.add(format.format(total) + " of 64 Stacks")
        }
    }
}
