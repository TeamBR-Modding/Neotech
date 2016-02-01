package com.dyonovan.neotech.client.gui.machines

import java.awt.Color
import javax.annotation.Nullable

import com.dyonovan.neotech.common.container.machines.ContainerElectricFurnace
import com.dyonovan.neotech.common.tiles.machines.TileElectricFurnace
import com.dyonovan.neotech.managers.{BlockManager, ItemManager}
import com.dyonovan.neotech.network.{OpenContainerGui, PacketDispatcher}
import com.teambr.bookshelf.client.gui.component.BaseComponent
import com.teambr.bookshelf.client.gui.component.control.{GuiComponentSideSelector, GuiComponentButton, GuiComponentTexturedButton}
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentArrow, GuiComponentPowerBar, GuiComponentText, GuiTabCollection}
import com.teambr.bookshelf.client.gui.component.listeners.IMouseEventListener
import com.teambr.bookshelf.client.gui.{GuiBase, GuiColor}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

import scala.collection.mutable.ArrayBuffer

class GuiElectricFurnace(player: EntityPlayer, tileEntity: TileElectricFurnace) extends
        GuiBase[ContainerElectricFurnace](new ContainerElectricFurnace(player.inventory, tileEntity), 175, 165,
            "neotech.furnace.title") {

    protected var tile = tileEntity

    var hasUpgrade = tileEntity.getUpgradeBoard != null

    override def drawGuiContainerBackgroundLayer(f: Float, i: Int, j: Int): Unit = {
        tile = tile.getWorld.getTileEntity(tile.getPos).asInstanceOf[TileElectricFurnace]

        val oldValue = hasUpgrade
        hasUpgrade = tile.getUpgradeBoard != null

        if(oldValue != hasUpgrade) {
            val motherBoardTab = rightTabs.getTabs.head
            rightTabs.getTabs.clear()
            rightTabs.getTabs += motherBoardTab
            GuiAbstractMachineHelper.updateRightTabs(rightTabs, tileEntity, inventory)
        }

        super[GuiBase].drawGuiContainerBackgroundLayer(f, i, j)
    }

    override def addComponents(): Unit = {
        components += new GuiComponentArrow(81, 35) {
            override def getCurrentProgress: Int = tile.getCookProgressScaled(24)
        }
        components += new GuiComponentPowerBar(20, 18, 18, 60, new Color(255, 0, 0)) {
            override def getEnergyPercent(scale: Int): Int = {
                tile.getEnergyStored(null) * scale / tile.getMaxEnergyStored(null)
            }

            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(tile.getEnergyStored(null) + " / " + tile.getMaxEnergyStored(null))
            }
        }
    }


    override def addRightTabs(tabs : GuiTabCollection) = {
        if (tileEntity != null)
            GuiAbstractMachineHelper.addRightTabs(tabs, tileEntity, inventory)
    }
}

