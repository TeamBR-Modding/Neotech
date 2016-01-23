package com.dyonovan.neotech.pipes.gui

import com.dyonovan.neotech.pipes.container.ContainerPipeFilter
import com.dyonovan.neotech.pipes.tiles.item.{ItemSinkPipe, ItemExtractionPipe}
import com.dyonovan.neotech.pipes.types.AdvancedPipe
import com.teambr.bookshelf.client.gui.GuiBase
import com.teambr.bookshelf.client.gui.component.control.{GuiComponentCheckBox, GuiComponentButton}
import net.minecraft.entity.player.EntityPlayer

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/22/2016
  */
class GuiPipeFilter(player : EntityPlayer, tile : AdvancedPipe) extends
    GuiBase[ContainerPipeFilter](new ContainerPipeFilter(player.inventory, tile), 175, 165, "Filter") {
    override def addComponents(): Unit = {
        components += new GuiComponentButton(if(tile.isInstanceOf[ItemExtractionPipe] || tile.isInstanceOf[ItemSinkPipe]) 8 else 61, 45, 50, 20, if(tile.blackList) "Blacklist" else "Whitelist") {
            override def doAction(): Unit = {
                tile.setVariable(AdvancedPipe.FILTER, AdvancedPipe.FILTER_BLACKLIST)
                tile.sendValueToServer(AdvancedPipe.FILTER, AdvancedPipe.FILTER_BLACKLIST)
            }
            override def renderOverlay(i : Int, j : Int) = {
                setText(if(tile.blackList) "Blacklist" else "Whitelist")
                super.renderOverlay(i, j)
            }
        }
        if(tile.isInstanceOf[ItemExtractionPipe] || tile.isInstanceOf[ItemSinkPipe]) {
            components += new GuiComponentCheckBox(8, 70, "Ore Dict", tile.matchOreDict) {
                override def setValue(bool: Boolean): Unit = {
                    tile.setVariable(AdvancedPipe.FILTER, AdvancedPipe.FILTER_MATCH_ORE)
                    tile.sendValueToServer(AdvancedPipe.FILTER, AdvancedPipe.FILTER_MATCH_ORE)
                }
            }
            components += new GuiComponentCheckBox(100, 55, "Damage", tile.matchDamage) {
                override def setValue(bool: Boolean): Unit = {
                    tile.setVariable(AdvancedPipe.FILTER, AdvancedPipe.FILTER_MATCH_DAMAGE)
                    tile.sendValueToServer(AdvancedPipe.FILTER, AdvancedPipe.FILTER_MATCH_DAMAGE)
                }
            }
            components += new GuiComponentCheckBox(100, 70, "NBT", tile.matchTag) {
                override def setValue(bool: Boolean): Unit = {
                    tile.setVariable(AdvancedPipe.FILTER, AdvancedPipe.FILTER_MATCH_TAG)
                    tile.sendValueToServer(AdvancedPipe.FILTER, AdvancedPipe.FILTER_MATCH_TAG)
                }
            }
        }
    }
}
