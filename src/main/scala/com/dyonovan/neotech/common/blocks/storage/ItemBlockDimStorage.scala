package com.dyonovan.neotech.common.blocks.storage

import java.text.DecimalFormat

import com.teambr.bookshelf.client.gui.{GuiColor, GuiTextFormat}
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.input.Keyboard

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
class ItemBlockDimStorage(block: Block) extends ItemBlock(block) {

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, list: java.util.List[String], boolean: Boolean): Unit = {
        if (stack.hasTagCompound) {
            var tagItem: ItemStack = null
            var tagUpgrade: ItemStack = null
            var hdCount = 0
            val tag = stack.getTagCompound
            if (tag.hasKey("Items:")) tagItem = ItemStack.loadItemStackFromNBT(tag.getTagList("Items:", 10).getCompoundTagAt(0))
            if (tag.hasKey("Items:upgrade")) tagUpgrade = ItemStack.loadItemStackFromNBT(tag.getTagList("Items:upgrade", 10).getCompoundTagAt(0))
            if (tagUpgrade != null) {
                if (tagUpgrade.getTagCompound.hasKey("HardDrive")) {
                    hdCount = tagUpgrade.getTagCompound.getInteger("HardDrive")
                }
            }
            if (tagItem != null)
                list.add(GuiColor.ORANGE + tagItem.getDisplayName + ": " + GuiColor.WHITE + tag.getInteger("Qty").toString)
            else list.add(GuiColor.WHITE.ordinal() + GuiTextFormat.ITALICS.ordinal() + "Empty")
            val total = tag.getInteger("Qty") / (tagItem.getMaxStackSize * (hdCount + 1)).toFloat
            val format = new DecimalFormat("###.#")
            list.add(format.format(total) + " of " + (64 * (hdCount + 1)).toString + " Stacks")
            if (tagUpgrade != null) {
                if (!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
                    list.add(GuiTextFormat.ITALICS + "Press Left-CTRL for Currently Installed Upgrades")
                else
                    list.add(GuiColor.GREEN + "Hard Drives: " + hdCount)
            }
        }
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            list.add(GuiTextFormat.ITALICS + "Press Left Shift for Upgrade Information")
        else {
            list.add(GuiColor.GREEN + "Hard Drives (Max 8)")
            list.add(GuiColor.WHITE + "      Increases storage by 64 Stacks per Upgrade")
            list.add(GuiColor.GREEN + "Expansion Upgrade (Max 1)")
            list.add(GuiColor.WHITE + "      Turns the Storage Crate into a Cobblegen")
            list.add(GuiColor.WHITE + "      Requires a Lava Block and Water Block Beside It")
        }
    }
}
