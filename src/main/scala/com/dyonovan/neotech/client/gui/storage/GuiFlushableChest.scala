package com.dyonovan.neotech.client.gui.storage

import com.dyonovan.neotech.common.container.storage.ContainerFlushableChest
import com.dyonovan.neotech.common.tiles.storage.TileFlushableChest
import com.teambr.bookshelf.client.gui.GuiBase
import net.minecraft.entity.player.EntityPlayer

/**
  * Created by Dyonovan on 1/22/2016.
  */
class GuiFlushableChest(player: EntityPlayer, tile: TileFlushableChest) extends
        GuiBase[ContainerFlushableChest](new ContainerFlushableChest(player.inventory, tile), 175, 165, "neotech.flushchest.title"){

    override def addComponents(): Unit = { }
}
