package com.teambrmodding.neotech.events

import com.teambrmodding.neotech.managers.ItemManager
import com.teambrmodding.neotech.tools.modifier.ModifierBeheading
import com.teambrmodding.neotech.tools.tools.ElectricSword
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.monster.{EntityCreeper, EntityZombie, EntitySkeleton}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.event.entity.living.{LivingDropsEvent, LivingAttackEvent}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/24/2016
  */
object AttackEvent {
    @SubscribeEvent
    def entityAttacked(event : LivingAttackEvent) : Unit = {
        event.getSource.getSourceOfDamage match {
            case player: EntityPlayer =>
                if (player.getHeldItemMainhand != null && player.getHeldItemMainhand.getItem == ItemManager.electricSword) {
                    val sword = player.getHeldItemMainhand
                    if(sword.getItem.asInstanceOf[ElectricSword].getEnergyStored(sword) <= 0)
                        event.setCanceled(true)
                }
            case _ =>
        }
    }

    @SubscribeEvent
    def onLivingDrop(event : LivingDropsEvent) : Unit = {
        if(event.getEntityLiving == null) return
        if(event.isRecentlyHit && event.getSource.damageType.equals("player")) {
            if(event.getEntityLiving.isInstanceOf[EntitySkeleton] || event.getEntityLiving.isInstanceOf[EntityZombie] ||
                    event.getEntityLiving.isInstanceOf[EntityCreeper] ||event.getEntityLiving.isInstanceOf[EntityPlayer]) {
                val player = event.getSource.getEntity.asInstanceOf[EntityPlayer]
                val stack = player.getHeldItemMainhand
                if(stack != null && ModifierBeheading.getBeheadingLevel(stack) > 0) {
                    val chance = event.getEntityLiving.worldObj.rand.nextInt(100) - ( ModifierBeheading.getBeheadingLevel(stack) * 25)
                    if(chance <= 0) {
                        event.getEntityLiving match {
                            case skeleton : EntitySkeleton =>
                                addDrops(event, new ItemStack(Items.SKULL, 1, skeleton.func_189771_df().ordinal()))
                            case zombie : EntityZombie =>
                                addDrops(event, new ItemStack(Items.SKULL, 1, 2))
                            case creeper : EntityCreeper =>
                                addDrops(event, new ItemStack(Items.SKULL, 1, 4))
                            case player : EntityPlayer =>
                                val stack = new ItemStack(Items.SKULL, 1, 3)
                                val tag = new NBTTagCompound
                                tag.setString("SkullOwner", player.getName)
                                stack.setTagCompound(tag)
                                addDrops(event, stack)
                            case _ =>
                        }
                    }
                }
            }
        }
    }

    def addDrops(event : LivingDropsEvent, stack : ItemStack) : Unit = {
        val item = new EntityItem(event.getEntityLiving.worldObj, event.getEntityLiving.posX, event.getEntityLiving.posY, event.getEntity.posZ, stack)
        item.setPickupDelay(10)
        event.getDrops.add(item)
    }
}
