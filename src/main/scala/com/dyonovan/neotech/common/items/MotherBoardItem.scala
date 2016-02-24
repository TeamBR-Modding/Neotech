package com.dyonovan.neotech.common.items

import java.util

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.collections.UpgradeBoard
import com.dyonovan.neotech.common.blocks.traits.ThermalBinderItem
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.ItemManager
import com.dyonovan.neotech.tools.ToolHelper.ToolType
import com.dyonovan.neotech.tools.ToolHelper.ToolType.ToolType
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
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
class MotherBoardItem(name: String, maxStackSize: Int, creative: Boolean) extends Item with ThermalBinderItem {

    if (creative)
        setCreativeTab(NeoTech.tabNeoTech)
    setMaxStackSize(maxStackSize)
    setUnlocalizedName(Reference.MOD_ID + ":" + name)

    def getName: String = { name }

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, tooltip: java.util.List[String], advanced: Boolean): Unit = {
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

    override val acceptableUpgrades: util.ArrayList[String] =
        new util.ArrayList[String](util.Arrays.asList(ItemManager.upgradeControl.getUpgradeName,
            ItemManager.upgradeExpansion.getUpgradeName, ItemManager.upgradeHardDrive.getUpgradeName,
            ItemManager.upgradeProcessor.getUpgradeName))

    override def getToolType: ToolType = {
        getName match {
            case ItemManager.upgradeMBEmpty.getName => ToolType.Empty_MB
            case ItemManager.upgradeMBFull.getName => ToolType.Filled_MB
        }
    }
}
