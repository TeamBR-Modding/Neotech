package com.dyonovan.neotech.client.gui.machines.generators

import java.awt.Color

import com.dyonovan.neotech.ColorUtils
import com.dyonovan.neotech.client.gui.machines.GuiAbstractMachine
import com.dyonovan.neotech.common.container.machines.generators.ContainerFurnaceGenerator
import com.dyonovan.neotech.common.tiles.machines.generators.TileFurnaceGenerator
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentFlame, GuiComponentPowerBar, GuiComponentText}
import net.minecraft.entity.player.EntityPlayer

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
                ArrayBuffer(tileEntity.burnTime + " ticks left.")
            }
        }

        //Energy Stored
        components += new GuiComponentPowerBar(7, 18, 18, 60, new Color(255, 0, 0)) {
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

        //Current Production
        components += new GuiComponentText(GuiColor.RED + "RF/t = " + tileEntity.getEnergyProduced, 64, 18) {
            override def renderOverlay(i : Int, j : Int, x : Int, y : Int) = {
                setText(GuiColor.RED + "RF/t = " + tileEntity.getEnergyProduced)
                super.renderOverlay(i, j, x, y)
            }
        }
    }
}
