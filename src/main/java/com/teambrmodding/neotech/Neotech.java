package com.teambrmodding.neotech;

import com.teambrmodding.neotech.collections.CreativeTabMetals;
import com.teambrmodding.neotech.common.CommonProxy;
import com.teambrmodding.neotech.lib.Reference;
import com.teambrmodding.neotech.managers.*;
import com.teambrmodding.neotech.network.PacketDispatcher;
import com.teambrmodding.neotech.world.OreGeneratorManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/15/2017
 */
@Mod(modid          = Reference.MOD_ID,
        name           = Reference.MOD_NAME,
        version        = Reference.VERSION,
        dependencies   = Reference.DEPENDENCIES,
        updateJSON     = Reference.UPDATE_JSON,
        guiFactory     = "com.teambrmodding.neotech.client.ingameconfig.GuiFactoryNeotech")
public class Neotech {

    // Enable the bucket on creation
    static {
        FluidRegistry.enableUniversalBucket();
    }

    @Mod.Instance
    public static Neotech INSTANCE;

    public static String configFolderLocation;

    @SidedProxy(
            clientSide = "com.teambrmodding.neotech.client.ClientProxy",
            serverSide = "com.teambrmodding.neotech.common.CommonProxy"
    )
    public static CommonProxy proxy;

    // Our main Creative Tab
    public static CreativeTabs tabNeotech = new CreativeTabs("tabNeotech") {
        @Override
        public Item getTabIconItem() {
            return Item.getItemFromBlock(BlockManager.eliteRFStorage);
        }
    };

    // Our metals tab
    public static CreativeTabMetals tabMetals = new CreativeTabMetals();

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        CapabilityLoadManager.registerCapabilities();
        configFolderLocation = event.getModConfigurationDirectory().getAbsolutePath() + File.separator + "Neotech";

        BlockManager.preInit();
        ItemManager.preInit();
        FluidManager.preInit();
        MetalManager.registerDefaultMetals();
        RecipeManager.preInit();

        ConfigManager.preInit();
        GameRegistry.registerWorldGenerator(OreGeneratorManager.INSTANCE, 2);

        proxy.preInit();
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event) {
        RecipeManager.init();
        PacketDispatcher.initPackets();
        proxy.init();
    }

    @Mod.EventHandler
    public static void posInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }

    @Mod.EventHandler
    public static void serverLoad(FMLServerStartingEvent event) {
        RecipeManager.initCommands((ServerCommandManager) event.getServer().getCommandManager());
    }
}
