package com.dyonovan.neotech.client.gui.machines.generators

import java.awt.Color
import java.text.NumberFormat
import java.util.Locale

import com.dyonovan.neotech.client.gui.machines.GuiAbstractMachine
import com.dyonovan.neotech.common.container.machines.generators.ContainerFluidGenerator
import com.dyonovan.neotech.common.tiles.machines.generators.TileFluidGenerator
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.client.gui.component.display._
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
 * @author Dyonovan
 * @since August 21, 2015
 */
class GuiFluidGenerator(player: EntityPlayer, tileEntity: TileFluidGenerator) extends
        GuiAbstractMachine[ContainerFluidGenerator](new ContainerFluidGenerator(player.inventory, tileEntity), 175, 165, "neotech.fluidgenerator.title", player, tileEntity) {

    override def addComponents(): Unit = {

        //Flame for Burning
        components += new GuiComponentFlame(78, 55) {
            override def getCurrentBurn: Int = if (tileEntity.isActive) tileEntity.getBurnProgressScaled(14) else 0

            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language))
                        .format(tileEntity.burnTime) + " ticks left.")
            }
        }

        //Power Bar
        components += new GuiComponentPowerBarGradient(150, 18, 18, 60, new Color(255, 0, 0)) {
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

        //Current Production
        components += new GuiComponentText(GuiColor.RED + "RF/t = " + tileEntity.getEnergyProduced, 64, 18) {
            override def renderOverlay(i : Int, j : Int, x : Int, y : Int) = {
                setText(GuiColor.RED + "RF/t = " +
                        NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language))
                                .format(tileEntity.getEnergyProduced))
                super.renderOverlay(i, j, x, y)
            }
        }

        //Stored Fluid
        components += new GuiComponentFluidTank(7, 18, 18, 60, tileEntity.tank) {
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                val buffer = new ArrayBuffer[String]()
                buffer += (if(tileEntity.tank.getFluid != null)
                    GuiColor.ORANGE + tileEntity.tank.getFluid.getLocalizedName
                else
                    GuiColor.RED + "Empty")
                buffer += NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language)).format(tileEntity.tank.getFluidAmount) + " / " +
                        NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language)).format(tileEntity.tank.getCapacity) + " mb"
                buffer
            }
        }
    }
}
