package com.teambrmodding.neotech.common.items;

import com.teambrmodding.neotech.Neotech;
import com.teambrmodding.neotech.lib.Reference;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

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
public class BaseItem extends Item {

    /**
     * Base item, handles stack size and tabs
     */
    public BaseItem(String name, int maxStackSize) {
        setCreativeTab(Neotech.tabNeotech);
        setMaxStackSize(maxStackSize);
        setRegistryName(new ResourceLocation(Reference.MOD_ID, name));
        setUnlocalizedName(getRegistryName().toString());
    }
}
