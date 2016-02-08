package com.dyonovan.neotech.client.gui.storage

import java.awt.Color
import com.dyonovan.neotech.common.tiles.storage.TileRFStorage
import com.teambr.bookshelf.client.gui.GuiBase
import com.teambr.bookshelf.client.gui.component.display.GuiComponentPowerBar
import com.teambr.bookshelf.common.container.ContainerGeneric
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
 * @author Dyonovan
 * @since August 15, 2015
 */
class GuiRFStorage(player: EntityPlayer, tileEntity: TileRFStorage, title: String) extends
    GuiBase[ContainerGeneric](new ContainerGeneric, 140, 120, title) {

    override def addComponents(): Unit = {
        if (tileEntity != null) {
            components += new GuiComponentPowerBar(56, 23, 18, 74, new Color(255, 0, 0)) {
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
                    (tileEntity.getEnergyStored(null).toLong * scale / tileEntity.getMaxEnergyStored(null)).toInt
                }

                override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                    ArrayBuffer(tileEntity.getEnergyStored(null) + " / " + tileEntity.getMaxEnergyStored(null))
                }
            }
        }
    }
}
