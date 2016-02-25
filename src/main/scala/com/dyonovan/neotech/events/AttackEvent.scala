package com.dyonovan.neotech.events

import com.dyonovan.neotech.managers.ItemManager
import com.dyonovan.neotech.tools.modifier.ModifierBeheading
import com.dyonovan.neotech.tools.tools.ElectricSword
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
        event.source.getSourceOfDamage match {
            case player: EntityPlayer =>
                if (player.getCurrentEquippedItem != null && player.getCurrentEquippedItem.getItem == ItemManager.electricSword) {
                    val sword = player.getCurrentEquippedItem
                    if(sword.getItem.asInstanceOf[ElectricSword].getEnergyStored(sword) <= 0)
                        event.setCanceled(true)
                }
            case _ =>
        }
    }

    @SubscribeEvent
    def onLivingDrop(event : LivingDropsEvent) : Unit = {
        if(event.entityLiving == null) return
        if(event.recentlyHit && event.source.damageType.equals("player")) {
            if(event.entityLiving.isInstanceOf[EntitySkeleton] || event.entityLiving.isInstanceOf[EntityZombie] ||
                    event.entityLiving.isInstanceOf[EntityCreeper] ||event.entityLiving.isInstanceOf[EntityPlayer]) {
                val player = event.source.getEntity.asInstanceOf[EntityPlayer]
                val stack = player.getCurrentEquippedItem
                if(stack != null && ModifierBeheading.getBeheadingLevel(stack) > 0) {
                    val chance = event.entityLiving.worldObj.rand.nextInt(100) - ( ModifierBeheading.getBeheadingLevel(stack) * 25)
                    if(chance <= 0) {
                        event.entityLiving match {
                            case skeleton : EntitySkeleton =>
                                addDrops(event, new ItemStack(Items.skull, 1, skeleton.getSkeletonType))
                            case zombie : EntityZombie =>
                                addDrops(event, new ItemStack(Items.skull, 1, 2))
                            case player : EntityPlayer =>
                                val stack = new ItemStack(Items.skull, 1, 4)
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
        val item = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entity.posZ, stack)
        item.setPickupDelay(10)
        event.drops.add(item)
    }
}
