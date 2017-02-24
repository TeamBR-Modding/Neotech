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
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/12/2017
 */
public class ItemManager {

    // Upgrade System
    public static UpgradeItem processorSingleCore;
    public static UpgradeItem processorDualCore;
    public static UpgradeItem processorQuadCore;
    public static UpgradeItem processorOctCore;

    public static UpgradeItem memoryDDR1;
    public static UpgradeItem memoryDDR2;
    public static UpgradeItem memoryDDR3;
    public static UpgradeItem memoryDDR4;

    public static UpgradeItem hardDrive64G;
    public static UpgradeItem hardDrive256G;
    public static UpgradeItem hardDrive512G;
    public static UpgradeItem hardDrive1T;

    public static UpgradeItem psu250W;
    public static UpgradeItem psu500W;
    public static UpgradeItem psu750W;
    public static UpgradeItem psu960W;

    public static UpgradeItem transformer;

    public static UpgradeItem expansion;
    public static UpgradeItem redstoneControl;
    public static UpgradeItem networkCard;

    // Utils
    public static ItemWrench wrench;

    public static void preInit() {
        processorSingleCore = registerItem(new UpgradeItem(IUpgradeItem.CPU_SINGLE_CORE, CPU, 1, 4, false));
        processorDualCore = registerItem(new UpgradeItem(IUpgradeItem.CPU_DUAL_CORE, CPU, 1, 8, false));
        processorQuadCore = registerItem(new UpgradeItem(IUpgradeItem.CPU_QUAD_CORE, CPU, 1, 12, false));
        processorOctCore = registerItem(new UpgradeItem(IUpgradeItem.CPU_OCT_CORE, CPU, 1, 16, false));

        memoryDDR1 = registerItem(new UpgradeItem(IUpgradeItem.MEMORY_DDR1, MEMORY, 2, 2, true));
        memoryDDR2 = registerItem(new UpgradeItem(IUpgradeItem.MEMORY_DDR2, MEMORY, 2, 4, true));
        memoryDDR3 = registerItem(new UpgradeItem(IUpgradeItem.MEMORY_DDR3, MEMORY, 2, 6, true));
        memoryDDR4 = registerItem(new UpgradeItem(IUpgradeItem.MEMORY_DDR4, MEMORY, 2, 8, true));

        hardDrive64G = registerItem(new UpgradeItem(IUpgradeItem.HDD_64G, HDD, 1, 2, false));
        hardDrive256G = registerItem(new UpgradeItem(IUpgradeItem.HDD_256G, HDD, 1, 4, false));
        hardDrive512G = registerItem(new UpgradeItem(IUpgradeItem.HDD_512G, HDD, 1, 8, false));
        hardDrive1T = registerItem(new UpgradeItem(IUpgradeItem.HDD_1T, HDD, 1, 16, false));

        psu250W = registerItem(new UpgradeItem(IUpgradeItem.PSU_250W, PSU, 1, 2, false));
        psu500W = registerItem(new UpgradeItem(IUpgradeItem.PSU_500W, PSU, 1, 4, false));
        psu750W = registerItem(new UpgradeItem(IUpgradeItem.PSU_750W, PSU, 1, 8, false));
        psu960W = registerItem(new UpgradeItem(IUpgradeItem.PSU_960W, PSU, 1, 16, false));

        // Check for IC2
        if(Loader.isModLoaded("IC2"))
            transformer = registerItem(new UpgradeItem(IUpgradeItem.TRANSFORMER, MISC, 4, 1, true));

        expansion = registerItem(new UpgradeItem(IUpgradeItem.EXPANSION_CARD, MISC, 1, 1, false));
        redstoneControl = registerItem(new UpgradeItem(IUpgradeItem.REDSTONE_CIRCUIT, MISC, 1, 1, false));
        networkCard = registerItem(new UpgradeItem(IUpgradeItem.NETWORK_CARD, MISC, 1, 1, false));

        wrench = registerItem(new ItemWrench());
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
