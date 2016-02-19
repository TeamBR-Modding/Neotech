package com.dyonovan.neotech.client.gui.machines.generators

import java.awt.Color
import java.text.NumberFormat
import java.util.Locale

import com.dyonovan.neotech.client.gui.machines.GuiAbstractMachine
import com.dyonovan.neotech.common.container.machines.generators.ContainerFurnaceGenerator
import com.dyonovan.neotech.common.tiles.machines.generators.TileFurnaceGenerator
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentFlame, GuiComponentPowerBarGradient, GuiComponentText}
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
 * @since August 14, 2015
 */
class GuiFurnaceGenerator(player: EntityPlayer, tileEntity: TileFurnaceGenerator) extends
        GuiAbstractMachine[ContainerFurnaceGenerator](new ContainerFurnaceGenerator(player.inventory, tileEntity), 175, 165,
            "neotech.furnacegenerator.title", player, tileEntity) {

    override def addComponents(): Unit = {
        //Flame for Burning
        components += new GuiComponentFlame(78, 55) {
            override def getCurrentBurn: Int = if (tileEntity.isActive) tileEntity.getBurnProgressScaled(14) else 0

            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language))
                        .format(tileEntity.burnTime) + " ticks left.")
            }
        }

        //Energy Stored
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

        //Current Production
        components += new GuiComponentText(GuiColor.RED + "RF/t = " + tileEntity.getEnergyProduced, 64, 18) {
            override def renderOverlay(i : Int, j : Int, x : Int, y : Int) = {
                setText(GuiColor.RED + "RF/t = " +
                        NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language))
                                .format(tileEntity.getEnergyProduced))
                super.renderOverlay(i, j, x, y)
            }
        }
    }
}
