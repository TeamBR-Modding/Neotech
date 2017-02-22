package com.teambrmodding.neotech.managers;

import com.ibm.icu.lang.UProperty;
import com.teambrmodding.neotech.common.items.ItemWrench;
import com.teambrmodding.neotech.common.items.UpgradeItem;
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

import static com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem.ENUM_UPGRADE_CATEGORY.*;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/12/2017
 */
public class ItemManager {

    // Upgrade System
    public static UpgradeItem processorSingleCore = new UpgradeItem(IUpgradeItem.CPU_SINGLE_CORE, CPU, 1, 2, false);
    public static UpgradeItem processorDualCore   = new UpgradeItem(IUpgradeItem.CPU_DUAL_CORE, CPU, 1, 4, false);
    public static UpgradeItem processorQuadCore   = new UpgradeItem(IUpgradeItem.CPU_QUAD_CORE, CPU, 1, 8, false);
    public static UpgradeItem processorOctCore    = new UpgradeItem(IUpgradeItem.CPU_OCT_CORE, CPU, 1, 16, false);

    public static UpgradeItem memoryDDR1 = new UpgradeItem(IUpgradeItem.MEMORY_DDR1, MEMORY, 2, 2, true);
    public static UpgradeItem memoryDDR2 = new UpgradeItem(IUpgradeItem.MEMORY_DDR2, MEMORY, 2, 4, true);
    public static UpgradeItem memoryDDR3 = new UpgradeItem(IUpgradeItem.MEMORY_DDR3, MEMORY, 2, 6, true);
    public static UpgradeItem memoryDDR4 = new UpgradeItem(IUpgradeItem.MEMORY_DDR4, MEMORY, 2, 8, true);

    public static UpgradeItem hardDrive64G  = new UpgradeItem(IUpgradeItem.HDD_64G, HDD, 1, 2, false);
    public static UpgradeItem hardDrive256G = new UpgradeItem(IUpgradeItem.HDD_256G, HDD, 1, 4, false);
    public static UpgradeItem hardDrive512G = new UpgradeItem(IUpgradeItem.HDD_512G, HDD, 1, 8, false);
    public static UpgradeItem hardDrive1T   = new UpgradeItem(IUpgradeItem.HDD_1T, HDD, 1, 16, false);

    public static UpgradeItem psu250W = new UpgradeItem(IUpgradeItem.PSU_250W, PSU, 1, 2, false);
    public static UpgradeItem psu500W = new UpgradeItem(IUpgradeItem.PSU_500W, PSU, 1, 4, false);
    public static UpgradeItem psu750W = new UpgradeItem(IUpgradeItem.PSU_750W, PSU, 1, 8, false);
    public static UpgradeItem psu960W = new UpgradeItem(IUpgradeItem.PSU_960W, PSU, 1, 16, false);

    public static UpgradeItem transformer = new UpgradeItem(IUpgradeItem.TRANSFORMER, MISC, 4, 1, true);

    public static UpgradeItem expansion = new UpgradeItem(IUpgradeItem.EXPANSION_CARD, MISC, 1, 1, false);
    public static UpgradeItem redstoneControl = new UpgradeItem(IUpgradeItem.REDSTONE_CIRCUIT, MISC, 1, 1, false);
    public static UpgradeItem networkCard = new UpgradeItem(IUpgradeItem.NETWORK_CARD, MISC, 1, 1, false);

    // Utils
    public static ItemWrench wrench = new ItemWrench();

    public static void preInit() {
        registerItem(processorSingleCore);
        registerItem(processorDualCore);
        registerItem(processorQuadCore);
        registerItem(processorOctCore);

        registerItem(memoryDDR1);
        registerItem(memoryDDR2);
        registerItem(memoryDDR3);
        registerItem(memoryDDR4);

        registerItem(hardDrive64G);
        registerItem(hardDrive256G);
        registerItem(hardDrive512G);
        registerItem(hardDrive1T);

        registerItem(psu250W);
        registerItem(psu500W);
        registerItem(psu750W);
        registerItem(psu960W);

        // Check for IC2
        if(Loader.isModLoaded("IC2"))
            registerItem(transformer);

        registerItem(expansion);
        registerItem(redstoneControl);
        registerItem(networkCard);

        registerItem(wrench);
    }

    /**
     * Helper method to register an item to the game
     * @param item The item
     * @param oreDict The ore dict tag
     * @return The registered item
     */
    public static <T extends Item> T registerItem(T item, @Nullable String oreDict) {
        GameRegistry.register(item);
        if(oreDict != null)
            OreDictionary.registerOre(oreDict, item);
        return item;
    }

    /**
     * Short hand helper to register item with no ore dict
     */
    public static <T extends Item> T registerItem(T item) {
        return registerItem(item, null);
    }
}
