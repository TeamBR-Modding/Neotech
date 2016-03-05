package com.dyonovan.neotech.client

import com.dyonovan.neotech.tools.ToolHelper
import com.dyonovan.neotech.tools.armor.ItemElectricArmor
import com.dyonovan.neotech.tools.tools.BaseElectricTool
import com.dyonovan.neotech.utils.ClientUtils
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.{ClientTickEvent, Phase}

import scala.collection.mutable.ArrayBuffer

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 3/4/2016
  */
object ClientTickHandler {

    @SubscribeEvent
    def clientTickEnd(event : ClientTickEvent): Unit = {
        if(event.phase == Phase.END) {
            if(KeybindHandler.radialMenu.isKeyDown)
                KeybindHandler.keyPressed(KeybindHandler.radialMenu)
        }
    }

    def player = Minecraft.getMinecraft.thePlayer

    def getUpgrades : ArrayBuffer[(String, Boolean, String, Int, ItemStack)] = {
        val buffer = new ArrayBuffer[(String, Boolean, String, Int, ItemStack)]()

        val itemBuffer = new ArrayBuffer[(Int, ItemStack)]()

        // Get Held Item
        if(player.getHeldItem != null && player.getHeldItem.getItem.isInstanceOf[BaseElectricTool]) {
            val id = player.inventory.currentItem
            val stack =  player.getHeldItem
            val tup = (id, stack)
            itemBuffer += tup
        }

        // Get Armor Items
        for(i <- 0 until player.inventory.armorInventory.length) {
            if(player.inventory.armorInventory(i) != null &&
                    player.inventory.armorInventory(i).getItem.isInstanceOf[ItemElectricArmor]) {
                val stack = player.inventory.armorInventory(i)
                val tup = (i + 10, stack)
                itemBuffer += tup
            }
        }

        for(stack <- itemBuffer) {
            val tagList = ToolHelper.getModifierTagList(stack._2)
            if (tagList != null && tagList.tagCount() > 0) {
                for (x <- 0 until tagList.tagCount()) {
                    val tag = tagList.getCompoundTagAt(x)
                    if (tag.hasKey("Active")) {
                        val name = ClientUtils.translate(tag.getString("ModifierID"))
                        val active = tag.getBoolean("Active")
                        val id = tag.getString("ModifierID")
                        val tuple = (name, active, id, stack._1, stack._2)
                        buffer += tuple
                    }
                }
            }
        }

        buffer
    }
}
