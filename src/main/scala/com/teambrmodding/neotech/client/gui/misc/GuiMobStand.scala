package com.teambrmodding.neotech.client.gui.misc

import com.teambrmodding.neotech.common.container.misc.ContainerMobStand
import com.teambrmodding.neotech.common.tiles.misc.TileMobStand
import com.teambr.bookshelf.client.gui.GuiBase
import com.teambr.bookshelf.client.gui.component.control.{GuiComponentCheckBox, GuiComponentScrollBar, GuiComponentSlider}
import com.teambr.bookshelf.client.gui.component.display.GuiComponentText
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.text.translation.I18n

import scala.collection.mutable.ArrayBuffer

/**
  * Created by Dyonovan on 2/14/2016.
  */
class GuiMobStand(playerInv: InventoryPlayer, tile: TileMobStand) extends
        GuiBase[ContainerMobStand](new ContainerMobStand(playerInv, tile), 175, 165, "neotech.mobStand.title"){

    override def addComponents(): Unit = {
        components += new GuiComponentScrollBar(150, 18, 50) {
            setPosition(tile.scale)
            override def onScroll(position: Float): Unit = {
                tile.scale = position
                if(tile.scale >= 1)
                    tile.scale = 0.99F
                tile.sendValueToServer(tile.SIZE, position)
            }
        }
        components += new GuiComponentSlider[Int](70, 72, 70, List.range(0, 360 + 1), (tile.rotation * 360).toInt) {
            override def onValueChanged(value: Int): Unit = {
                tile.rotation = value / 360.toFloat
                tile.sendValueToServer(tile.DIRECTION, tile.rotation)
            }
        }
        components += new GuiComponentCheckBox(55, 25, "neotech.text.fitToBlock", tile.fitToBlock) {
            override def setValue(bool: Boolean): Unit = {
                val value = if(bool) 1.0 else 0.0
                tile.fitToBlock = bool
                tile.sendValueToServer(tile.FIT, value)
            }

            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(I18n.translateToLocal("neotech.text.fitToBlockInfo"))
            }
        }
        components += new GuiComponentCheckBox(55, 35, "neotech.text.lookAtPlayer", tile.lookAtPlayer) {
            override def setValue(bool: Boolean): Unit = {
                val value = if(bool) 1.0 else 0.0
                tile.lookAtPlayer = bool
                tile.sendValueToServer(tile.LOOK, value)
            }
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(I18n.translateToLocal("neotech.text.lookAtPlayerInfo"))
            }
        }
        components += new GuiComponentCheckBox(55, 45, "neotech.text.renderName", tile.renderName) {
            override def setValue(bool: Boolean): Unit = {
                val value = if(bool) 1.0 else 0.0
                tile.renderName = bool
                tile.sendValueToServer(tile.NAME, value)
            }
            override def getDynamicToolTip(x: Int, y: Int): ArrayBuffer[String] = {
                ArrayBuffer(I18n.translateToLocal("neotech.text.renderNameInfo"))
            }
        }
        components += new GuiComponentText(I18n.translateToLocal("neotech.text.facing"), 28, 70)
        components += new GuiComponentText(I18n.translateToLocal("neotech.text.size"), 147, 73)
    }
}
