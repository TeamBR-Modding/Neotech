package com.dyonovan.neotech.client.gui.machines.operators

import java.awt.Color

import com.dyonovan.neotech.client.gui.machines.GuiAbstractMachine
import com.dyonovan.neotech.collections.EnumInputOutputMode
import com.dyonovan.neotech.common.container.machines.operators.ContainerTreeFarm
import com.dyonovan.neotech.common.tiles.machines.operators.TileTreeFarm
import com.dyonovan.neotech.utils.ClientUtils
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentColoredZone, GuiComponentPowerBarGradient}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{EnumFacing, StatCollector}

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/12/2016
  */
class GuiTreeFarm(player: EntityPlayer, tileEntity: TileTreeFarm) extends
        GuiAbstractMachine[ContainerTreeFarm](new ContainerTreeFarm(player.inventory, tileEntity), 175, 165,
            "neotech.treeFarm.title", player, tileEntity) {
    override def addComponents(): Unit = {
        components += new GuiComponentPowerBarGradient(25, 18, 18, 60, new Color(255, 0, 0)) {
            addColor(new Color(255, 150, 0))
            addColor(new Color(255, 255, 0))


            override def getEnergyPercent(scale: Int): Int = {
                tileEntity.getEnergyStored(null) * scale / tileEntity.getMaxEnergyStored(null)
            }
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                val buffer = new ArrayBuffer[String]()
                buffer += GuiColor.ORANGE + StatCollector.translateToLocal("neotech.text.redstoneFlux")
                buffer += ClientUtils.formatNumber(tileEntity.getEnergyStored(null)) + " / " +
                        ClientUtils.formatNumber(tileEntity.getMaxEnergyStored(null)) + " RF"
                buffer
            }
        }

        // Axe Slot
        components += new GuiComponentColoredZone(60, 18, 20, 20, new Color(0, 0, 0, 0)) {
            override def getDynamicColor = {
                var color = new Color(0, 0, 0, 0)
                breakable {
                    for(dir <- EnumFacing.values())
                        if(tileEntity.getModeForSide(dir) == EnumInputOutputMode.ALL_MODES) {
                            color = EnumInputOutputMode.ALL_MODES.getHighlightColor
                            break
                        }
                        else if(tileEntity.getModeForSide(dir) == EnumInputOutputMode.INPUT_ALL) {
                            color = EnumInputOutputMode.INPUT_ALL.getHighlightColor
                            break
                        } else if(tileEntity.getModeForSide(dir) == EnumInputOutputMode.INPUT_PRIMARY)
                            color = EnumInputOutputMode.INPUT_PRIMARY.getHighlightColor
                }
                if(color.getAlpha != 0)
                    color = new Color(color.getRed, color.getGreen, color.getBlue, 80)
                color
            }
        }

        // Shears Slot
        components += new GuiComponentColoredZone(87, 18, 20, 20, new Color(0, 0, 0, 0)) {
            override def getDynamicColor = {
                var color = new Color(0, 0, 0, 0)
                breakable {
                    for(dir <- EnumFacing.values())
                        if(tileEntity.getModeForSide(dir) == EnumInputOutputMode.ALL_MODES) {
                            color = EnumInputOutputMode.ALL_MODES.getHighlightColor
                            break
                        }
                        else if(tileEntity.getModeForSide(dir) == EnumInputOutputMode.INPUT_ALL) {
                            color = EnumInputOutputMode.INPUT_ALL.getHighlightColor
                            break
                        } else if(tileEntity.getModeForSide(dir) == EnumInputOutputMode.INPUT_SECONDARY)
                            color = EnumInputOutputMode.INPUT_SECONDARY.getHighlightColor
                }
                if(color.getAlpha != 0)
                    color = new Color(color.getRed, color.getGreen, color.getBlue, 80)
                color
            }
        }

        // Output
        components += new GuiComponentColoredZone(60, 38, 110, 38, new Color(0, 0, 0, 0)) {
            override def getDynamicColor = {
                var color = new Color(0, 0, 0, 0)
                for(dir <- EnumFacing.values())
                    breakable {
                        if (tileEntity.getModeForSide(dir) == EnumInputOutputMode.ALL_MODES) {
                            color = EnumInputOutputMode.ALL_MODES.getHighlightColor
                            break
                        }
                        else if (tileEntity.getModeForSide(dir) == EnumInputOutputMode.OUTPUT_ALL)
                            color = EnumInputOutputMode.OUTPUT_ALL.getHighlightColor
                    }
                if(color.getAlpha != 0)
                    color = new Color(color.getRed, color.getGreen, color.getBlue, 80)
                color
            }
        }
    }
}
