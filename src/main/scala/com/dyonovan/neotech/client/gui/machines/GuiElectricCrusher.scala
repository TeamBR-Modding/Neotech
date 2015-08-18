package com.dyonovan.neotech.client.gui.machines

import java.awt.Color

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.common.container.machines.ContainerElectricCrusher
import com.dyonovan.neotech.common.tiles.machines.TileElectricCrusher
import com.teambr.bookshelf.client.gui.GuiBase
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentArrow, GuiComponentPowerBar}
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
 * @since August 13, 2015
 */
class GuiElectricCrusher(player: EntityPlayer, tileEntity: TileElectricCrusher) extends
        GuiBase[ContainerElectricCrusher](new ContainerElectricCrusher(player.inventory, tileEntity), 175, 165, "inventory.crusher.title"){

    protected var tile = tileEntity

    override def drawGuiContainerBackgroundLayer(f: Float, i: Int, j:Int): Unit = {
        tile = tile.getWorld.getTileEntity(tile.getPos).asInstanceOf[TileElectricCrusher]
        super[GuiBase].drawGuiContainerBackgroundLayer(f, i, j)
    }

    override def addComponents(): Unit = {
        components += new GuiComponentArrow(64, 34) {
            override def getCurrentProgress: Int = tile.getCookProgressScaled(24)

            override def getDynamicToolTip(mouseX: Int, mouseY: Int): ArrayBuffer[String] = {
                if (NeoTech.nei != null)
                    ArrayBuffer(StatCollector.translateToLocal("inventory.nei.recipes"))
                null
            }

            override def mouseDown(x: Int, y: Int, button: Int) : Unit = {
                if (NeoTech.nei != null) NeoTech.nei.onArrowClicked(inventory)
            }
        }
        components += new GuiComponentPowerBar(14, 18, 18, 60, new Color(255, 0, 0)) {
            override def getEnergyPercent(scale: Int): Int = {
                tile.getEnergyStored(null) * scale / tile.getMaxEnergyStored(null)
            }
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(tile.getEnergyStored(null) + " / " + tile.getMaxEnergyStored(null))
            }
        }
    }
}
