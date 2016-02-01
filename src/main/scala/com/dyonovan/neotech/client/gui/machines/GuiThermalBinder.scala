package com.dyonovan.neotech.client.gui.machines

import java.awt.Color

import com.dyonovan.neotech.common.container.machines.ContainerThermalBinder
import com.dyonovan.neotech.common.tiles.machines.TileThermalBinder
import com.dyonovan.neotech.network.{PacketDispatcher, TBStartPacket}
import com.teambr.bookshelf.client.gui.component.control.GuiComponentButton
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentArrow, GuiComponentPowerBar, GuiComponentText, GuiTabCollection}
import com.teambr.bookshelf.client.gui.{GuiBase, GuiColor}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.StatCollector

import scala.collection.mutable.ArrayBuffer

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 21, 2015
 */
class GuiThermalBinder (player: EntityPlayer, tileEntity: TileThermalBinder) extends
        GuiBase[ContainerThermalBinder](new ContainerThermalBinder(player.inventory, tileEntity), 175, 185,
            "neotech.thermalbinder.title") {

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
        components += new GuiComponentPowerBar(10, 23, 18, 60, new Color(255, 0, 0)) {
            override def getEnergyPercent(scale: Int): Int = {
                tileEntity.getEnergyStored(null) * scale / tileEntity.getMaxEnergyStored(null)
            }
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(tileEntity.getEnergyStored(null) + " / " + tileEntity.getMaxEnergyStored(null))
            }
        }
        components += new GuiComponentButton(120, 75, 40, 20, "neotech.text.start") {
            override def doAction(): Unit = {
                if (tileEntity.getStackInSlot(tileEntity.MB_INPUT) != null) {
                    PacketDispatcher.net.sendToServer(new TBStartPacket(tileEntity.getPos, tileEntity.getCount))
                }
            }
        }
        components += new GuiComponentArrow(76, 45) {
            override def getCurrentProgress: Int = tileEntity.getCookProgressScaled(24)
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(tileEntity.values.burnTime + " ticks left")
            }
        }
        components += new GuiComponentText(GuiColor.BLACK + StatCollector.translateToLocal("neotech.text.in"), 41, 63)
        components += new GuiComponentText(GuiColor.BLACK + StatCollector.translateToLocal("neotech.text.out"), 123, 63)
        components += new GuiComponentText(GuiColor.BLACK + StatCollector.translateToLocal("neotech.text.upgrade"),
            65, 87)
    }


    override def addRightTabs(tabs : GuiTabCollection) = {
        if (tileEntity != null)
            GuiAbstractMachineHelper.addRightTabs(tabs, tileEntity, inventory)
    }
}
