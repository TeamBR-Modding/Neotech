package com.dyonovan.neotech.events

import com.dyonovan.neotech.managers.{BlockManager, ItemManager}
import com.dyonovan.neotech.tools.tools.BaseElectricTool
import net.minecraft.item.Item
import net.minecraft.nbt.NBTTagCompound
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
        // Mob Gun
        if (event.crafting.getItem == ItemManager.mobGun) {
            val tag = new NBTTagCompound
            var tier = 1
            event.craftMatrix.getStackInSlot(8).getItem match {
                case ItemManager.basicRFBattery => tier = 1
                case ItemManager.advancedRFBattery => tier = 2
                case ItemManager.eliteRFBattery => tier = 3
            }
            if (event.craftMatrix.getStackInSlot(8).hasTagCompound && event.craftMatrix.getStackInSlot(8).getTagCompound.hasKey("Energy"))
                tag.setInteger("Energy", event.craftMatrix.getStackInSlot(8).getTagCompound.getInteger("Energy"))
            tag.setInteger("Tier", tier)
            event.crafting.setTagCompound(tag)
            return
        }
        //Electric Tools
        if (event.crafting.getItem.isInstanceOf[BaseElectricTool]) {
            val tag = new NBTTagCompound
            var capacity = 25000
            var extract = 250
            event.craftMatrix.getStackInSlot(7).getItem match {
                case ItemManager.advancedRFBattery =>
                    capacity = ItemManager.advancedRFBattery.capacity
                    extract = ItemManager.advancedRFBattery.maxExtract
                case ItemManager.eliteRFBattery =>
                    capacity = ItemManager.eliteRFBattery.capacity
                    extract = ItemManager.eliteRFBattery.maxExtract
                case _ =>
            }
            if (event.craftMatrix.getStackInSlot(4).hasTagCompound && event.craftMatrix.getStackInSlot(4).getTagCompound.hasKey("Energy"))
                tag.setInteger("Energy", event.craftMatrix.getStackInSlot(8).getTagCompound.getInteger("Energy"))
            tag.setInteger("EnergyCapacity", capacity)
            tag.setInteger("MaxExtract", extract)
            tag.setInteger("MaxReceive", extract)
            event.crafting.setTagCompound(tag)
            return
        }
        if (event.craftMatrix.getStackInSlot(4) != null && event.craftMatrix.getStackInSlot(4).hasTagCompound) {
            if (event.crafting.getItem == Item.getItemFromBlock(BlockManager.advancedRFStorage) ||
              event.crafting.getItem == Item.getItemFromBlock(BlockManager.eliteRFStorage) ||
              event.crafting.getItem == Item.getItemFromBlock(BlockManager.goldTank) ||
              event.crafting.getItem == Item.getItemFromBlock(BlockManager.diamondTank) ||
              event.crafting.getItem == ItemManager.advancedRFBattery ||
              event.crafting.getItem == ItemManager.eliteRFBattery) {
                val tag = event.craftMatrix.getStackInSlot(4).getTagCompound
                tag.setInteger("Tier", tag.getInteger("Tier") + 1)
                event.crafting.setTagCompound(tag)
            }
        }
    }
}
