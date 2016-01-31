package com.dyonovan.neotech.client.gui.machines

import java.awt.Color
import javax.annotation.Nullable

import com.dyonovan.neotech.common.container.machines.ContainerThermalBinder
import com.dyonovan.neotech.common.tiles.machines.TileThermalBinder
import com.dyonovan.neotech.managers.{BlockManager, ItemManager}
import com.dyonovan.neotech.network.{OpenContainerGui, PacketDispatcher, TBStartPacket}
import com.teambr.bookshelf.client.gui.component.BaseComponent
import com.teambr.bookshelf.client.gui.component.control.{GuiComponentButton, GuiComponentSideSelector}
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentArrow, GuiComponentPowerBar, GuiComponentText, GuiTabCollection}
import com.teambr.bookshelf.client.gui.component.listeners.IMouseEventListener
import com.teambr.bookshelf.client.gui.{GuiBase, GuiColor}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.{EnumFacing, StatCollector}

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
            "neotech.thermalbinder.title"){

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
        if(tileEntity != null) {

            val motherBoardTag = new ArrayBuffer[BaseComponent]
            tabs.addTab(motherBoardTag.toList, 100, 100, new Color(0, 155, 0), new ItemStack(ItemManager.upgradeMBFull))

            if(tileEntity.getUpgradeBoard != null && tileEntity.getUpgradeBoard.hasControl) {
                var redstoneTab = new ArrayBuffer[BaseComponent]
                redstoneTab += new GuiComponentText("Redstone Mode", 20, 7)
                redstoneTab += new GuiComponentButton(5, 20, 15, 20, "<") {
                    override def doAction(): Unit = {
                        tileEntity.moveRedstoneMode(-1)
                        tileEntity.sendValueToServer(tileEntity.REDSTONE_FIELD_ID, tileEntity.redstone)
                    }
                }
                redstoneTab += new GuiComponentButton(25, 20, 50, 20, tileEntity.getRedstoneModeName) {
                    override def doAction(): Unit = {}

                    override def renderOverlay(i: Int, j: Int, x : Int, y : Int): Unit = {
                        setText(tileEntity.getRedstoneModeName)
                        super.renderOverlay(i, j, x, y)
                    }
                }
                redstoneTab += new GuiComponentButton(80, 20, 15, 20, ">") {
                    override def doAction(): Unit = {
                        tileEntity.moveRedstoneMode(1)
                        tileEntity.sendValueToServer(tileEntity.REDSTONE_FIELD_ID, tileEntity.redstone)
                    }
                }
                tabs.addTab(redstoneTab.toList, 100, 50, new Color(255, 0, 0), new ItemStack(Items.redstone))
            }

            if (tileEntity.getUpgradeBoard != null && tileEntity.getUpgradeBoard.hasExpansion) {
                val selectorTab = new ArrayBuffer[BaseComponent]
                selectorTab += new GuiComponentText("I/O Mode", 29, 6)
                selectorTab += new GuiComponentSideSelector(20, 20, 40, tileEntity.getWorld.getBlockState(tileEntity.getPos), tileEntity, true) {
                    override def setToggleController(): Unit = {
                        toggleableSidesController = new ToggleableSidesController {

                            override def onSideToggled(side: EnumFacing, modifier: Int): Unit = {
                                tileEntity.setVariable(tileEntity.IO_FIELD_ID, side.ordinal())
                                tileEntity.sendValueToServer(tileEntity.IO_FIELD_ID, side.ordinal())
                                setBlockState(tileEntity.getWorld.getBlockState(tileEntity.getPos))
                            }

                            @Nullable
                            override def getColorForMode(side: EnumFacing): Color = {
                                tileEntity.getColor(tileEntity.getModeForSide(side))
                            }
                        }
                    }
                }
                tabs.addTab(selectorTab.toList, 100, 100, new Color(150, 150, 150), new ItemStack(BlockManager.thermalBinder))
            }

            tabs.getTabs.head.setMouseEventListener(new IMouseEventListener {
                override def onMouseDown(component: BaseComponent, mouseX: Int, mouseY: Int, button: Int): Unit = {
                    PacketDispatcher.net.sendToServer(new OpenContainerGui(tileEntity.getPos, 1))
                }
                override def onMouseDrag(component: BaseComponent, mouseX: Int, mouseY: Int, button: Int, time: Long): Unit = {}
                override def onMouseUp(component: BaseComponent, mouseX: Int, mouseY: Int, button: Int): Unit = {}
            })
        }
    }
}
