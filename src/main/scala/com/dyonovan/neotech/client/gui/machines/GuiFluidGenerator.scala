package com.dyonovan.neotech.client.gui.machines

import java.awt.Color

import com.dyonovan.neotech.common.tiles.machines.TileFluidGenerator
import com.teambr.bookshelf.client.gui.{GuiColor, GuiBase}
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentFluidTank, GuiComponentText, GuiComponentPowerBar, GuiComponentFlame}
import com.teambr.bookshelf.common.container.ContainerGeneric
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
 * @since August 21, 2015
 */
class GuiFluidGenerator(player: EntityPlayer, tileEntity: TileFluidGenerator) extends
        GuiBase[ContainerGeneric](new ContainerGeneric, 140, 120, "neotech.fluidgenerator.title") {


    override def addComponents(): Unit = {
        components += new GuiComponentFlame(62, 52) {
            override def getCurrentBurn: Int = if (tileEntity.isBurning) tileEntity.getBurnTimeRemainingScaled(14) else 0

            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(tileEntity.values.burnTime + " ticks left.")
            }
        }
        components += new GuiComponentPowerBar(20, 30, 18, 60, new Color(255, 0, 0)) {
            override def getEnergyPercent(scale: Int): Int = {
                tileEntity.getEnergyStored(null) * scale / tileEntity.getMaxEnergyStored(null)
            }
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(tileEntity.getEnergyStored(null) + " / " + tileEntity.getMaxEnergyStored(null))
            }
        }
        components += new GuiComponentText(GuiColor.RED + "RF/t = " + tileEntity.RF_TICK, 48, 18)
        components += new GuiComponentFluidTank(102, 30, 18, 60, tileEntity.tank) {
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(tileEntity.tank.getFluidAmount + "/" + tileEntity.tank.getCapacity + " mb")
            }
        }
    }
}
