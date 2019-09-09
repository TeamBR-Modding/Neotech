package com.teambrmodding.neotech.machine.data;

import net.minecraft.nbt.CompoundNBT;

import java.util.HashMap;
import java.util.Map;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 9/8/2019
 */
public abstract class Entry {

    public static final Map<Integer, Class<? extends Entry>> typeToClassMap;

    // Set the map
    static {
        typeToClassMap = new HashMap<>();
        typeToClassMap.put(0, ItemStackEntry.class);
    }

    // A reference to the parent folder
    private Folder parent;

    /**
     * Creates an entry in a directory
     * @param parent The parent folder
     */
    public Entry(Folder parent) {
        this.parent = parent;
    }

    /*******************************************************************************************************************
     * Entry                                                                                                           *
     *******************************************************************************************************************/

    /**
     * Write the data for this entry to the compound
     * @param nbt The nbt, clean tag for this entry only
     */
    public abstract void write(CompoundNBT nbt);

    /**
     * Read the data from the nbt
     * @param nbt The nbt for just this entry
     */
    public abstract Entry read(CompoundNBT nbt);

    /**
     * Delete yourself from the parent directory
     */
    public void delete() {
        if(parent != null)
            parent.delete(this);
    }
}
