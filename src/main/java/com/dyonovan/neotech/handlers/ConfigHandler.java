package com.dyonovan.neotech.handlers;

import com.dyonovan.neotech.lib.Constants;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

    public static boolean genCopper, genTin;
    public static int copperMax, copperMin, copperSize, copperPerChunk;
    public static int tinMax, tinMin, tinSize, tinPerChunk;

    public static void init(Configuration config) {

        config.load();

        genCopper       = config.get(Constants.CONFIG_ORE_GENERATION, "Generate Copper", true).getBoolean();
        copperMin       = config.get(Constants.CONFIG_COPPER_GENERATION, "Copper Min Level", 40).getInt();
        copperMax       = config.get(Constants.CONFIG_COPPER_GENERATION, "Copper Max Level", 70).getInt();
        copperSize      = config.get(Constants.CONFIG_COPPER_GENERATION, "Copper Vein Size", 12).getInt();
        copperPerChunk  = config.get(Constants.CONFIG_COPPER_GENERATION, "Copper Veins per Chunk", 6).getInt();

        genTin       = config.get(Constants.CONFIG_ORE_GENERATION, "Generate Tin", true).getBoolean();
        tinMin       = config.get(Constants.CONFIG_TIN_GENERATION, "Tin Min Level", 20).getInt();
        tinMax       = config.get(Constants.CONFIG_TIN_GENERATION, "Tin Max Level", 50).getInt();
        tinSize      = config.get(Constants.CONFIG_TIN_GENERATION, "Tin Vein Size", 12).getInt();
        tinPerChunk  = config.get(Constants.CONFIG_TIN_GENERATION, "Tin Veins per Chunk", 6).getInt();

        config.save();
    }
}
