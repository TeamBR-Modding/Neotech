package com.dyonovan.neotech.client.gui.machines

import java.awt.Color
import javax.annotation.Nullable

import com.dyonovan.neotech.common.container.machines.ContainerFurnaceGenerator
import com.dyonovan.neotech.common.tiles.machines.TileFurnaceGenerator
import com.dyonovan.neotech.managers.{BlockManager, ItemManager}
import com.dyonovan.neotech.network.{OpenContainerGui, PacketDispatcher}
import com.teambr.bookshelf.client.gui.component.BaseComponent
import com.teambr.bookshelf.client.gui.component.control.{GuiComponentButton, GuiComponentSideSelector}
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentFlame, GuiComponentPowerBar, GuiComponentText, GuiTabCollection}
import com.teambr.bookshelf.client.gui.component.listeners.IMouseEventListener
import com.teambr.bookshelf.client.gui.{GuiBase, GuiColor}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

import scala.collection.mutable.ArrayBuffer

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 14, 2015
 */
class GuiFurnaceGenerator(player: EntityPlayer, tileEntity: TileFurnaceGenerator) extends
        GuiBase[ContainerFurnaceGenerator](new ContainerFurnaceGenerator(player.inventory, tileEntity), 175, 165,
            "neotech.furnacegenerator.title") {

    var hasUpgrade = tileEntity.getUpgradeBoard != null

    override def drawGuiContainerBackgroundLayer(f: Float, i: Int, j:Int): Unit = {
        val oldValue = hasUpgrade
        hasUpgrade = tileEntity.getUpgradeBoard != null

        if(oldValue != hasUpgrade) {
            val motherBoardTab = rightTabs.getTabs.head
            rightTabs.getTabs.clear()
            rightTabs.getTabs += motherBoardTab
            GuiAbstractMachineHelper.updateRightTabs(rightTabs, tileEntity, inventory)
        }

        super[GuiBase].drawGuiContainerBackgroundLayer(f, i, j)
    }

    override def addComponents(): Unit = {
        components += new GuiComponentFlame(78, 55) {
            override def getCurrentBurn: Int = if (tileEntity.isBurning) tileEntity.getBurnTimeRemainingScaled(14) else 0

            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(tileEntity.values.burnTime + " ticks left.")
            }
        }
        components += new GuiComponentPowerBar(20, 18, 18, 60, new Color(255, 0, 0)) {
            override def getEnergyPercent(scale: Int): Int = {
                tileEntity.getEnergyStored(null) * scale / tileEntity.getMaxEnergyStored(null)
            }
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(tileEntity.getEnergyStored(null) + " / " + tileEntity.getMaxEnergyStored(null))
            }
        }
        components += new GuiComponentText(GuiColor.RED + "RF/t = " + tileEntity.RF_TICK, 64, 18)
    }


    override def addRightTabs(tabs : GuiTabCollection) = {
        if (tileEntity != null)
            GuiAbstractMachineHelper.addRightTabs(tabs, tileEntity, inventory)
    }
}
