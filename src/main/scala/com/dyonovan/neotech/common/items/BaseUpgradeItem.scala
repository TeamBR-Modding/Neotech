package com.dyonovan.neotech.common.items

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.collections.UpgradeBoard
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.ItemManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemStack, Item}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 20, 2015
 */
class BaseUpgradeItem(name: String, maxStackSize: Int, creative: Boolean) extends Item {

    if (creative)
        setCreativeTab(NeoTech.tabNeoTech)
    setMaxStackSize(maxStackSize)
    setUnlocalizedName(Reference.MOD_ID + ":" + name)

    def getName: String = { name }

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, tooltip: java.util.List[_], advanced: Boolean): Unit = {
        //super.addInformation(stack, player, tooltip, advanced)
        stack.getItem match {
            case ItemManager.upgradeMBFull =>
                val mb = UpgradeBoard.getBoardFromStack(stack)
                tooltip.asInstanceOf[java.util.List[String]].add("Has Control: " + mb.hasControl)
                tooltip.asInstanceOf[java.util.List[String]].add("Has Expansion: " + mb.hasExpansion)
                tooltip.asInstanceOf[java.util.List[String]].add("HardDrives: " + mb.getHardDriveCount)
                tooltip.asInstanceOf[java.util.List[String]].add("Processors: " + mb.getProcessorCount)
            case _ =>
        }
    }
}
