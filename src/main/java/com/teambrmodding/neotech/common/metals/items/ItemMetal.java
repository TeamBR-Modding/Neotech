package com.teambrmodding.neotech.common.metals.items;

import com.teambrmodding.neotech.Neotech;
import com.teambrmodding.neotech.lib.Reference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/16/2017
 */
public class ItemMetal extends Item {
    private String name;
    private int color;

    /**
     * Creates a metal item
     * @param name  The item name
     * @param color The item color
     */
    public ItemMetal(String name, int color, int maxStackSize) {
        this.name = name;
        this.color = color;
        setRegistryName(new ResourceLocation(Reference.MOD_ID, name));
        setUnlocalizedName(getRegistryName().toString());
        setCreativeTab(Neotech.tabMetals);
        setMaxStackSize(maxStackSize);
    }

    /**
     * The name of this item
     */
    public String getName() {
        return name;
    }

    /**
     * The color
     */
    public int getColorFromItemStack(ItemStack stack) {
        return color;
    }
}
