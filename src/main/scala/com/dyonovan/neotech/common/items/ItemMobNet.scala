package com.dyonovan.neotech.common.items

import com.teambr.bookshelf.client.gui.{GuiColor, GuiTextFormat}
import net.minecraft.entity.EntityList
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
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
  * @since 2/12/2016
  */
object ItemMobNet {
    final val instance = new ItemMobNet
}

class ItemMobNet extends BaseItem("mobNet", 16) {

    override def onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack = {
        if (stack.hasTagCompound && !world.isRemote) {
            val mop = getMovingObjectPositionFromPlayer(world, player, false)
            if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                val pos = mop.getBlockPos.offset(mop.sideHit)
                val entity = EntityList.createEntityByName(stack.getTagCompound.getString("type"), world)
                entity.readFromNBT(stack.getTagCompound)
                entity.setPosition(pos.getX, pos.getY, pos.getZ)
                world.spawnEntityInWorld(entity)
                stack.setTagCompound(null)
                entity match {
                    case mob: EntityMob =>
                        mob.setAttackTarget(player)
                    case _ =>
                }
            }
        }
        stack
    }

    override def hasEffect(stack : ItemStack) = stack.hasTagCompound

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, list: java.util.List[String], boolean: Boolean): Unit = {
        if (stack.hasTagCompound){
            list.add(GuiColor.GREEN + stack.getTagCompound.getString("type"))
        } else {
            list.add(GuiTextFormat.ITALICS + GuiColor.RED.toString + "Empty")
        }

    }
}
