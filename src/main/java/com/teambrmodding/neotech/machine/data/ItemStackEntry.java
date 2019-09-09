package com.teambrmodding.neotech.machine.data;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

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
public class ItemStackEntry extends Entry {

    // The Stack itself
    private ItemStack stack = ItemStack.EMPTY;

    /**
     * Creates an entry in a directory
     *
     * @param parent The parent folder
     */
    public ItemStackEntry(Folder parent) {
        super(parent);
    }

    /**
     * Write the data for this entry to the compound
     *
     * @param nbt The nbt, clean tag for this entry only
     */
    @Override
    public void write(CompoundNBT nbt) {
        nbt.putInt("entry_type", 0);
        stack.write(nbt);
    }

    /**
     * Read the data from the nbt
     *
     * @param nbt The nbt for just this entry
     */
    @Override
    public Entry read(CompoundNBT nbt) {
        stack = ItemStack.read(nbt);
        return this;
    }

    /*******************************************************************************************************************
     * ItemStackEntry                                                                                                  *
     *******************************************************************************************************************/

    /**
     * Get the stack for this entry
     * @return The current stack
     */
    public ItemStack getStack() {
        return stack;
    }

    /**
     * Set the stack in this entry
     * @param stack The stack
     */
    public void setStack(ItemStack stack) {
        this.stack = stack;
    }
}
