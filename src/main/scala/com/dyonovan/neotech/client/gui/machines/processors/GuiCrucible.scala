package com.dyonovan.neotech.client.gui.machines.processors

import java.awt.Color
import java.text.NumberFormat
import java.util.Locale

import com.dyonovan.neotech.client.gui.machines.GuiAbstractMachine
import com.dyonovan.neotech.common.container.machines.processors.ContainerCrucible
import com.dyonovan.neotech.common.tiles.machines.processors.TileCrucible
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentArrow, GuiComponentFluidTank, GuiComponentPowerBarGradient}
import net.minecraft.client.Minecraft
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
  * @since 2/17/2016
  */
class GuiCrucible(player: EntityPlayer, tileEntity: TileCrucible) extends
        GuiAbstractMachine[ContainerCrucible](new ContainerCrucible(player.inventory, tileEntity), 175, 165,
            "neotech.crucible.title", player, tileEntity) {

    override def addComponents(): Unit = {

        //Arrow
        components += new GuiComponentArrow(81, 35) {
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
                buffer += NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language)).format(tileEntity.getEnergyStored(null)) + " / " +
                        NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language)).format(tileEntity.getMaxEnergyStored(null)) + " RF"
                buffer
            }
        }

        //Stored Fluid
        components += new GuiComponentFluidTank(115, 18, 50, 60, tileEntity.tanks(tileEntity.OUTPUT_TANK)) {
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                val buffer = new ArrayBuffer[String]()
                buffer += (if(tileEntity.tanks(tileEntity.OUTPUT_TANK).getFluid != null)
                    GuiColor.ORANGE + tileEntity.tanks(tileEntity.OUTPUT_TANK).getFluid.getLocalizedName
                else
                    GuiColor.RED + "Empty")
                buffer += NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language)).format(tileEntity.tanks(tileEntity.OUTPUT_TANK).getFluidAmount) + " / " +
                        NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language)).format(tileEntity.tanks(tileEntity.OUTPUT_TANK).getCapacity) + " mb"
                buffer
            }
        }
    }
}
