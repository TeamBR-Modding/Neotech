package com.dyonovan.neotech.client.gui.machines

import java.awt.Color

import com.dyonovan.neotech.common.container.machines.ContainerThermalBinder
import com.dyonovan.neotech.common.tiles.machines.TileThermalBinder
import com.dyonovan.neotech.managers.{BlockManager, ItemManager}
import com.dyonovan.neotech.network.{OpenContainerGui, TBStartPacket, PacketDispatcher}
import com.teambr.bookshelf.client.gui.component.BaseComponent
import com.teambr.bookshelf.client.gui.component.control.{GuiComponentTexturedButton, GuiComponentButton}
import com.teambr.bookshelf.client.gui.component.listeners.IMouseEventListener
import com.teambr.bookshelf.client.gui.{GuiColor, GuiBase}
import com.teambr.bookshelf.client.gui.component.display.{GuiTabCollection, GuiComponentArrow, GuiComponentText, GuiComponentPowerBar}
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
                        tileEntity.sendValueToServer(tile.REDSTONE_FIELD_ID, tileEntity.redstone)
                    }
                }
                redstoneTab += new GuiComponentButton(25, 20, 50, 20, tileEntity.getRedstoneModeName) {
                    override def doAction(): Unit = {}

                    override def renderOverlay(i: Int, j: Int): Unit = {
                        setText(tileEntity.getRedstoneModeName)
                        super.renderOverlay(i, j)
                    }
                }
                redstoneTab += new GuiComponentButton(80, 20, 15, 20, ">") {
                    override def doAction(): Unit = {
                        tileEntity.moveRedstoneMode(1)
                        tileEntity.sendValueToServer(tile.REDSTONE_FIELD_ID, tileEntity.redstone)
                    }
                }
                tabs.addTab(redstoneTab.toList, 100, 50, new Color(255, 0, 0), new ItemStack(Items.redstone))
            }

            if(tileEntity.getUpgradeBoard != null && tileEntity.getUpgradeBoard.hasExpansion) {
                val controlTab = new ArrayBuffer[BaseComponent]()
                controlTab += new GuiComponentText("I/O Mode", 29, 6)

                controlTab += new GuiComponentTexturedButton(40, 20,
                    tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.UP))._1,
                    tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.UP))._2,
                    16, 16, 20, 20) {
                    override def doAction(): Unit = {
                        tileEntity.setVariable(tileEntity.IO_FIELD_ID, EnumFacing.UP.ordinal())
                        tileEntity.sendValueToServer(tileEntity.IO_FIELD_ID, EnumFacing.UP.ordinal())
                    }

                    override def render(i : Int, j : Int) = {
                        setUV(tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.UP)))
                        super.render(i, j)
                    }

                    override def getDynamicToolTip(mouseX: Int, mouseY: Int): ArrayBuffer[String] = {
                        val tip = new ArrayBuffer[String]()
                        tip += GuiColor.YELLOW + "Top: " + GuiColor.WHITE + tileEntity.getDisplayNameForIOMode(tileEntity.getModeForSide(EnumFacing.UP))
                        tip
                    }
                }

                controlTab += new GuiComponentTexturedButton(40, 70,
                    tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.DOWN))._1,
                    tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.DOWN))._2,
                    16, 16, 20, 20) {
                    override def doAction(): Unit = {
                        tileEntity.setVariable(tileEntity.IO_FIELD_ID, EnumFacing.DOWN.ordinal())
                        tileEntity.sendValueToServer(tileEntity.IO_FIELD_ID, EnumFacing.DOWN.ordinal())
                    }

                    override def render(i : Int, j : Int) = {
                        setUV(tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.DOWN)))
                        super.render(i, j)
                    }

                    override def getDynamicToolTip(mouseX: Int, mouseY: Int): ArrayBuffer[String] = {
                        val tip = new ArrayBuffer[String]()
                        tip += GuiColor.YELLOW + "Bottom: " + GuiColor.WHITE + tileEntity.getDisplayNameForIOMode(tileEntity.getModeForSide(EnumFacing.DOWN))
                        tip
                    }
                }

                controlTab += new GuiComponentTexturedButton(40, 45,
                    tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.NORTH))._1,
                    tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.NORTH))._2,
                    16, 16, 20, 20) {
                    override def doAction(): Unit = {
                        tileEntity.setVariable(tileEntity.IO_FIELD_ID, EnumFacing.NORTH.ordinal())
                        tileEntity.sendValueToServer(tileEntity.IO_FIELD_ID, EnumFacing.NORTH.ordinal())
                    }

                    override def render(i : Int, j : Int) = {
                        setUV(tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.NORTH)))
                        super.render(i, j)
                    }

                    override def getDynamicToolTip(mouseX: Int, mouseY: Int): ArrayBuffer[String] = {
                        val tip = new ArrayBuffer[String]()
                        tip += GuiColor.YELLOW + "Front: " + GuiColor.WHITE + tileEntity.getDisplayNameForIOMode(tileEntity.getModeForSide(EnumFacing.NORTH))
                        tip
                    }
                }

                controlTab += new GuiComponentTexturedButton(15, 45,
                    tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.EAST))._1,
                    tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.EAST))._2,
                    16, 16, 20, 20) {
                    override def doAction(): Unit = {
                        tileEntity.setVariable(tileEntity.IO_FIELD_ID, EnumFacing.EAST.ordinal())
                        tileEntity.sendValueToServer(tileEntity.IO_FIELD_ID, EnumFacing.EAST.ordinal())
                    }

                    override def render(i : Int, j : Int) = {
                        setUV(tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.EAST)))
                        super.render(i, j)
                    }

                    override def getDynamicToolTip(mouseX: Int, mouseY: Int): ArrayBuffer[String] = {
                        val tip = new ArrayBuffer[String]()
                        tip += GuiColor.YELLOW + "Right Side: " + GuiColor.WHITE + tileEntity.getDisplayNameForIOMode(tileEntity.getModeForSide(EnumFacing.EAST))
                        tip
                    }
                }

                controlTab += new GuiComponentTexturedButton(65, 45,
                    tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.WEST))._1,
                    tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.WEST))._2,
                    16, 16, 20, 20) {
                    override def doAction(): Unit = {
                        tileEntity.setVariable(tileEntity.IO_FIELD_ID, EnumFacing.WEST.ordinal())
                        tileEntity.sendValueToServer(tileEntity.IO_FIELD_ID, EnumFacing.WEST.ordinal())
                    }

                    override def render(i : Int, j : Int) = {
                        setUV(tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.WEST)))
                        super.render(i, j)
                    }

                    override def getDynamicToolTip(mouseX: Int, mouseY: Int): ArrayBuffer[String] = {
                        val tip = new ArrayBuffer[String]()
                        tip += GuiColor.YELLOW + "Left Side: " + GuiColor.WHITE + tileEntity.getDisplayNameForIOMode(tileEntity.getModeForSide(EnumFacing.WEST))
                        tip
                    }
                }

                controlTab += new GuiComponentTexturedButton(65, 70,
                    tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.SOUTH))._1,
                    tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.SOUTH))._2,
                    16, 16, 20, 20) {
                    override def doAction(): Unit = {
                        tileEntity.setVariable(tileEntity.IO_FIELD_ID, EnumFacing.SOUTH.ordinal())
                        tileEntity.sendValueToServer(tileEntity.IO_FIELD_ID, EnumFacing.SOUTH.ordinal())
                    }

                    override def render(i : Int, j : Int) = {
                        setUV(tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.SOUTH)))
                        super.render(i, j)
                    }

                    override def getDynamicToolTip(mouseX: Int, mouseY: Int): ArrayBuffer[String] = {
                        val tip = new ArrayBuffer[String]()
                        tip += GuiColor.YELLOW + "Back: " + GuiColor.WHITE + tileEntity.getDisplayNameForIOMode(tileEntity.getModeForSide(EnumFacing.SOUTH))
                        tip
                    }
                }
                tabs.addTab(controlTab.toList, 100, 100, new Color(0, 0, 255), new ItemStack(BlockManager.thermalBinder))
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
