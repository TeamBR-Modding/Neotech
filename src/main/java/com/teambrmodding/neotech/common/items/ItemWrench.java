package com.teambrmodding.neotech.common.items;

import com.teambr.bookshelf.common.items.ToolWrench;
import com.teambrmodding.neotech.Neotech;
import com.teambrmodding.neotech.lib.Reference;
import net.minecraft.util.ResourceLocation;

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
public class ItemWrench extends ToolWrench {

    /**
     * Base item, handles stack size and tabs
     */
    public ItemWrench() {
        setCreativeTab(Neotech.tabNeotech);
        setMaxStackSize(1);
        setRegistryName(new ResourceLocation(Reference.MOD_ID, "wrench"));
        setUnlocalizedName(getRegistryName().toString());
    }

    /*******************************************************************************************************************
     * Item                                                                                                            *
     *******************************************************************************************************************/

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    @Override
    public boolean isFull3D() {
        return true;
    }
}
