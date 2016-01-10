package com.dyonovan.neotech.client.gui.machines

import com.dyonovan.neotech.common.blocks.traits.Upgradeable
import com.dyonovan.neotech.common.container.machines.ContainerMachineUpgrade
import com.teambr.bookshelf.client.gui.GuiBase
import net.minecraft.entity.player.EntityPlayer

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/9/2016
  */
class GuiMachineUpgrade(player: EntityPlayer, tileEntity: Upgradeable) extends
    GuiBase[ContainerMachineUpgrade](new ContainerMachineUpgrade(player.inventory, tileEntity), 175, 165, "neotech.upgrade.title") {
    override def addComponents(): Unit = {}
}
