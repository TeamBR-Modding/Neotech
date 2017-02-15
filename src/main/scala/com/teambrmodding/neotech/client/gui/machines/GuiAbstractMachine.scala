package com.teambrmodding.neotech.client.gui.machines

import java.awt.Color
import java.lang.Double
import javax.annotation.Nullable

import com.teambrmodding.neotech.common.container.machines.ContainerAbstractMachine
import com.teambrmodding.neotech.common.tiles.AbstractMachine
import com.teambrmodding.neotech.managers.ItemManager
import com.teambr.bookshelf.client.gui.component.BaseComponent
import com.teambr.bookshelf.client.gui.component.control.{GuiComponentButton, GuiComponentSideSelector, GuiComponentTabSlotHolder}
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentLongText, GuiComponentText, GuiTabCollection}
import com.teambr.bookshelf.client.gui.{GuiBase, GuiColor}
import com.teambr.bookshelf.common.tiles.InventoryHandler
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.text.translation.I18n
import net.minecraftforge.items.IItemHandler

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
abstract class GuiAbstractMachine[C <: ContainerAbstractMachine](container : C, x : Int, y : Int, title : String, player: EntityPlayer, var tileEntity: AbstractMachine) extends
        GuiBase[C](container, x, y, title) {

    var lastInventory : InventoryHandler = new InventoryHandler {
        override def getInitialSize = 6
        override def isItemValidForSlot(index: Int, stack: ItemStack): Boolean = true
        override def getVariable(id: Int): Double = 0.0
        override def setVariable(id: Int, value: java.lang.Double): Unit = {}
    }
    lastInventory.copyFrom(tileEntity.upgradeInventory)

    override def drawGuiContainerBackgroundLayer(f: Float, i: Int, j:Int): Unit = {
        if(tileEntity.hasChangedFromLast(lastInventory)) {
            val motherBoardTab = rightTabs.getTabs.head
            rightTabs.getTabs.clear()
            rightTabs.getTabs += motherBoardTab
            addRightTabsLocal(rightTabs, tileEntity, inventory, addUpgradeTab = false)
            leftTabs.getTabs.clear()
            addLeftTabs(leftTabs)
        }
        lastInventory.copyFrom(tileEntity.upgradeInventory)

        super[GuiBase].drawGuiContainerBackgroundLayer(f, i, j)
    }

    override def addRightTabs(tabs : GuiTabCollection) = addRightTabsLocal(tabs, tileEntity, inventory, addUpgradeTab = true)

    override def addLeftTabs(tabs : GuiTabCollection) = {
        val infoTab = new ArrayBuffer[BaseComponent]()
        infoTab += new GuiComponentText(GuiColor.YELLOW + I18n.translateToLocal("neotech.text.information"), 10, 7)
        infoTab += new GuiComponentLongText(10, 20, tileEntity.getDescription, 100, 65, textScale = 50)
        tabs.addReverseTab(infoTab.toList, 120, 100, new Color(130, 0, 0), new ItemStack(tileEntity.getBlockType))
    }

    def addRightTabsLocal(tabs : GuiTabCollection, tileEntity : AbstractMachine, container : ContainerAbstractMachine, addUpgradeTab : Boolean = true): Unit = {
        if (tileEntity != null) {

            // Add Upgrade Tab
            if(addUpgradeTab) {
                val motherBoardTag = new ArrayBuffer[BaseComponent]
                motherBoardTag += new GuiComponentText(GuiColor.ORANGE + I18n.translateToLocal("neotech.text.upgrade"), 26, 6)
                tabs.addTab(motherBoardTag.toList, 100, 65, new Color(0, 155, 0), new ItemStack(ItemManager.processorOctCore))

                var slotID = 0
                val xStart = 25
                val yStart = 20
                for(x <- 0 until 3) {
                    for(y <- 0 until 2) {
                        tabs.getTabs.head.addChild(new GuiComponentTabSlotHolder(xStart + (x * 18), yStart + (y * 18), 18, 18,
                            tabs.getTabs.head, container.upgradeSlots(slotID), 170 + xStart + (x * 18), (yStart + 2) + (y * 18)))
                        slotID += 1
                    }
                }
            }

            // Add Restone Control Tab
            if (tileEntity != null && tileEntity.hasUpgradeByID(IUpgradeItem.REDSTONE_CIRCUIT)) {
                var redstoneTab = new ArrayBuffer[BaseComponent]
                redstoneTab += new GuiComponentText(GuiColor.ORANGE + I18n.translateToLocal("neotech.text.redstoneMode"), 20, 7)
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
                tabs.addTab(redstoneTab.toList, 100, 50, new Color(255, 0, 0), new ItemStack(Items.REDSTONE))
            }

            // Add IO Config tab
            if (tileEntity.shouldHandleIO && tileEntity != null && tileEntity.hasUpgradeByID(IUpgradeItem.NETWORK_CARD)) {
                val selectorTab = new ArrayBuffer[BaseComponent]
                selectorTab += new GuiComponentText(GuiColor.ORANGE + I18n.translateToLocal("neotech.text.ioConfig"), 29, 6)
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
                                tileEntity.getModeForSide(side).getHighlightColor
                            }
                        }
                    }
                }
                tabs.addTab(selectorTab.toList, 100, 100, new Color(150, 150, 150), new ItemStack(tileEntity.getBlockType))
            }
        }
    }
}
