package com.dyonovan.neotech.pipes.gui

import java.awt.Color
import javax.annotation.Nullable

import com.dyonovan.neotech.client.gui.machines.GuiAbstractMachine
import com.dyonovan.neotech.common.container.machines.ContainerAbstractMachine
import com.dyonovan.neotech.common.tiles.machines.processors.TileElectricCrusher
import com.dyonovan.neotech.managers.{BlockManager, ItemManager}
import com.dyonovan.neotech.pipes.container.ContainerAdvancedPipeMenu
import com.dyonovan.neotech.pipes.tiles.item.ItemInterfacePipe
import com.dyonovan.neotech.pipes.types.AdvancedPipe
import com.teambr.bookshelf.client.gui.component.BaseComponent
import com.teambr.bookshelf.client.gui.component.control._
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentText, GuiTabCollection}
import com.teambr.bookshelf.client.gui.{GuiBase, GuiColor, GuiTextFormat}
import com.teambr.bookshelf.common.container.slots.PhantomSlot
import net.minecraft.client.Minecraft
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
  * @author Paul Davis <pauljoda>
  * @since 1/15/2016
  */
class GuiAdvancedPipeMenu(player : EntityPlayer, tile : AdvancedPipe) extends
        GuiBase[ContainerAdvancedPipeMenu](new ContainerAdvancedPipeMenu(player.inventory, tile), 175, 165, "Interface Pipe") {

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
        components +=  new GuiComponentButton(if (tile.isInstanceOf[ItemInterfacePipe]) 8 else 61, 45, 50, 20, if (tile.blackList) "Blacklist" else "Whitelist") {
            override def doAction(): Unit = {
                tile.setVariable(AdvancedPipe.FILTER, AdvancedPipe.FILTER_BLACKLIST)
                tile.sendValueToServer(AdvancedPipe.FILTER, AdvancedPipe.FILTER_BLACKLIST)
            }

            override def renderOverlay(i: Int, j: Int, x: Int, y: Int) = {
                setText(if (tile.blackList) "Blacklist" else "Whitelist")
                super.renderOverlay(i, j, x, y)
            }
        }

        if (tile.isInstanceOf[ItemInterfacePipe]) {
            components += new GuiComponentCheckBox(8, 70, "Ore Dict", tile.matchOreDict) {
                override def setValue(bool: Boolean): Unit = {
                    tile.setVariable(AdvancedPipe.FILTER, AdvancedPipe.FILTER_MATCH_ORE)
                    tile.sendValueToServer(AdvancedPipe.FILTER, AdvancedPipe.FILTER_MATCH_ORE)
                }
            }

            components += new GuiComponentCheckBox(100, 55, "Damage", tile.matchDamage) {
                override def setValue(bool: Boolean): Unit = {
                    tile.setVariable(AdvancedPipe.FILTER, AdvancedPipe.FILTER_MATCH_DAMAGE)
                    tile.sendValueToServer(AdvancedPipe.FILTER, AdvancedPipe.FILTER_MATCH_DAMAGE)
                }
            }

            components += new GuiComponentCheckBox(100, 70, "NBT", tile.matchTag) {
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
                motherBoardTag += new GuiComponentText(GuiColor.ORANGE + "Motherboard", 26, 6)
                tabs.addTab(motherBoardTag.toList, 100, 65, new Color(0, 155, 0), new ItemStack(ItemManager.upgradeMBFull))

                tabs.getTabs.head.addChild(new GuiComponentTabSlotHolder(41, 25, 18, 18, tabs.getTabs.head, container.motherboardSlot, 170 + 41, 27))
            }

            val selectorTab = new ArrayBuffer[BaseComponent]
            selectorTab += new GuiComponentText(GuiColor.ORANGE + "I/O Config", 29, 6)
            selectorTab += new GuiComponentSideSelector(15, 20, 40, tileEntity.getWorld.getBlockState(tileEntity.getPos), tileEntity, true, renderTile = false) {
                override def setToggleController(): Unit = {
                    toggleableSidesController = new ToggleableSidesController {

                        override def onSideToggled(side: EnumFacing, modifier: Int): Unit = {
                            tileEntity.setVariable(AdvancedPipe.IO_FIELD_ID, side.ordinal())
                            tileEntity.sendValueToServer(AdvancedPipe.IO_FIELD_ID, side.ordinal())
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
            tabs.addTab(selectorTab.toList, 100, 125, new Color(150, 150, 150), new ItemStack(Blocks.piston))
        }
    }

    def addLeftTabs(tabs : GuiTabCollection, tileEntity : AdvancedPipe, container : ContainerAdvancedPipeMenu): Unit = {
        if (tileEntity != null) {
            if (tileEntity.getUpgradeBoard != null && tileEntity.getUpgradeBoard.hasControl) {
                var redstoneTab = new ArrayBuffer[BaseComponent]
                redstoneTab += new GuiComponentText(GuiColor.BLACK + "Redstone Mode", 5, 7)
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
                tabs.addReverseTab(redstoneTab.toList, 100, 50, new Color(255, 0, 0), new ItemStack(Items.redstone))

                var frequencyTab = new ArrayBuffer[BaseComponent]
                frequencyTab += new GuiComponentText(GuiColor.BLACK + "Frequency", 7, 7)

                frequencyTab += new GuiComponentSetNumber(20, 20, 60, tile.frequency, 0, 100) {
                    override def setValue(i: Int): Unit = {
                        tile.setVariable(AdvancedPipe.FREQUENCY, if(i < 0) 0 else if(i > 100) 100 else i)
                        tile.sendValueToServer(AdvancedPipe.FREQUENCY, tile.getVariable(AdvancedPipe.FREQUENCY))
                    }
                }

                tabs.addReverseTab(frequencyTab.toList, 100, 50, new Color(255, 153, 0), new ItemStack(Items.paper))
            }

            if (tileEntity.getUpgradeBoard != null && tileEntity.getUpgradeBoard.hasExpansion) {

                var extractionMode = new ArrayBuffer[BaseComponent]
                extractionMode += new GuiComponentText(GuiColor.BLACK + "Extraction Mode", 7, 6)

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
                tabs.addReverseTab(extractionMode.toList, 150, 50, new Color(255, 255, 0), new ItemStack(Items.book))
            }
        }
    }
}