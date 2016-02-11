package com.dyonovan.neotech.common.items

import com.teambr.bookshelf.client.gui.GuiTextFormat
import net.minecraft.entity.EntityCreature
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{EnumAction, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

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
class ItemAnimalMover extends BaseItem("animalMover", 1) {

    override def onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack = {
        player.setItemInUse(stack, getMaxItemUseDuration(stack))
        stack
    }

    override def getItemUseAction(stack: ItemStack): EnumAction = {
        EnumAction.BOW
    }

    override def getMaxItemUseDuration(stack: ItemStack): Int = {
        7200
    }

    override def hasEffect(stack: ItemStack) = stack.hasTagCompound

    override def onPlayerStoppedUsing(stack: ItemStack, world: World, player: EntityPlayer, timeLeft: Int): Unit = {

        if (timeLeft <= 7180) {
            val mop = getMovingObjectPositionFromPlayer(world, player, false)
            if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && !stack.hasTagCompound) {
                val entity = mop.entityHit
                entity match {
                    case mob: EntityCreature =>
                        val tag = new NBTTagCompound
                        mob.writeEntityToNBT(tag)
                        stack.writeToNBT(tag)
                    case _ =>
                }
            } else if (stack.hasTagCompound) {
                val pos = mop.getBlockPos

            }

        }
    }

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, list: java.util.List[String], boolean: Boolean): Unit = {
        if (stack.hasTagCompound) {
            list.add(GuiTextFormat.ITALICS + "Type: " + stack.getTagCompound.getString("EntityId"))
        }
    }

}
