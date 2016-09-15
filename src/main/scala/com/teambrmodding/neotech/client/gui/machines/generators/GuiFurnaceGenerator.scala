package com.teambrmodding.neotech.client.gui.machines.generators

import java.awt.Color
import java.text.NumberFormat
import java.util.Locale

import com.teambrmodding.neotech.client.gui.machines.GuiAbstractMachine
import com.teambrmodding.neotech.collections.EnumInputOutputMode
import com.teambrmodding.neotech.common.container.machines.generators.ContainerFurnaceGenerator
import com.teambrmodding.neotech.common.tiles.machines.generators.TileFurnaceGenerator
import com.teambrmodding.neotech.utils.ClientUtils
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.client.gui.component.display._
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.text.translation.I18n

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

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
                        .format(tileEntity.burnTime / tileEntity.getMultiplierByCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY)) + " ticks left.")
            }
        }

        //Energy Stored
        components += new GuiComponentPowerBarGradient(7, 18, 18, 60, new Color(255, 0, 0)) {
            addColor(new Color(255, 150, 0))
            addColor(new Color(255, 255, 0))


            override def getEnergyPercent(scale: Int): Int = {
                tileEntity.getEnergyStored(null) * scale / tileEntity.getMaxEnergyStored(null)
            }
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                val buffer = new ArrayBuffer[String]()
                buffer += GuiColor.ORANGE + I18n.translateToLocal("neotech.text.redstoneFlux")
                buffer += ClientUtils.formatNumber(tileEntity.getEnergyStored(null)) + " / " +
                        ClientUtils.formatNumber(tileEntity.getMaxEnergyStored(null)) + " RF"
                buffer
            }
        }

        // Slot Backdrop
        components += new GuiComponentColoredZone(76, 33, 20, 20, new Color(0, 0, 0, 0)) {
            override def getDynamicColor = {
                var color = new Color(0, 0, 0, 0)
                breakable {
                    for(dir <- EnumFacing.values())
                        if(tileEntity.getModeForSide(dir) == EnumInputOutputMode.INPUT_ALL) {
                            color = EnumInputOutputMode.INPUT_ALL.getHighlightColor
                            break
                        } else if(tileEntity.getModeForSide(dir) == EnumInputOutputMode.INPUT_PRIMARY)
                            color = EnumInputOutputMode.INPUT_PRIMARY.getHighlightColor
                }
                if(color.getAlpha != 0)
                    color = new Color(color.getRed, color.getGreen, color.getBlue, 80)
                color
            }
        }

        // Tank Backdrop
        components += new GuiComponentColoredZone(149, 17, 20, 62, new Color(0, 0, 0, 0)) {
            override def getDynamicColor = {
                var color = new Color(0, 0, 0, 0)
                breakable {
                for(dir <- EnumFacing.values())
                    if(tileEntity.getModeForSide(dir) == EnumInputOutputMode.INPUT_ALL) {
                        color = EnumInputOutputMode.INPUT_ALL.getHighlightColor
                        break
                    } else if(tileEntity.getModeForSide(dir) == EnumInputOutputMode.INPUT_SECONDARY)
                        color = EnumInputOutputMode.INPUT_SECONDARY.getHighlightColor
                }
                if(color.getAlpha != 0)
                    color = new Color(color.getRed, color.getGreen, color.getBlue, 80)
                color
            }
        }

        //Oxygen Fluid
        components += new GuiComponentFluidTank(150, 18, 18, 60, tileEntity.tanks(tileEntity.OXYGEN_TANK)) {
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                val buffer = new ArrayBuffer[String]()
                buffer += (if(tileEntity.tanks(tileEntity.OXYGEN_TANK).getFluid != null)
                    GuiColor.ORANGE + tileEntity.tanks(tileEntity.OXYGEN_TANK).getFluid.getLocalizedName
                else
                    GuiColor.RED + "Empty")
                buffer += ClientUtils.formatNumber(tileEntity.tanks(tileEntity.OXYGEN_TANK).getFluidAmount) + " / " +
                        ClientUtils.formatNumber(tileEntity.tanks(tileEntity.OXYGEN_TANK).getCapacity) + " mb"
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
