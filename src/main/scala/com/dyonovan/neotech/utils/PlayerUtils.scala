package com.dyonovan.neotech.utils

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item

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

}
