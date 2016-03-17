package com.dyonovan.neotech.client.gui.machines.processors

import java.awt.Color

import com.dyonovan.neotech.client.gui.machines.GuiAbstractMachine
import com.dyonovan.neotech.collections.EnumInputOutputMode
import com.dyonovan.neotech.common.container.machines.processors.ContainerCentrifuge
import com.dyonovan.neotech.common.tiles.machines.processors.TileCentrifuge
import com.dyonovan.neotech.utils.ClientUtils
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentColoredZone, GuiComponentArrow, GuiComponentFluidTank, GuiComponentPowerBarGradient}
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
  * @since 2/21/2016
  */
class GuiCentrifuge(player: EntityPlayer, tileEntity: TileCentrifuge) extends
        GuiAbstractMachine[ContainerCentrifuge](new ContainerCentrifuge(player.inventory, tileEntity), 175, 165,
            "neotech.centrifuge.title", player, tileEntity) {

    override def addComponents(): Unit = {
        components += new GuiComponentArrow(94, 35) {
            override def getCurrentProgress: Int = tileEntity.getCookProgressScaled(24)
        }

        components += new GuiComponentPowerBarGradient(14, 18, 18, 60, new Color(255, 0, 0)) {
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

        // Input Tank
        components += new GuiComponentColoredZone(37, 17, 52, 62, new Color(0, 0, 0, 0)) {
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

        components += new GuiComponentFluidTank(38, 18, 50, 60, tileEntity.tanks(tileEntity.INPUT_TANK)) {
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                val buffer = new ArrayBuffer[String]()
                buffer += (if(tileEntity.tanks(tileEntity.INPUT_TANK).getFluid != null)
                    GuiColor.ORANGE + tileEntity.tanks(tileEntity.INPUT_TANK).getFluid.getLocalizedName
                else
                    GuiColor.RED + "Empty")
                buffer += ClientUtils.formatNumber(tileEntity.tanks(tileEntity.INPUT_TANK).getFluidAmount) + " / " +
                        ClientUtils.formatNumber(tileEntity.tanks(tileEntity.INPUT_TANK).getCapacity) + " mb"
                buffer
            }
        }

        // Output One
        components += new GuiComponentColoredZone(124, 17, 20, 62, new Color(0, 0, 0, 0)) {
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

        components += new GuiComponentFluidTank(125, 18, 18, 60, tileEntity.tanks(tileEntity.OUTPUT_TANK_1)) {
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                val buffer = new ArrayBuffer[String]()
                buffer += (if(tileEntity.tanks(tileEntity.OUTPUT_TANK_1).getFluid != null)
                    GuiColor.ORANGE + tileEntity.tanks(tileEntity.OUTPUT_TANK_1).getFluid.getLocalizedName
                else
                    GuiColor.RED + "Empty")
                buffer += ClientUtils.formatNumber(tileEntity.tanks(tileEntity.OUTPUT_TANK_1).getFluidAmount) + " / " +
                        ClientUtils.formatNumber(tileEntity.tanks(tileEntity.OUTPUT_TANK_1).getCapacity) + " mb"
                buffer
            }
        }

        // Output Two
        components += new GuiComponentColoredZone(146, 17, 20, 62, new Color(0, 0, 0, 0)) {
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
                        } else if(tileEntity.getModeForSide(dir) == EnumInputOutputMode.OUTPUT_SECONDARY)
                            color = EnumInputOutputMode.OUTPUT_SECONDARY.getHighlightColor
                }
                if(color.getAlpha != 0)
                    color = new Color(color.getRed, color.getGreen, color.getBlue, 80)
                color
            }
        }

        components += new GuiComponentFluidTank(147, 18, 18, 60, tileEntity.tanks(tileEntity.OUTPUT_TANK_2)) {
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                val buffer = new ArrayBuffer[String]()
                buffer += (if(tileEntity.tanks(tileEntity.OUTPUT_TANK_2).getFluid != null)
                    GuiColor.ORANGE + tileEntity.tanks(tileEntity.OUTPUT_TANK_2).getFluid.getLocalizedName
                else
                    GuiColor.RED + "Empty")
                buffer += ClientUtils.formatNumber(tileEntity.tanks(tileEntity.OUTPUT_TANK_2).getFluidAmount) + " / " +
                        ClientUtils.formatNumber(tileEntity.tanks(tileEntity.OUTPUT_TANK_2).getCapacity) + " mb"
                buffer
            }
        }
    }
}