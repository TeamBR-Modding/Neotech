package com.teambrmodding.neotech.managers;

import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem;
import com.teambrmodding.neotech.lib.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 9/6/2016
  */
public class CapabilityLoadManager {

    @CapabilityInject(IUpgradeItem.class)
    public static Capability<IUpgradeItem> UPGRADE_ITEM_CAPABILITY = null;
    public static ResourceLocation UPGRADE_ITEM_KEY = new ResourceLocation(Reference.MOD_ID(), "capability/upgradeItem");

    public static void registerCapabilities() {
        MinecraftForge.EVENT_BUS.register(new CapabilityLoadManager());

        // Register Upgrade Item Capability
        CapabilityManager.INSTANCE.register(IUpgradeItem.class, new IUpgradeItem.Storage(), IUpgradeItem.UpgradeItemDefaultImp.class);
    }

    @SubscribeEvent
    public void onItemLoaded(AttachCapabilitiesEvent.Item event) {
        if(event.getItem() instanceof IUpgradeItem) {
            event.addCapability(UPGRADE_ITEM_KEY, new IUpgradeItem.UpgradeItemDefaultImp((IUpgradeItem) event.getItem()));
        }
    }
}
