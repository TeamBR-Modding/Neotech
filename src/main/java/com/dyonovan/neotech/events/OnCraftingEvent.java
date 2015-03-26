package com.dyonovan.neotech.events;

import com.dyonovan.neotech.handlers.BlockHandler;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class OnCraftingEvent {
    @SubscribeEvent
    public void onCrafted(PlayerEvent.ItemCraftedEvent event) {
        if(event.craftMatrix.getStackInSlot(4) != null && event.craftMatrix.getStackInSlot(4).hasTagCompound() ) {
            if(event.crafting.getItem() == Item.getItemFromBlock(BlockHandler.goldTank) ||
                    event.crafting.getItem() == Item.getItemFromBlock(BlockHandler.diamondTank)) {
                NBTTagCompound tag = event.craftMatrix.getStackInSlot(4).getTagCompound();
                event.crafting.setTagCompound(tag);
            }
        }
    }
}
