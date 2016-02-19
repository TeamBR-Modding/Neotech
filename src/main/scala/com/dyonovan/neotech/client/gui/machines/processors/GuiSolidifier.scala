package com.dyonovan.neotech.client.gui.machines.processors

import java.awt.Color

import com.dyonovan.neotech.client.gui.machines.GuiAbstractMachine
import com.dyonovan.neotech.common.container.machines.processors.ContainerSolidifier
import com.dyonovan.neotech.common.tiles.machines.processors.TileSolidifier
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.client.gui.component.control.GuiComponentItemStackButton
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentArrow, GuiComponentFluidTank, GuiComponentPowerBar}
import com.teambr.bookshelf.util.ColorUtils
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.StatCollector

import scala.collection.mutable.ArrayBuffer

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

        components += new GuiComponentPowerBar(14, 18, 18, 60, new Color(255, 0, 0)) {
            override  def getDynamicColor() : Color = {
                val scale = tileEntity.getEnergyStored(null) * 100 / tileEntity.getMaxEnergyStored(null)
                if(scale >= 66) {
                    ColorUtils.getColorBetween(new Color(255, 153, 0), new Color(255, 0, 0), (scale - 67) / 33F)
                } else if(scale >= 33) {
                    ColorUtils.getColorBetween(new Color(255, 255, 0), new Color(255, 153, 0), (scale - 33) / 33F)
                } else
                    ColorUtils.getColorBetween(new Color(0, 0, 0),new Color(255, 255, 0), scale / 33F)
            }
            override def getEnergyPercent(scale: Int): Int = {
                tileEntity.getEnergyStored(null) * scale / tileEntity.getMaxEnergyStored(null)
            }
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(tileEntity.getEnergyStored(null) + " / " + tileEntity.getMaxEnergyStored(null))
            }
        }

        //Stored Fluid
        components += new GuiComponentFluidTank(35, 18, 50, 60, tileEntity.tanks(tileEntity.INPUT_TANK)) {
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                val buffer = new ArrayBuffer[String]()
                buffer += (if(tileEntity.tanks(tileEntity.INPUT_TANK).getFluid != null)
                    GuiColor.ORANGE + tileEntity.tanks(tileEntity.INPUT_TANK).getFluid.getLocalizedName
                else
                    GuiColor.RED + "Empty")
                buffer += tileEntity.tanks(tileEntity.INPUT_TANK).getFluidAmount + "/" + tileEntity.tanks(tileEntity.INPUT_TANK).getCapacity + " mb"
                buffer
            }
        }
    }
}