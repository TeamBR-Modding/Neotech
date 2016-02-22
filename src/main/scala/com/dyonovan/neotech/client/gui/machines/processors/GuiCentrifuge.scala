package com.dyonovan.neotech.client.gui.machines.processors

import java.awt.Color
import java.text.NumberFormat
import java.util.Locale

import com.dyonovan.neotech.client.gui.machines.GuiAbstractMachine
import com.dyonovan.neotech.common.container.machines.processors.ContainerCentrifuge
import com.dyonovan.neotech.common.tiles.machines.processors.TileCentrifuge
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentFluidTank, GuiComponentPowerBarGradient, GuiComponentArrow}
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
                buffer += NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language)).format(tileEntity.getEnergyStored(null)) + " / " +
                        NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language)).format(tileEntity.getMaxEnergyStored(null)) + " RF"
                buffer
            }
        }

        components += new GuiComponentFluidTank(38, 18, 50, 60, tileEntity.tanks(tileEntity.INPUT_TANK)) {
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                val buffer = new ArrayBuffer[String]()
                buffer += (if(tileEntity.tanks(tileEntity.INPUT_TANK).getFluid != null)
                    GuiColor.ORANGE + tileEntity.tanks(tileEntity.INPUT_TANK).getFluid.getLocalizedName
                else
                    GuiColor.RED + "Empty")
                buffer += NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language)).format(tileEntity.tanks(tileEntity.INPUT_TANK).getFluidAmount) + " / " +
                        NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language)).format(tileEntity.tanks(tileEntity.INPUT_TANK).getCapacity) + " mb"
                buffer
            }
        }

        components += new GuiComponentFluidTank(125, 18, 18, 60, tileEntity.tanks(tileEntity.OUTPUT_TANK_1)) {
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                val buffer = new ArrayBuffer[String]()
                buffer += (if(tileEntity.tanks(tileEntity.OUTPUT_TANK_1).getFluid != null)
                    GuiColor.ORANGE + tileEntity.tanks(tileEntity.OUTPUT_TANK_1).getFluid.getLocalizedName
                else
                    GuiColor.RED + "Empty")
                buffer += NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language)).format(tileEntity.tanks(tileEntity.OUTPUT_TANK_1).getFluidAmount) + " / " +
                        NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language)).format(tileEntity.tanks(tileEntity.OUTPUT_TANK_1).getCapacity) + " mb"
                buffer
            }
        }

        components += new GuiComponentFluidTank(147, 18, 18, 60, tileEntity.tanks(tileEntity.OUTPUT_TANK_2)) {
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                val buffer = new ArrayBuffer[String]()
                buffer += (if(tileEntity.tanks(tileEntity.OUTPUT_TANK_2).getFluid != null)
                    GuiColor.ORANGE + tileEntity.tanks(tileEntity.OUTPUT_TANK_2).getFluid.getLocalizedName
                else
                    GuiColor.RED + "Empty")
                buffer += NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language)).format(tileEntity.tanks(tileEntity.OUTPUT_TANK_2).getFluidAmount) + " / " +
                        NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language)).format(tileEntity.tanks(tileEntity.OUTPUT_TANK_2).getCapacity) + " mb"
                buffer
            }
        }
    }
}