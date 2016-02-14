package com.dyonovan.neotech.client.gui.misc

import com.dyonovan.neotech.common.container.misc.ContainerMobStand
import com.dyonovan.neotech.common.tiles.misc.TileMobStand
import com.teambr.bookshelf.client.gui.GuiBase
import com.teambr.bookshelf.client.gui.component.control.GuiComponentScrollBar
import com.teambr.bookshelf.client.gui.component.display.GuiComponentText
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.StatCollector

/**
  * Created by Dyonovan on 2/14/2016.
  */
class GuiMobStand(playerInv: InventoryPlayer, tile: TileMobStand) extends
        GuiBase[ContainerMobStand](new ContainerMobStand(playerInv, tile), 175, 165, "neotech.mobStand.title"){

    override def addComponents(): Unit = {
        components += new GuiComponentScrollBar(115, 18, 50) {
            setPosition(tile.scale)
            override def onScroll(position: Float): Unit = {
                tile.scale = position
                tile.sendValueToServer(tile.SIZE, position)
            }
        }
        components += new GuiComponentScrollBar(145, 18, 50) {
            setPosition(tile.rotation)
            override def onScroll(position: Float): Unit = {
                tile.rotation = position
                tile.sendValueToServer(tile.DIRECTION, position)
            }
        }
        components += new GuiComponentText(StatCollector.translateToLocal("neotech.text.size"), 112, 73)
        components += new GuiComponentText(StatCollector.translateToLocal("neotech.text.facing"), 139, 73)
    }
}
