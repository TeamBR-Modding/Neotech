package com.teambrmodding.neotech;

import com.teambrmodding.neotech.client.ClientProxy;
import com.teambrmodding.neotech.common.CommonProxy;
import com.teambrmodding.neotech.lib.Reference;
import com.teambrmodding.neotech.managers.LootManager;
import com.teambrmodding.neotech.managers.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This file was created for Neotech
 *
 * Neotech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author James Rogers - Dyonovan
 * @since ${DATE}
 */
@Mod(Reference.MOD_ID)
public class Neotech {

    public static CommonProxy common = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public Neotech() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> ScreenManager::registerScreens);

        MinecraftForge.EVENT_BUS.register(new LootManager());
    }
}
