package com.dyonovan.neotech.client.gui.machines.processors

import java.awt.Color

import com.dyonovan.neotech.client.gui.machines.GuiAbstractMachine
import com.dyonovan.neotech.common.container.machines.processors.ContainerCrucible
import com.dyonovan.neotech.common.tiles.machines.processors.TileCrucible
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentPowerBar, GuiComponentFluidTank, GuiComponentArrow}
import com.teambr.bookshelf.util.ColorUtils
import net.minecraft.entity.player.EntityPlayer

import scala.collection.mutable.ArrayBuffer

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/17/2016
  */
class GuiCrucible(player: EntityPlayer, tileEntity: TileCrucible) extends
        GuiAbstractMachine[ContainerCrucible](new ContainerCrucible(player.inventory, tileEntity), 175, 165,
            "neotech.crucible.title", player, tileEntity) {

    override def addComponents(): Unit = {

        //Arrow
        components += new GuiComponentArrow(104, 41) {
            override def getCurrentProgress: Int = tileEntity.getCookProgressScaled(24)
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
        components += new GuiComponentFluidTank(150, 18, 18, 60, tileEntity.tanks(tileEntity.OUTPUT_TANK)) {
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(tileEntity.tanks(tileEntity.OUTPUT_TANK).getFluidAmount + "/" + tileEntity.tanks(tileEntity.OUTPUT_TANK).getCapacity + " mb")
            }
        }
    }
}
