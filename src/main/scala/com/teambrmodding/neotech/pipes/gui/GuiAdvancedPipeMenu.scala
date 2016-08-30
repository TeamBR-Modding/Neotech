package com.teambrmodding.neotech.pipes.gui

import java.awt.Color
import javax.annotation.Nullable

import com.teambrmodding.neotech.managers.ItemManager
import com.teambrmodding.neotech.pipes.container.ContainerAdvancedPipeMenu
import com.teambrmodding.neotech.pipes.tiles.item.ItemInterfacePipe
import com.teambrmodding.neotech.pipes.types.{InterfacePipe, AdvancedPipe}
import com.teambr.bookshelf.client.gui.component.BaseComponent
import com.teambr.bookshelf.client.gui.component.control._
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentLongText, GuiComponentText, GuiTabCollection}
import com.teambr.bookshelf.client.gui.{GuiBase, GuiColor}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.text.translation.I18n

import scala.collection.mutable.ArrayBuffer

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/15/2016
  */
class GuiAdvancedPipeMenu(player : EntityPlayer, tile : AdvancedPipe) extends
        GuiBase[ContainerAdvancedPipeMenu](new ContainerAdvancedPipeMenu(player.inventory, tile), 175, 165, "neotech.interfacePipe.title") {

    var hasUpgrade = tile.getUpgradeBoard != null

    override def drawGuiContainerBackgroundLayer(f: Float, i: Int, j:Int): Unit = {

        val oldValue = hasUpgrade
        hasUpgrade = tile.getUpgradeBoard != null

        if(oldValue != hasUpgrade) {
            val motherBoardTab = rightTabs.getTabs.head
            rightTabs.getTabs.clear()
            rightTabs.getTabs += motherBoardTab
            addRightTabs(rightTabs, tile, inventory, changeMotherboard = false)
            leftTabs.getTabs.clear()
            addLeftTabs(leftTabs, tile, inventory)
        }

        super[GuiBase].drawGuiContainerBackgroundLayer(f, i, j)
    }

    override def addComponents(): Unit = {
        components +=  new GuiComponentButton(if (tile.isInstanceOf[ItemInterfacePipe]) 8 else 61, 45, 50, 20, if (tile.blackList) I18n.translateToLocal("neotech.text.blacklist") else I18n.translateToLocal("neotech.text.whitelist")) {
            override def doAction(): Unit = {
                tile.setVariable(AdvancedPipe.FILTER, AdvancedPipe.FILTER_BLACKLIST)
                tile.sendValueToServer(AdvancedPipe.FILTER, AdvancedPipe.FILTER_BLACKLIST)
            }

            override def renderOverlay(i: Int, j: Int, x: Int, y: Int) = {
                setText(if (tile.blackList) I18n.translateToLocal("neotech.text.blacklist") else I18n.translateToLocal("neotech.text.whitelist"))
                super.renderOverlay(i, j, x, y)
            }
        }

        if (tile.isInstanceOf[ItemInterfacePipe]) {
            components += new GuiComponentCheckBox(8, 70, I18n.translateToLocal("neotech.text.oredict"), tile.matchOreDict) {
                override def setValue(bool: Boolean): Unit = {
                    tile.setVariable(AdvancedPipe.FILTER, AdvancedPipe.FILTER_MATCH_ORE)
                    tile.sendValueToServer(AdvancedPipe.FILTER, AdvancedPipe.FILTER_MATCH_ORE)
                }
            }

            components += new GuiComponentCheckBox(100, 55, I18n.translateToLocal("neotech.text.damage"), tile.matchDamage) {
                override def setValue(bool: Boolean): Unit = {
                    tile.setVariable(AdvancedPipe.FILTER, AdvancedPipe.FILTER_MATCH_DAMAGE)
                    tile.sendValueToServer(AdvancedPipe.FILTER, AdvancedPipe.FILTER_MATCH_DAMAGE)
                }
            }

            components += new GuiComponentCheckBox(100, 70, I18n.translateToLocal("neotech.text.nbt"), tile.matchTag) {
                override def setValue(bool: Boolean): Unit = {
                    tile.setVariable(AdvancedPipe.FILTER, AdvancedPipe.FILTER_MATCH_TAG)
                    tile.sendValueToServer(AdvancedPipe.FILTER, AdvancedPipe.FILTER_MATCH_TAG)
                }
            }
        }
    }


    override def addRightTabs(tabs : GuiTabCollection) = addRightTabs(tabs, tile, inventory, changeMotherboard = true)

    override def addLeftTabs(tabs : GuiTabCollection) = addLeftTabs(tabs, tile, inventory)

    def addRightTabs(tabs : GuiTabCollection, tileEntity : AdvancedPipe, container : ContainerAdvancedPipeMenu, changeMotherboard : Boolean): Unit = {
        if (tileEntity != null) {

            if(changeMotherboard) {
                val motherBoardTag = new ArrayBuffer[BaseComponent]
                motherBoardTag += new GuiComponentText(GuiColor.ORANGE + I18n.translateToLocal("neotech.text.motherboard"), 26, 6)
                tabs.addTab(motherBoardTag.toList, 100, 65, new Color(0, 155, 0), new ItemStack(ItemManager.upgradeMBFull))

                tabs.getTabs.head.addChild(new GuiComponentTabSlotHolder(41, 25, 18, 18, tabs.getTabs.head, container.motherboardSlot, 170 + 41, 27))
            }

            val selectorTab = new ArrayBuffer[BaseComponent]
            selectorTab += new GuiComponentText(GuiColor.ORANGE + I18n.translateToLocal("neotech.text.ioConfig"), 29, 6)
            selectorTab += new GuiComponentSideSelector(15, 20, 40, tileEntity.getWorld.getBlockState(tileEntity.getPos).getBlock.getActualState(tileEntity.getWorld.getBlockState(tileEntity.getPos), tileEntity.getWorld, tileEntity.getPos), tileEntity, true, renderTile = false) {
                override def setToggleController(): Unit = {
                    toggleableSidesController = new ToggleableSidesController {

                        override def onSideToggled(side: EnumFacing, modifier: Int): Unit = {
                            tileEntity.setVariable(AdvancedPipe.IO_FIELD_ID, side.ordinal())
                            tileEntity.sendValueToServer(AdvancedPipe.IO_FIELD_ID, side.ordinal())
                            setBlockState(tileEntity.getWorld.getBlockState(tileEntity.getPos)
                                    .getBlock
                                    .getActualState(
                                        tileEntity.getWorld.getBlockState(tileEntity.getPos),
                                        tileEntity.getWorld,
                                tileEntity.getPos))
                        }

                        @Nullable
                        override def getColorForMode(side: EnumFacing): Color = {
                            tileEntity.getModeForSide(side).getHighlightColor
                        }
                    }
                }
            }
            selectorTab += new GuiComponentText(GuiColor.BLUE + I18n.translateToLocal("neotech.text.blue") + ": " + GuiColor.WHITE + I18n.translateToLocal("neotech.text.insert"), 10, 90)
            selectorTab += new GuiComponentText(GuiColor.ORANGE + I18n.translateToLocal("neotech.text.orange") + ": " + GuiColor.WHITE + I18n.translateToLocal("neotech.text.extract"), 10, 100)
            selectorTab += new GuiComponentText(GuiColor.GREEN + I18n.translateToLocal("neotech.text.green") + ": " + GuiColor.WHITE + I18n.translateToLocal("neotech.text.both"), 10, 110)
            tabs.addTab(selectorTab.toList, 100, 125, new Color(150, 150, 150), new ItemStack(Blocks.PISTON))
        }
    }

    def addLeftTabs(tabs : GuiTabCollection, tileEntity : AdvancedPipe, container : ContainerAdvancedPipeMenu): Unit = {
        if (tileEntity != null) {
            val infoTab = new ArrayBuffer[BaseComponent]()
            infoTab += new GuiComponentText(GuiColor.YELLOW + I18n.translateToLocal("neotech.text.information"), 10, 7)
            infoTab += new GuiComponentLongText(10, 20, tileEntity.asInstanceOf[InterfacePipe[_, _]].getDescription, 100, 65, textScale = 50)
            tabs.addReverseTab(infoTab.toList, 120, 100, new Color(130, 0, 0), new ItemStack(Items.WRITABLE_BOOK))

            if (tileEntity.getUpgradeBoard != null && tileEntity.getUpgradeBoard.hasControl) {
                var redstoneTab = new ArrayBuffer[BaseComponent]
                redstoneTab += new GuiComponentText(GuiColor.BLACK + I18n.translateToLocal("neotech.text.redstoneMode"), 5, 7)
                redstoneTab += new GuiComponentButton(5, 20, 15, 20, "<") {
                    override def doAction(): Unit = {
                        tileEntity.moveRedstoneMode(-1)
                        tileEntity.sendValueToServer(AdvancedPipe.REDSTONE_FIELD_ID, tileEntity.redstone)
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
                        tileEntity.sendValueToServer(AdvancedPipe.REDSTONE_FIELD_ID, tileEntity.redstone)
                    }
                }
                tabs.addReverseTab(redstoneTab.toList, 100, 50, new Color(255, 0, 0), new ItemStack(Items.REDSTONE))

                var frequencyTab = new ArrayBuffer[BaseComponent]
                frequencyTab += new GuiComponentText(GuiColor.BLACK + I18n.translateToLocal("neotech.text.frequency"), 7, 7)

                frequencyTab += new GuiComponentSetNumber(20, 20, 60, tile.frequency, 0, 100) {
                    override def setValue(i: Int): Unit = {
                        tile.setVariable(AdvancedPipe.FREQUENCY, if(i < 0) 0 else if(i > 100) 100 else i)
                        tile.sendValueToServer(AdvancedPipe.FREQUENCY, tile.getVariable(AdvancedPipe.FREQUENCY))
                    }
                }

                tabs.addReverseTab(frequencyTab.toList, 100, 50, new Color(255, 153, 0), new ItemStack(Items.PAPER))
            }

            if (tileEntity.getUpgradeBoard != null && tileEntity.getUpgradeBoard.hasExpansion) {

                var extractionMode = new ArrayBuffer[BaseComponent]
                extractionMode += new GuiComponentText(GuiColor.BLACK + I18n.translateToLocal("neotech.text.extractionMode"), 7, 6)

                extractionMode += new GuiComponentButton(5, 20, 15, 20, "<") {
                    override def doAction(): Unit = {
                        tile.moveMode(-1)
                        tile.sendValueToServer(AdvancedPipe.MODE_FIELD_ID, tile.mode)
                    }
                }
                extractionMode += new GuiComponentButton(25, 20, 100, 20, tile.getModeName) {
                    override def doAction(): Unit = {}
                    override def renderOverlay(i : Int, j : Int, x : Int, y : Int) : Unit = {
                        setText(tile.getModeName)
                        super.renderOverlay(i, j, x, y)
                    }
                }
                extractionMode += new GuiComponentButton(130, 20, 15, 20, ">") {
                    override def doAction(): Unit = {
                        tile.moveMode(1)
                        tile.sendValueToServer(AdvancedPipe.MODE_FIELD_ID, tile.mode)
                    }
                }
                tabs.addReverseTab(extractionMode.toList, 150, 50, new Color(255, 255, 0), new ItemStack(Items.BOOK))
            }
        }
    }
}