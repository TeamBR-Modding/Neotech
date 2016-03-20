package com.dyonovan.neotech.client.gui.machines.processors

import java.awt.Color

import com.dyonovan.neotech.client.gui.machines.GuiAbstractMachine
import com.dyonovan.neotech.collections.EnumInputOutputMode
import com.dyonovan.neotech.common.container.machines.processors.ContainerElectricCrusher
import com.dyonovan.neotech.common.tiles.machines.processors.TileElectricCrusher
import com.dyonovan.neotech.utils.ClientUtils
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentArrow, GuiComponentColoredZone, GuiComponentPowerBarGradient}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.text.translation.I18n

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

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
        GuiAbstractMachine[ContainerElectricCrusher](new ContainerElectricCrusher(player.inventory, tileEntity), 175, 165,
            "neotech.crusher.title", player, tileEntity) {

    protected var tile = tileEntity


    override def drawGuiContainerBackgroundLayer(f: Float, i: Int, j:Int): Unit = {
        tile = tile.getWorld.getTileEntity(tile.getPos).asInstanceOf[TileElectricCrusher]
        super[GuiAbstractMachine].drawGuiContainerBackgroundLayer(f, i, j)
    }

    override def addComponents(): Unit = {
        components += new GuiComponentArrow(64, 34) {
            override def getCurrentProgress: Int = tile.getCookProgressScaled(24)
        }

        components += new GuiComponentPowerBarGradient(14, 18, 18, 60, new Color(255, 0, 0)) {
            addColor(new Color(255, 150, 0))
            addColor(new Color(255, 255, 0))


            override def getEnergyPercent(scale: Int): Int = {
                tile.getEnergyStored(null) * scale / tile.getMaxEnergyStored(null)
            }
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                val buffer = new ArrayBuffer[String]()
                buffer += GuiColor.ORANGE + I18n.translateToLocal("neotech.text.redstoneFlux")
                buffer += ClientUtils.formatNumber(tile.getEnergyStored(null)) + " / " +
                        ClientUtils.formatNumber(tile.getMaxEnergyStored(null)) + " RF"
                buffer
            }
        }

        // Input Slot
        components += new GuiComponentColoredZone(39, 33, 20, 20, new Color(0, 0, 0, 0)) {
            override def getDynamicColor = {
                var color = new Color(0, 0, 0, 0)
                for(dir <- EnumFacing.values())
                    breakable {
                        if(tileEntity.getModeForSide(dir) == EnumInputOutputMode.ALL_MODES) {
                            color = EnumInputOutputMode.ALL_MODES.getHighlightColor
                            break
                        } else if (tileEntity.getModeForSide(dir) == EnumInputOutputMode.INPUT_ALL)
                            color = EnumInputOutputMode.INPUT_ALL.getHighlightColor
                    }

                if(color.getAlpha != 0)
                    color = new Color(color.getRed, color.getGreen, color.getBlue, 80)
                color
            }
        }

        // Output Slot One
        components += new GuiComponentColoredZone(95, 29, 28, 28, new Color(0, 0, 0, 0)) {
            override def getDynamicColor = {
                var color = new Color(0, 0, 0, 0)
                breakable {
                    for(dir <- EnumFacing.values())
                        if(tileEntity.getModeForSide(dir) == EnumInputOutputMode.ALL_MODES) {
                            color = EnumInputOutputMode.ALL_MODES.getHighlightColor
                            break
                        } else if(tileEntity.getModeForSide(dir) == EnumInputOutputMode.OUTPUT_ALL) {
                            color = EnumInputOutputMode.OUTPUT_ALL.getHighlightColor
                            break
                        } else if(tileEntity.getModeForSide(dir) == EnumInputOutputMode.OUTPUT_PRIMARY)
                            color = EnumInputOutputMode.OUTPUT_PRIMARY.getHighlightColor
                }
                if(color.getAlpha != 0)
                    color = new Color(color.getRed, color.getGreen, color.getBlue, 80)
                color
            }
        }

        // Output Slot Two
        components += new GuiComponentColoredZone(125, 29, 28, 28, new Color(0, 0, 0, 0)) {
            override def getDynamicColor = {
                var color = new Color(0, 0, 0, 0)
                breakable {
                    for(dir <- EnumFacing.values()) {
                        if (tileEntity.getModeForSide(dir) == EnumInputOutputMode.ALL_MODES) {
                            color = EnumInputOutputMode.ALL_MODES.getHighlightColor
                            break
                        }
                        if (tileEntity.getModeForSide(dir) == EnumInputOutputMode.OUTPUT_ALL) {
                            color = EnumInputOutputMode.OUTPUT_ALL.getHighlightColor
                        }
                        if (tileEntity.getModeForSide(dir) == EnumInputOutputMode.OUTPUT_SECONDARY) {
                            color = EnumInputOutputMode.OUTPUT_SECONDARY.getHighlightColor
                            break
                        }
                    }
                }
                if(color.getAlpha != 0)
                    color = new Color(color.getRed, color.getGreen, color.getBlue, 80)
                color
            }
        }
    }
}
