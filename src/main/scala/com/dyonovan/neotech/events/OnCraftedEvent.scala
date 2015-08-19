package com.dyonovan.neotech.events

import com.dyonovan.neotech.managers.BlockManager
import net.minecraft.item.Item
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 16, 2015
 */
object OnCraftedEvent {

    @SubscribeEvent
    def onCrafted(event: PlayerEvent.ItemCraftedEvent): Unit = {
        if(event.craftMatrix.getStackInSlot(4) != null && event.craftMatrix.getStackInSlot(4).hasTagCompound) {
            if(event.crafting.getItem == Item.getItemFromBlock(BlockManager.advancedRFStorage) ||
                    event.crafting.getItem == Item.getItemFromBlock(BlockManager.eliteRFStorage) ||
                    event.crafting.getItem == Item.getItemFromBlock(BlockManager.goldTank) ||
                    event.crafting.getItem == Item.getItemFromBlock(BlockManager.diamondTank)) {
                val tag = event.craftMatrix.getStackInSlot(4).getTagCompound
                event.crafting.setTagCompound(tag)
            }
        }
    }
}
