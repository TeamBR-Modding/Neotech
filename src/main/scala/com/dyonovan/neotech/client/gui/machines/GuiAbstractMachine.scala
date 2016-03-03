package com.dyonovan.neotech.client.gui.machines

import java.awt.Color
import javax.annotation.Nullable

import com.dyonovan.neotech.common.container.machines.ContainerAbstractMachine
import com.dyonovan.neotech.common.tiles.AbstractMachine
import com.dyonovan.neotech.managers.ItemManager
import com.teambr.bookshelf.client.gui.component.BaseComponent
import com.teambr.bookshelf.client.gui.component.control.{GuiComponentButton, GuiComponentSideSelector, GuiComponentTabSlotHolder}
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentLongText, GuiComponentText, GuiTabCollection}
import com.teambr.bookshelf.client.gui.{GuiBase, GuiColor}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.{StatCollector, EnumFacing}

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
abstract class GuiAbstractMachine[C <: ContainerAbstractMachine](container : C, x : Int, y : Int, title : String, player: EntityPlayer, tileEntity: AbstractMachine) extends
        GuiBase[C](container, x, y, title) {

    var hasUpgrade = tileEntity.getUpgradeBoard != null

    override def drawGuiContainerBackgroundLayer(f: Float, i: Int, j:Int): Unit = {
        val oldValue = hasUpgrade
        hasUpgrade = tileEntity.getUpgradeBoard != null

        if(oldValue != hasUpgrade) {
            val motherBoardTab = rightTabs.getTabs.head
            rightTabs.getTabs.clear()
            rightTabs.getTabs += motherBoardTab
            addRightTabs(rightTabs, tileEntity, inventory, updateMotherBoard = false)
            leftTabs.getTabs.clear()
            addLeftTabs(leftTabs)
        }

        super[GuiBase].drawGuiContainerBackgroundLayer(f, i, j)
    }

    override def addRightTabs(tabs : GuiTabCollection) = addRightTabs(tabs, tileEntity, inventory, updateMotherBoard = true)

    override def addLeftTabs(tabs : GuiTabCollection) = {
        val infoTab = new ArrayBuffer[BaseComponent]()
        infoTab += new GuiComponentText(GuiColor.YELLOW + StatCollector.translateToLocal("neotech.text.information"), 10, 7)
        infoTab += new GuiComponentLongText(10, 20, tileEntity.getDescription, 100, 65, textScale = 50)
        tabs.addReverseTab(infoTab.toList, 120, 100, new Color(130, 0, 0), new ItemStack(tileEntity.getBlockType))
    }

    def addRightTabs(tabs : GuiTabCollection, tileEntity : AbstractMachine, container : ContainerAbstractMachine, updateMotherBoard : Boolean = true): Unit = {
        if (tileEntity != null) {

            if(updateMotherBoard) {
                val motherBoardTag = new ArrayBuffer[BaseComponent]
                motherBoardTag += new GuiComponentText(GuiColor.ORANGE + StatCollector.translateToLocal("neotech.text.motherboard"), 26, 6)
                tabs.addTab(motherBoardTag.toList, 100, 65, new Color(0, 155, 0), new ItemStack(ItemManager.upgradeMBFull))

                tabs.getTabs.head.addChild(new GuiComponentTabSlotHolder(41, 25, 18, 18, tabs.getTabs.head, container.motherboardSlot, 170 + 41, 27))
            }
            if (tileEntity.getUpgradeBoard != null && tileEntity.getUpgradeBoard.hasControl) {
                var redstoneTab = new ArrayBuffer[BaseComponent]
                redstoneTab += new GuiComponentText(GuiColor.ORANGE + StatCollector.translateToLocal("neotech.text.redstoneMode"), 20, 7)
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

            if (tileEntity.shouldHandleIO && tileEntity.getUpgradeBoard != null && tileEntity.getUpgradeBoard.hasExpansion) {
                val selectorTab = new ArrayBuffer[BaseComponent]
                selectorTab += new GuiComponentText(GuiColor.ORANGE + StatCollector.translateToLocal("neotech.text.ioConfig"), 29, 6)
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
                selectorTab += new GuiComponentText(GuiColor.BLUE + StatCollector.translateToLocal("neotech.text.blue") + ": " + GuiColor.WHITE + StatCollector.translateToLocal("neotech.text.input"), 10, 90)
                selectorTab += new GuiComponentText(GuiColor.ORANGE + StatCollector.translateToLocal("neotech.text.orange") + ": " + GuiColor.WHITE + StatCollector.translateToLocal("neotech.text.output"), 10, 100)
                selectorTab += new GuiComponentText(GuiColor.GREEN + StatCollector.translateToLocal("neotech.text.green") + ": " + GuiColor.WHITE + StatCollector.translateToLocal("neotech.text.both"), 10, 110)
                tabs.addTab(selectorTab.toList, 100, 125, new Color(150, 150, 150), new ItemStack(tileEntity.getBlockType))
            }
        }
    }
}
