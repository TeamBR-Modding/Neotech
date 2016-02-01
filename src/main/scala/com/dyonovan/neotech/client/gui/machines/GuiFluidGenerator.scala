package com.dyonovan.neotech.client.gui.machines

import java.awt.Color
import javax.annotation.Nullable

import com.dyonovan.neotech.common.container.machines.{ContainerFluidGenerator, ContainerAbstractMachine}
import com.dyonovan.neotech.common.tiles.machines.{TileElectricCrusher, TileFluidGenerator}
import com.dyonovan.neotech.managers.{BlockManager, ItemManager}
import com.dyonovan.neotech.network.{OpenContainerGui, PacketDispatcher}
import com.teambr.bookshelf.client.gui.component.BaseComponent
import com.teambr.bookshelf.client.gui.component.control.{GuiComponentSideSelector, GuiComponentTexturedButton, GuiComponentButton}
import com.teambr.bookshelf.client.gui.component.listeners.IMouseEventListener
import com.teambr.bookshelf.client.gui.{GuiColor, GuiBase}
import com.teambr.bookshelf.client.gui.component.display._
import com.teambr.bookshelf.common.container.ContainerGeneric
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
 * @since August 21, 2015
 */
class GuiFluidGenerator(player: EntityPlayer, tileEntity: TileFluidGenerator) extends
        GuiBase[ContainerFluidGenerator](new ContainerFluidGenerator(player.inventory, tileEntity), 175, 165, "neotech.fluidgenerator.title") {

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

        //Flame for Burning
        components += new GuiComponentFlame(78, 55) {
            override def getCurrentBurn: Int = if (tileEntity.isActive) tileEntity.getBurnProgressScaled(14) else 0

            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(tileEntity.burnTime + " ticks left.")
            }
        }

        //Power Bar
        components += new GuiComponentPowerBar(150, 18, 18, 60, new Color(255, 0, 0)) {
            override def getEnergyPercent(scale: Int): Int = {
                tileEntity.getEnergyStored(null) * scale / tileEntity.getMaxEnergyStored(null)
            }
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(tileEntity.getEnergyStored(null) + " / " + tileEntity.getMaxEnergyStored(null))
            }
        }

        //Current Production
        components += new GuiComponentText(GuiColor.RED + "RF/t = " + tileEntity.getEnergyProduced, 64, 18) {
            override def renderOverlay(i : Int, j : Int, x : Int, y : Int) = {
                setText(GuiColor.RED + "RF/t = " + tileEntity.getEnergyProduced)
                super.renderOverlay(i, j, x, y)
            }
        }

        //Stored Fluid
        components += new GuiComponentFluidTank(7, 18, 18, 60, tileEntity.tank) {
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(tileEntity.tank.getFluidAmount + "/" + tileEntity.tank.getCapacity + " mb")
            }
        }
    }

    override def addRightTabs(tabs : GuiTabCollection) = {
        if (tileEntity != null)
            GuiAbstractMachineHelper.addRightTabs(tabs, tileEntity, inventory)
    }
}
