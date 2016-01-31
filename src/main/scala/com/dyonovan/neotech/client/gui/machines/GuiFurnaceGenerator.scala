package com.dyonovan.neotech.client.gui.machines

import java.awt.Color
import javax.annotation.Nullable

import com.dyonovan.neotech.common.container.machines.ContainerFurnaceGenerator
import com.dyonovan.neotech.common.tiles.machines.TileFurnaceGenerator
import com.dyonovan.neotech.managers.{BlockManager, ItemManager}
import com.dyonovan.neotech.network.{OpenContainerGui, PacketDispatcher}
import com.teambr.bookshelf.client.gui.component.BaseComponent
import com.teambr.bookshelf.client.gui.component.control.{GuiComponentButton, GuiComponentSideSelector}
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentFlame, GuiComponentPowerBar, GuiComponentText, GuiTabCollection}
import com.teambr.bookshelf.client.gui.component.listeners.IMouseEventListener
import com.teambr.bookshelf.client.gui.{GuiBase, GuiColor}
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
 * @since August 14, 2015
 */
class GuiFurnaceGenerator(player: EntityPlayer, tileEntity: TileFurnaceGenerator) extends
        GuiBase[ContainerFurnaceGenerator](new ContainerFurnaceGenerator(player.inventory, tileEntity), 175, 165,
            "neotech.furnacegenerator.title"){

    override def addComponents(): Unit = {
        components += new GuiComponentFlame(78, 55) {
            override def getCurrentBurn: Int = if (tileEntity.isBurning) tileEntity.getBurnTimeRemainingScaled(14) else 0

            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(tileEntity.values.burnTime + " ticks left.")
            }
        }
        components += new GuiComponentPowerBar(20, 18, 18, 60, new Color(255, 0, 0)) {
            override def getEnergyPercent(scale: Int): Int = {
                tileEntity.getEnergyStored(null) * scale / tileEntity.getMaxEnergyStored(null)
            }
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(tileEntity.getEnergyStored(null) + " / " + tileEntity.getMaxEnergyStored(null))
            }
        }
        components += new GuiComponentText(GuiColor.RED + "RF/t = " + tileEntity.RF_TICK, 64, 18)
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
                tabs.addTab(selectorTab.toList, 100, 100, new Color(150, 150, 150), new ItemStack(BlockManager.furnaceGenerator))
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
