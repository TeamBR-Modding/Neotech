package com.dyonovan.neotech.client.gui.machines.processors

import java.awt.Color

import com.dyonovan.neotech.client.gui.machines.GuiAbstractMachine
import com.dyonovan.neotech.collections.EnumInputOutputMode
import com.dyonovan.neotech.common.container.machines.processors.ContainerElectricFurnace
import com.dyonovan.neotech.common.tiles.machines.processors.TileElectricFurnace
import com.dyonovan.neotech.utils.ClientUtils
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentColoredZone, GuiComponentArrow, GuiComponentPowerBarGradient}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{EnumFacing, StatCollector}

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

class GuiElectricFurnace(player: EntityPlayer, tileEntity: TileElectricFurnace) extends
        GuiAbstractMachine[ContainerElectricFurnace](new ContainerElectricFurnace(player.inventory, tileEntity), 175, 165,
            "neotech.furnace.title", player, tileEntity) {

    protected var tile = tileEntity


    override def drawGuiContainerBackgroundLayer(f: Float, i: Int, j: Int): Unit = {
        tile = tile.getWorld.getTileEntity(tile.getPos).asInstanceOf[TileElectricFurnace]
        super[GuiAbstractMachine].drawGuiContainerBackgroundLayer(f, i, j)
    }

    override def addComponents(): Unit = {
        components += new GuiComponentArrow(81, 35) {
            override def getCurrentProgress: Int = tile.getCookProgressScaled(24)
        }

        components += new GuiComponentPowerBarGradient(14, 18, 18, 60, new Color(255, 0, 0)) {
            addColor(new Color(255, 150, 0))
            addColor(new Color(255, 255, 0))


            override def getEnergyPercent(scale: Int): Int = {
                tile.getEnergyStored(null) * scale / tile.getMaxEnergyStored(null)
            }
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                val buffer = new ArrayBuffer[String]()
                buffer += GuiColor.ORANGE + StatCollector.translateToLocal("neotech.text.redstoneFlux")
                buffer += ClientUtils.formatNumber(tile.getEnergyStored(null)) + " / " +
                        ClientUtils.formatNumber(tile.getMaxEnergyStored(null)) + " RF"
                buffer
            }
        }

        // Input Slot
        components += new GuiComponentColoredZone(54, 33, 20, 20, new Color(0, 0, 0, 0)) {
            override def getDynamicColor = {
                var color = new Color(0, 0, 0, 0)
                for(dir <- EnumFacing.values())
                    breakable {
                        if(tileEntity.getModeForSide(dir) == EnumInputOutputMode.ALL_MODES) {
                            color = EnumInputOutputMode.ALL_MODES.getHighlightColor
                            break
                        } else
                        if (tileEntity.getModeForSide(dir) == EnumInputOutputMode.INPUT_ALL)
                            color = EnumInputOutputMode.INPUT_ALL.getHighlightColor
                    }

                if(color.getAlpha != 0)
                    color = new Color(color.getRed, color.getGreen, color.getBlue, 80)
                color
            }
        }

        // Output Slot
        components += new GuiComponentColoredZone(110, 29, 28, 28, new Color(0, 0, 0, 0)) {
            override def getDynamicColor = {
                var color = new Color(0, 0, 0, 0)
                for(dir <- EnumFacing.values())
                    breakable {
                        if(tileEntity.getModeForSide(dir) == EnumInputOutputMode.ALL_MODES) {
                            color = EnumInputOutputMode.ALL_MODES.getHighlightColor
                            break
                        } else if (tileEntity.getModeForSide(dir) == EnumInputOutputMode.OUTPUT_ALL)
                            color = EnumInputOutputMode.OUTPUT_ALL.getHighlightColor
                    }
                if(color.getAlpha != 0)
                    color = new Color(color.getRed, color.getGreen, color.getBlue, 80)
                color
            }
        }
    }
}

