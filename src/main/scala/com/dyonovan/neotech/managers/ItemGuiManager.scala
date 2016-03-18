package com.dyonovan.neotech.managers

import com.dyonovan.neotech.client.gui.misc.GuiTrashBag
import com.dyonovan.neotech.common.container.misc.ContainerTrashBag
import com.dyonovan.neotech.common.items.ItemTrashBag
import com.dyonovan.neotech.pipes.blocks.BlockPipeSpecial
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/26/2016
  */
object ItemGuiManager {
    lazy val TRASH_BAG_GUI_ID = 0
}

class ItemGuiManager extends IGuiHandler {
    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        world.getBlockState(new BlockPos(x, y, z)).getBlock match {
            case block : BlockPipeSpecial =>
                return block.getClientGuiElement(ID, player, world, x, y, z)
            case _ =>
        }

        ID match {
            case ItemGuiManager.TRASH_BAG_GUI_ID =>
                new GuiTrashBag(ItemTrashBag.getInventory(player.getHeldItem), player.inventory, player.getHeldItem)
            case _ => null
        }
    }

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        world.getBlockState(new BlockPos(x, y, z)).getBlock match {
            case block : BlockPipeSpecial =>
                return block.getServerGuiElement(ID, player, world, x, y, z)
            case _ =>
        }

        ID match {
            case ItemGuiManager.TRASH_BAG_GUI_ID =>
                new ContainerTrashBag(ItemTrashBag.getInventory(player.getHeldItem), player.inventory, player.getHeldItem)
            case _ => null
        }
    }
}
