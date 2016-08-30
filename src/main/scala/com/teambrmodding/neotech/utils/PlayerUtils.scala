package com.teambrmodding.neotech.utils

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemStack, Item}
import net.minecraft.util.EnumHand

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 3/19/2016
  */
object PlayerUtils {

    def isPlayerHoldingEither(player: EntityPlayer, item: Item): Boolean = {
        if (player == null || item == null || player.getHeldEquipment == null) return false
        if ((player.getHeldItemMainhand != null && player.getHeldItemMainhand.getItem == item )
                || (player.getHeldItemOffhand != null && player.getHeldItemOffhand.getItem == item)) return true
        false
    }

    /**
      * Gets what hand this item is in, this does an object match so you must send the object, not match by values
      * @param stack The object
      * @return What hand its in
      */
    def getHandStackIsIn(player : EntityPlayer, stack : ItemStack) : EnumHand = {
        if(player == null || stack == null) return EnumHand.MAIN_HAND
        if(player.getHeldItemMainhand != null && player.getHeldItemMainhand.equals(stack))
            EnumHand.MAIN_HAND
        else
            EnumHand.OFF_HAND
    }
}
