package com.dyonovan.neotech.events

import com.dyonovan.neotech.managers.ItemManager
import com.dyonovan.neotech.tools.tools.ElectricSword
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.event.entity.living.LivingAttackEvent
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
}
