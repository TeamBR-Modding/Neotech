package com.dyonovan.neotech.common.items

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.managers.{ItemGuiManager, ItemManager}
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.common.tiles.traits.Inventory
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.event.entity.player.EntityItemPickupEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.util.control.Breaks._

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
class ItemTrashBag extends BaseItem("trashBag", 1) {

    override def onItemRightClick(stack : ItemStack, world : World, player : EntityPlayer) : ItemStack = {
        if(!world.isRemote)
            player.openGui(NeoTech, ItemGuiManager.TRASH_BAG_GUI_ID, world, player.posX.toInt, player.posY.toInt, player.posZ.toInt)
        stack
    }

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, list: java.util.List[String], boolean: Boolean): Unit = {
        if (stack.hasTagCompound) {
            val inventory = ItemTrashBag.getInventory(stack)
            if(inventory.getStackInSlot(0) != null) {
                list.add(GuiColor.YELLOW + "Trashing: " + GuiColor.WHITE + inventory.getStackInSlot(0).getDisplayName)
            }
        }
    }
}

object ItemTrashBag {

    def getInventory(stack : ItemStack) : Inventory = {
        if(stack.getItem == ItemManager.trashBag)
            return new TrashBagInventory(stack)
        null
    }

    class TrashBagInventory(stack : ItemStack) extends Inventory {
        if(stack != null && stack.hasTagCompound)
            setInventorySlotContents(0, ItemStack.loadItemStackFromNBT(stack.getTagCompound))
        override def initialSize: Int = 1
    }

    @SubscribeEvent
    def onItemPickUp(event : EntityItemPickupEvent): Unit = {
        val player = event.entityPlayer
        val pickedUp = event.item.getEntityItem

        if(pickedUp == null || player == null) return

        breakable {
            for (x <- 0 until player.inventory.getSizeInventory) {
                val stack = player.inventory.getStackInSlot(x)

                if (stack != null && stack.getItem == ItemManager.trashBag && stack.hasTagCompound) {
                    val trashBagInventory = new TrashBagInventory(stack)
                    val containedStack = trashBagInventory.getStackInSlot(0)
                    if (containedStack != null) {
                        if (containedStack.getItem == pickedUp.getItem && containedStack.getItemDamage == pickedUp.getItemDamage &&
                                ItemStack.areItemStackTagsEqual(containedStack, pickedUp)) {
                            pickedUp.stackSize = 0
                            break
                        }
                    }
                }
            }
        }
    }
}
