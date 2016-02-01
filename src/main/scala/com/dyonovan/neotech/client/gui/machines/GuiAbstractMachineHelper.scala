package com.dyonovan.neotech.client.gui.machines

import java.awt.Color
import javax.annotation.Nullable

import com.dyonovan.neotech.common.container.machines.ContainerAbstractMachine
import com.dyonovan.neotech.common.tiles.AbstractMachine
import com.dyonovan.neotech.managers.{BlockManager, ItemManager}
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.client.gui.component.BaseComponent
import com.teambr.bookshelf.client.gui.component.control.{GuiComponentTabSlotHolder, GuiComponentButton, GuiComponentSideSelector}
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentText, GuiTabCollection}
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
  * @author Paul Davis <pauljoda>
  * @since 1/31/2016
  */
object GuiAbstractMachineHelper {
    def addRightTabs(tabs : GuiTabCollection, tileEntity : AbstractMachine, container : ContainerAbstractMachine): Unit = {
        if (tileEntity != null) {

            val motherBoardTag = new ArrayBuffer[BaseComponent]
            motherBoardTag += new GuiComponentText(GuiColor.ORANGE + "Motherboard", 26, 6)
            tabs.addTab(motherBoardTag.toList, 100, 65, new Color(0, 155, 0), new ItemStack(ItemManager.upgradeMBFull))

            tabs.getTabs.head.addChild(new GuiComponentTabSlotHolder(41, 25, 18, 18, tabs.getTabs.head, container.motherboardSlot,  170 + 41, 27))

            if (tileEntity.getUpgradeBoard != null && tileEntity.getUpgradeBoard.hasControl) {
                var redstoneTab = new ArrayBuffer[BaseComponent]
                redstoneTab += new GuiComponentText(GuiColor.ORANGE + "Redstone Mode", 20, 7)
                redstoneTab += new GuiComponentButton(5, 20, 15, 20, "<") {
                    override def doAction(): Unit = {
                        tileEntity.moveRedstoneMode(-1)
                        tileEntity.sendValueToServer(tileEntity.REDSTONE_FIELD_ID, tileEntity.redstone)
                    }
                }
                redstoneTab += new GuiComponentButton(25, 20, 50, 20, tileEntity.getRedstoneModeName) {
                    override def doAction(): Unit = {}

                    override def renderOverlay(i: Int, j: Int, x: Int, y: Int): Unit = {
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
                selectorTab += new GuiComponentText(GuiColor.ORANGE + "I/O Config", 29, 6)
                selectorTab += new GuiComponentSideSelector(15, 20, 40, tileEntity.getWorld.getBlockState(tileEntity.getPos), tileEntity, true) {
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
                selectorTab += new GuiComponentText(GuiColor.ORANGE + "Orange: " + GuiColor.WHITE + "Output", 10, 100)
                selectorTab += new GuiComponentText(GuiColor.BLUE + "Blue: " + GuiColor.WHITE + "Input", 10, 90)
                selectorTab += new GuiComponentText(GuiColor.GREEN + "Green: " + GuiColor.WHITE + "Both", 10, 110)
                tabs.addTab(selectorTab.toList, 100, 125, new Color(150, 150, 150), new ItemStack(BlockManager.electricCrusher))
            }
        }
    }

    def updateRightTabs(tabs : GuiTabCollection, tileEntity : AbstractMachine, container : ContainerAbstractMachine): Unit = {
        if (tileEntity != null) {

            if (tileEntity.getUpgradeBoard != null && tileEntity.getUpgradeBoard.hasControl) {
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

                    override def renderOverlay(i: Int, j: Int, x: Int, y: Int): Unit = {
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
                tabs.addTab(selectorTab.toList, 100, 100, new Color(150, 150, 150), new ItemStack(BlockManager.electricCrusher))
            }
        }
    }
}
