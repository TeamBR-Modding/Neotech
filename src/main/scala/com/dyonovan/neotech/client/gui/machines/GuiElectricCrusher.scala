package com.dyonovan.neotech.client.gui.machines

import java.awt.Color
import javax.annotation.Nullable

import com.dyonovan.neotech.client.gui.GuiComponentSideSelectorTemp
import com.dyonovan.neotech.common.container.machines.ContainerElectricCrusher
import com.dyonovan.neotech.common.tiles.machines.TileElectricCrusher
import com.dyonovan.neotech.managers.ItemManager
import com.dyonovan.neotech.network.{OpenContainerGui, PacketDispatcher}
import com.dyonovan.neotech.pipes.types.AdvancedPipe
import com.teambr.bookshelf.client.gui.GuiBase
import com.teambr.bookshelf.client.gui.component.BaseComponent
import com.teambr.bookshelf.client.gui.component.control.GuiComponentButton
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentArrow, GuiComponentPowerBar, GuiComponentText, GuiTabCollection}
import com.teambr.bookshelf.client.gui.component.listeners.IMouseEventListener
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.{Blocks, Items}
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
  * @since August 13, 2015
  */
class GuiElectricCrusher(player: EntityPlayer, tileEntity: TileElectricCrusher) extends
        GuiBase[ContainerElectricCrusher](new ContainerElectricCrusher(player.inventory, tileEntity), 175, 165,
            "neotech.crusher.title"){

    protected var tile = tileEntity

    override def drawGuiContainerBackgroundLayer(f: Float, i: Int, j:Int): Unit = {
        tile = tile.getWorld.getTileEntity(tile.getPos).asInstanceOf[TileElectricCrusher]
        super[GuiBase].drawGuiContainerBackgroundLayer(f, i, j)
    }

    override def addComponents(): Unit = {
        components += new GuiComponentArrow(64, 34) {
            override def getCurrentProgress: Int = tile.getCookProgressScaled(24)
        }
        components += new GuiComponentPowerBar(14, 18, 18, 60, new Color(255, 0, 0)) {
            override def getEnergyPercent(scale: Int): Int = {
                tile.getEnergyStored(null) * scale / tile.getMaxEnergyStored(null)
            }
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(tile.getEnergyStored(null) + " / " + tile.getMaxEnergyStored(null))
            }
        }
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

            /*if(tileEntity.getUpgradeBoard != null && tileEntity.getUpgradeBoard.hasExpansion) {
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

                    override def render(i : Int, j : Int, x : Int, y : Int) = {
                        setUV(tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.UP)))
                        super.render(i, j, x, y)
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

                    override def render(i : Int, j : Int, x : Int, y : Int) = {
                        setUV(tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.DOWN)))
                        super.render(i, j, x, y)
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

                    override def render(i : Int, j : Int, x : Int, y : Int) = {
                        setUV(tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.NORTH)))
                        super.render(i, j, x, y)
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

                    override def render(i : Int, j : Int, x : Int, y : Int) = {
                        setUV(tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.EAST)))
                        super.render(i, j, x, y)
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

                    override def render(i : Int, j : Int, x : Int, y : Int) = {
                        setUV(tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.WEST)))
                        super.render(i, j, x, y)
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

                    override def render(i : Int, j : Int, x : Int, y : Int) = {
                        setUV(tileEntity.getUVForMode(tileEntity.getModeForSide(EnumFacing.SOUTH)))
                        super.render(i, j, x, y)
                    }

                    override def getDynamicToolTip(mouseX: Int, mouseY: Int): ArrayBuffer[String] = {
                        val tip = new ArrayBuffer[String]()
                        tip += GuiColor.YELLOW + "Back: " + GuiColor.WHITE + tileEntity.getDisplayNameForIOMode(tileEntity.getModeForSide(EnumFacing.SOUTH))
                        tip
                    }
                }
                tabs.addTab(controlTab.toList, 100, 100, new Color(0, 0, 255), new ItemStack(BlockManager.electricCrusher))
            }

            tabs.getTabs.head.setMouseEventListener(new IMouseEventListener {
                override def onMouseDown(component: BaseComponent, mouseX: Int, mouseY: Int, button: Int): Unit = {
                    PacketDispatcher.net.sendToServer(new OpenContainerGui(tileEntity.getPos, 1))
                }
                override def onMouseDrag(component: BaseComponent, mouseX: Int, mouseY: Int, button: Int, time: Long): Unit = {}
                override def onMouseUp(component: BaseComponent, mouseX: Int, mouseY: Int, button: Int): Unit = {}
            })*/

            val selectorTab = new ArrayBuffer[BaseComponent]
            selectorTab += new GuiComponentSideSelectorTemp(20, 20, 40, tileEntity.getWorld.getBlockState(tileEntity.getPos), tileEntity, true) {
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
            tabs.addTab(selectorTab.toList, 100, 100, new Color(255, 255, 255), new ItemStack(Blocks.piston))
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
