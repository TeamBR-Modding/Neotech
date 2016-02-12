package com.dyonovan.neotech.common.items

import com.dyonovan.neotech.common.entities.EntityNet
import com.dyonovan.neotech.managers.ItemManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{EnumAction, ItemStack}
import net.minecraft.nbt.NBTTagCompound
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

    private def setTag(isLoaded: Boolean): NBTTagCompound = {
        val tag = new NBTTagCompound
        tag.setBoolean("isLoaded", isLoaded)
        tag
    }

    override def onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack = {
        if (stack.hasTagCompound && stack.getTagCompound.getBoolean("isLoaded"))
            player.setItemInUse(stack, getMaxItemUseDuration(stack))
        else if (!world.isRemote) {
            val actual = player.inventory.clearMatchingItems(ItemManager.mobNet, 0, 1, null)
            if (actual > 0) {
                stack.setTagCompound(setTag(true))
                world.updateEntity(player)
            }
            else world.playSoundAtEntity(player, "fire.ignite", 0.5F, 0.4F / (new Random().nextFloat() * 0.4F + 0.8F))
        }
        stack
    }

    override def getItemUseAction(stack: ItemStack): EnumAction = {
        EnumAction.BOW
    }

    override def getMaxItemUseDuration(stack: ItemStack): Int = 7200

    override def hasEffect(stack: ItemStack): Boolean = {
        if (stack.hasTagCompound) {
            return stack.getTagCompound.getBoolean("isLoaded")
        }
        false
    }

    override def onPlayerStoppedUsing(stack: ItemStack, world: World, player: EntityPlayer, timeLeft: Int): Unit = {
        if (!world.isRemote) {
            val net = new EntityNet(world, player, new ItemStack(ItemManager.mobNet, 1))
            world.spawnEntityInWorld(net)
            world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (new Random().nextFloat() * 0.4F + 0.8F))
            stack.setTagCompound(setTag(false))
        }
    }

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, list: java.util.List[String], boolean: Boolean): Unit = {

    }



}
