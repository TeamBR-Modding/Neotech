package com.dyonovan.neotech.client.gui.machines.processors

import java.awt.Color

import com.dyonovan.neotech.client.gui.machines.GuiAbstractMachine
import com.dyonovan.neotech.collections.EnumInputOutputMode
import com.dyonovan.neotech.common.container.machines.processors.ContainerSolidifier
import com.dyonovan.neotech.common.tiles.machines.processors.TileSolidifier
import com.dyonovan.neotech.utils.ClientUtils
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.client.gui.component.control.GuiComponentItemStackButton
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
  * @since 2/18/2016
  */
class GuiSolidifier(player: EntityPlayer, tileEntity: TileSolidifier) extends
        GuiAbstractMachine[ContainerSolidifier](new ContainerSolidifier(player.inventory, tileEntity), 175, 165,
            "neotech.electricSolidifier.title", player, tileEntity) {

    override def addComponents(): Unit = {

        //Arrow
        components += new GuiComponentArrow(97, 35) {
            override def getCurrentProgress: Int = tileEntity.getCookProgressScaled(24)
        }

        components += new GuiComponentItemStackButton(97, 55, tileEntity.getDisplayStackForProcessMode(tileEntity.currentMode)) {
            override def doAction(): Unit = {
                tileEntity.toggleProcessMode()
                tileEntity.sendValueToServer(tileEntity.UPDATE_MODE, tileEntity.processModeToInt(tileEntity.currentMode))
                setStack(tileEntity.getDisplayStackForProcessMode(tileEntity.currentMode))
            }

            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                val buffer = new ArrayBuffer[String]()
                buffer += GuiColor.ORANGE + StatCollector.translateToLocal("neotech.text.processMode")
                buffer += tileEntity.getDisplayNameForProcessMode(tileEntity.currentMode)
            }
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

        // Output Slot
        components += new GuiComponentColoredZone(131, 33, 20, 20, new Color(0, 0, 0, 0)) {
            override def getDynamicColor = {
                var color = new Color(0, 0, 0, 0)
                for(dir <- EnumFacing.values())
                    breakable {
                        if(tileEntity.getModeForSide(dir) == EnumInputOutputMode.ALL_MODES) {
                            color = EnumInputOutputMode.ALL_MODES.getHighlightColor
                            break
                        } else if (tileEntity.getModeForSide(dir) == EnumInputOutputMode.OUTPUT_ALL)
                            color = EnumInputOutputMode.OUTPUT_ALL.getHighlightColor
                    }

                if(color.getAlpha != 0)
                    color = new Color(color.getRed, color.getGreen, color.getBlue, 80)
                color
            }
        }

        //Stored Fluid
        components += new GuiComponentColoredZone(34, 17, 52, 62, new Color(0, 0, 0, 0)) {
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

        components += new GuiComponentFluidTank(35, 18, 50, 60, tileEntity.tanks(tileEntity.INPUT_TANK)) {
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
    }
}