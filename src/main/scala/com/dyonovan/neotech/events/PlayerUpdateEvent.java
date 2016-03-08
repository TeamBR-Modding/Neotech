package com.dyonovan.neotech.events;

import com.dyonovan.neotech.tools.armor.ItemElectricArmor;
import com.dyonovan.neotech.tools.modifier.ModifierSprinting;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis "pauljoda"
 * @since 3/8/2016
 */
public class PlayerUpdateEvent {

    private static ItemStack[] lastArmorStored = new ItemStack[4];
    private static AttributeModifier speedBonus = new AttributeModifier("Speed Bonus", 2.0, 2).setSaved(false);


    @SubscribeEvent
    public void playerUpdate(TickEvent.PlayerTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            for(int x = 0; x < 4; x++) {
                if (!ItemStack.areItemStacksEqual(lastArmorStored[x], event.player.inventory.armorInventory[x])) {
                    lastArmorStored[x] = event.player.inventory.armorInventory[x];
                    switch (x) {
                        case 1 :
                            if(lastArmorStored[x] == null)
                                event.player.stepHeight = 0.5F;
                            else if(lastArmorStored[x].getItem() instanceof ItemElectricArmor) {
                                int speedValue = ModifierSprinting.getSprintingLevel(lastArmorStored[x]);
                                if(speedValue > 0) {
                                    event.player.stepHeight = 1;
                                } else
                                    event.player.stepHeight = 0.5F;
                            }
                        default :
                    }
                }
            }
        }
    }
}
