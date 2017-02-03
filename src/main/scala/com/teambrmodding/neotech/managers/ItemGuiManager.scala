package com.teambrmodding.neotech.managers

import com.teambrmodding.neotech.client.gui.misc.GuiTrashBag
import com.teambrmodding.neotech.common.container.misc.ContainerTrashBag
import com.teambrmodding.neotech.common.items.ItemTrashBag
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
        ID match {
            case ItemGuiManager.TRASH_BAG_GUI_ID =>
                if(player.getHeldItemMainhand != null && player.getHeldItemMainhand.getItem == ItemManager.trashBag)
                    new GuiTrashBag(ItemTrashBag.getInventory(player.getHeldItemMainhand), player.inventory, player.getHeldItemMainhand)
                else if(player.getHeldItemOffhand != null && player.getHeldItemOffhand.getItem == ItemManager.trashBag)
                    new GuiTrashBag(ItemTrashBag.getInventory(player.getHeldItemOffhand), player.inventory, player.getHeldItemOffhand)
                else
                    null
            case _ => null
        }
    }

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        ID match {
            case ItemGuiManager.TRASH_BAG_GUI_ID =>
                if(player.getHeldItemMainhand != null && player.getHeldItemMainhand.getItem == ItemManager.trashBag)
                    new ContainerTrashBag(ItemTrashBag.getInventory(player.getHeldItemMainhand), player.inventory, player.getHeldItemMainhand)
                else if(player.getHeldItemOffhand != null && player.getHeldItemOffhand.getItem == ItemManager.trashBag)
                    new ContainerTrashBag(ItemTrashBag.getInventory(player.getHeldItemOffhand), player.inventory, player.getHeldItemOffhand)
                else
                    null
            case _ => null
        }
    }
}
