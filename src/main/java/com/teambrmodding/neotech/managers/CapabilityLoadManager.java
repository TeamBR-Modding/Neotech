package com.teambrmodding.neotech.managers;

import com.teambrmodding.neotech.common.traits.IUpgradeItem;
import com.teambrmodding.neotech.lib.Reference;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 9/15/19
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class CapabilityLoadManager {

    /**
     * Public instance of the capability, use this to check for if an item has this capability
     */
    @CapabilityInject(IUpgradeItem.class)
    public static Capability<IUpgradeItem> UPGRADE_ITEM_CAPABILITY;

    /**
     * The key that defines the location for this capability
     */
    public static ResourceLocation UPGRADE_ITEM_KEY
            = new ResourceLocation(Reference.MOD_ID, "capability/upgrade_item");

    /**
     * Registers the capability to the forge registry
     */
    public static void registerCapabilities() {
        CapabilityManager.INSTANCE.register(IUpgradeItem.class, new IUpgradeItem.Storage(), IUpgradeItem.UpgradeItemDefaultImp::new);
    }

    /**
     * When an item is created, if it is an upgrade item we want to attach the capability
     * @param event Item attach event
     */
    @SubscribeEvent
    public void onItemLoaded(AttachCapabilitiesEvent<Item> event) {
        if(event.getObject() instanceof IUpgradeItem)
            event.addCapability(UPGRADE_ITEM_KEY,
                    new IUpgradeItem.UpgradeItemDefaultImp((IUpgradeItem) event.getObject()));
    }
}
