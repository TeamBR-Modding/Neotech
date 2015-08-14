package com.dyonovan.neotech.client.gui.machines

import java.awt.Color

import com.dyonovan.neotech.common.container.machines.ContainerElectricFurnace
import com.dyonovan.neotech.common.tiles.machines.TileElectricFurnace
import com.teambr.bookshelf.client.gui.GuiBase
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentArrow, GuiComponentPowerBar}
import net.minecraft.entity.player.EntityPlayer

import scala.collection.mutable.ArrayBuffer

class GuiElectricFurnace(player: EntityPlayer, tileEntity: TileElectricFurnace) extends
        GuiBase[ContainerElectricFurnace](new ContainerElectricFurnace(player.inventory, tileEntity), 175, 165, "inventory.furnace.title"){

    protected var tile = tileEntity

    override def drawGuiContainerBackgroundLayer(f: Float, i: Int, j:Int): Unit = {
        tile = tile.getWorld.getTileEntity(tile.getPos).asInstanceOf[TileElectricFurnace]
        super[GuiBase].drawGuiContainerBackgroundLayer(f, i, j)
    }

    override def addComponents(): Unit = {
        components += new GuiComponentArrow(81, 35) {
            override def getCurrentProgress: Int = tile.getCookProgressScaled(24)
        }
        components += new GuiComponentPowerBar(20, 18, 18, 60, new Color(255, 0, 0)) {
            override def getEnergyPercent(scale: Int): Int = {
                tile.getEnergyStored(null) * scale / tile.getMaxEnergyStored(null)
            }
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(tile.getEnergyStored(null) + " / " + tile.getMaxEnergyStored(null))
            }
        }
    }
}
