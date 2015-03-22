package com.dyonovan.jatm.handlers;

import com.dyonovan.jatm.lib.Constants;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

    public static boolean genCopper;
    public static int copperMax, copperMin, copperSize, copperPerChunk;

    public static void init(Configuration config) {

        config.load();

        genCopper       = config.get(Constants.CONFIG_ORE_GENERATION, "Generate Copper", true).getBoolean();
        copperMin       = config.get(Constants.CONFIG_ORE_GENERATION, "Copper Min Level", 40).getInt();
        copperMax       = config.get(Constants.CONFIG_ORE_GENERATION, "Copper Max Level", 70).getInt();
        copperSize      = config.get(Constants.CONFIG_ORE_GENERATION, "Copper Vein Size", 12).getInt();
        copperPerChunk  = config.get(Constants.CONFIG_ORE_GENERATION, "Copper Veins per Chunk", 6).getInt();

        config.save();
    }
}
