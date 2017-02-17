package com.teambrmodding.neotech.managers;

import com.teambrmodding.neotech.Neotech;
import com.teambrmodding.neotech.lib.Reference;
import com.teambrmodding.neotech.world.OreGeneratorManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/15/2017
 */
public class ConfigManager {
    // A public instance of this config object
    public static Configuration config = new Configuration(new File(Neotech.configFolderLocation + File.separator + "Neotech.cfg"));

    // Ore Gen Variables
    public static boolean genCopper, genTin, genLead, genSilver;

    // Copper Gen
    public static int copperMin, copperMax, copperSize, copperPerChunk;

    // Tin Gen
    public static int tinMin, tinMax, tinSize, tinPerChunk;

    // Lead Gen
    public static int leadMin, leadMax, leadSize, leadPerChunk;

    // Silver Gen
    public static int silverMin, silverMax, silverSize, silverPerChunk;

    // Ore gen blacklist
    public static int[] oreGenDimensionBlacklist;

    // Version check
    public static boolean versionCheck;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void preInit() {
        MinecraftForge.EVENT_BUS.register(new ConfigManager());

        config.load();

        oreGenDimensionBlacklist    = config.get(Reference.CONFIG_WORLD, "dimBlacklist", new int[] {1, -1}, "Dimensions not to spawn ore").getIntList();

        genCopper       = config.get(Reference.CONFIG_WORLD, "copperEnable", true, "Generate Copper").getBoolean();
        copperMin       = config.get(Reference.CONFIG_WORLD, "copperMin", 40, "Copper Min Level").getInt();
        copperMax       = config.get(Reference.CONFIG_WORLD, "copperMax", 70, "Copper Max Level").getInt();
        copperSize      = config.get(Reference.CONFIG_WORLD, "copperVeinSize", 12, "Copper Vein Size").getInt();
        copperPerChunk  = config.get(Reference.CONFIG_WORLD, "copperVeinsPerChunk", 6, "Copper Veins per Chunk").getInt();
        if(!genCopper)
            OreGeneratorManager.INSTANCE.disableOre(MetalManager.getMetal(MetalManager.COPPER).getOreBlock());
        else
            OreGeneratorManager.INSTANCE.registerGeneratingOre(
                    new OreGeneratorManager.GeneratingOre(copperMin, copperMax, copperSize, copperPerChunk,
                            MetalManager.getMetal(MetalManager.COPPER).getOreBlock()));

        genTin       = config.get(Reference.CONFIG_WORLD, "tinEnable", true, "Generate Tin").getBoolean();
        tinMin       = config.get(Reference.CONFIG_WORLD, "tinMin", 20, "Tin Min Level").getInt();
        tinMax       = config.get(Reference.CONFIG_WORLD, "tinMax", 50, "Tin Max Level").getInt();
        tinSize      = config.get(Reference.CONFIG_WORLD, "tinVeinSize", 12, "Tin Vein Size").getInt();
        tinPerChunk  = config.get(Reference.CONFIG_WORLD, "tinVeinsPerChunk", 6, "Tin Veins per Chunk").getInt();
        if(!genTin)
            OreGeneratorManager.INSTANCE.disableOre(MetalManager.getMetal(MetalManager.TIN).getOreBlock());
        else
            OreGeneratorManager.INSTANCE.registerGeneratingOre(
                    new OreGeneratorManager.GeneratingOre(tinMin, tinMax, tinSize, tinPerChunk,
                            MetalManager.getMetal(MetalManager.TIN).getOreBlock()));

        genLead       = config.get(Reference.CONFIG_WORLD, "leadEnable", true, "Generate Lead").getBoolean();
        leadMin       = config.get(Reference.CONFIG_WORLD, "leadMin", 20, "Lead Min Level").getInt();
        leadMax       = config.get(Reference.CONFIG_WORLD, "leadMax", 50, "Lead Max Level").getInt();
        leadSize      = config.get(Reference.CONFIG_WORLD, "leadVeinSize", 12, "Lead Vein Size").getInt();
        leadPerChunk  = config.get(Reference.CONFIG_WORLD, "leadVeinsPerChunk", 6, "Lead Veins per Chunk").getInt();
        if(!genLead)
            OreGeneratorManager.INSTANCE.disableOre(MetalManager.getMetal(MetalManager.LEAD).getOreBlock());
        else
            OreGeneratorManager.INSTANCE.registerGeneratingOre(
                    new OreGeneratorManager.GeneratingOre(leadMin, leadMax, leadSize, leadPerChunk,
                            MetalManager.getMetal(MetalManager.LEAD).getOreBlock()));

        genSilver       = config.get(Reference.CONFIG_WORLD, "silverEnable", true, "Generate Silver").getBoolean();
        silverMin       = config.get(Reference.CONFIG_WORLD, "silverMin", 20, "Silver Min Level").getInt();
        silverMax       = config.get(Reference.CONFIG_WORLD, "silverMax", 50, "Silver Max Level").getInt();
        silverSize      = config.get(Reference.CONFIG_WORLD, "silverVeinSize", 12, "Silver Vein Size").getInt();
        silverPerChunk  = config.get(Reference.CONFIG_WORLD, "silverVeinsPerChunk", 6, "Silver Veins per Chunk").getInt();
        if(!genSilver)
            OreGeneratorManager.INSTANCE.disableOre(MetalManager.getMetal(MetalManager.SILVER).getOreBlock());
        else
            OreGeneratorManager.INSTANCE.registerGeneratingOre(
                    new OreGeneratorManager.GeneratingOre(silverMin, silverMax, silverSize, silverPerChunk,
                            MetalManager.getMetal(MetalManager.SILVER).getOreBlock()));

        versionCheck = config.get(Reference.CONFIG_CLIENT, "versionCheck", true, "Enable Version Check?").getBoolean();

        config.save();

        //Check to make sure Registry Directory is made
        File path = new File(Neotech.configFolderLocation + File.separator + "Registries");
        if (!path.exists())
            path.mkdirs();
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if(event.getModID().equalsIgnoreCase(Reference.MOD_ID) && config.hasChanged())
            config.save();
    }
}
