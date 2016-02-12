package com.dyonovan.neotech.common.items

import com.dyonovan.neotech.common.entities.EntityNet
import com.dyonovan.neotech.managers.ItemManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{EnumAction, ItemStack}
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.util.Random

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/10/2016
  */
class ItemMobGun extends BaseItem("mobGun", 1) {

    override def onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack = {
            if (hasAmmo(player, remove = false)) {
                player.setItemInUse(stack, getMaxItemUseDuration(stack))
            }
            else world.playSoundAtEntity(player, "fire.ignite", 0.5F, 0.4F / (new Random().nextFloat() * 0.4F + 0.8F))
        stack
    }

    private def hasAmmo(player: EntityPlayer, remove: Boolean): Boolean = {
        for (i <- 0 until player.inventory.getSizeInventory) {
            if (player.inventory.getStackInSlot(i) != null && !player.inventory.getStackInSlot(i).hasTagCompound &&
              player.inventory.getStackInSlot(i).getItem.isInstanceOf[ItemManager.mobNet.type]) {
                if (remove) player.inventory.decrStackSize(i, 1)
                return true
            }
        }
        false
    }

    override def getItemUseAction(stack: ItemStack): EnumAction = {
        EnumAction.BOW
    }

    override def getMaxItemUseDuration(stack: ItemStack): Int = 7200

    override def onPlayerStoppedUsing(stack: ItemStack, world: World, player: EntityPlayer, timeLeft: Int): Unit = {
        if (!world.isRemote) {
            hasAmmo(player, remove = true)
            val net = new EntityNet(world, player)
            world.spawnEntityInWorld(net)
            world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (new Random().nextFloat() * 0.4F + 0.8F))
        }
    }

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, list: java.util.List[String], boolean: Boolean): Unit = {

    }
}
