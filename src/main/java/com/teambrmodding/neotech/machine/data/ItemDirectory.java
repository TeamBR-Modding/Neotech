package com.teambrmodding.neotech.machine.data;

import net.minecraft.item.ItemStack;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * Creates a directory structure for given object. Think of it like a file directory
 *
 * @param <V> The type of object this organizes
 *
 * @author Paul Davis - pauljoda
 * @since 9/8/2019
 */
public class ItemDirectory<V extends ItemStack> {

    // The root folder for this directory
    public Folder root = new Folder(null, "");

    /**
     * Creates an item directory
     */
    public ItemDirectory() {
        root.addChild(new Folder(root, "Input"));
        root.addChild(new Folder(root, "Output"));
    }
}
