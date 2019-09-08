package com.teambrmodding.neotech.machine.program;

import com.teambr.nucleus.helper.LogHelper;
import com.teambrmodding.neotech.machine.MachineOS;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

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
public class FurnaceProgram extends AbstractProgram {

    // The name for this program
    private static final String ID = "furnace";

    // How long this operation takes to complete, in cpu cycles
    public static final int PROCESS_TIME = 200;

    // Sync Variables
    private static final int PROGRESS_SYNC_VARIABLE = MachineOS.getNextFreeSyncID();

    // Current progress towards finishing task
    private int currentProgress = 0;

    // List of Inventory contents
    private NonNullList<ItemStack> inventoryContents = NonNullList.withSize(2, ItemStack.EMPTY);

    /**
     * Creates the program
     */
    public FurnaceProgram( ) {
        super(ID);
    }

    /**
     * Called by the operating system, this is not guaranteed to be on each tick, but should be used to increment all
     * processes being run at this time
     */
    @Override
    public void cpuCycle() {

    }

    /**
     * Write the current data to be saved/synced
     *
     * @param nbt The NBT, this will be just your data, does not have access to parent NBT, write data directly
     *            into this
     */
    @Override
    public void write(CompoundNBT nbt) {
        ItemStackHelper.saveAllItems(nbt, inventoryContents);
    }

    /**
     * Read the data from the NBT
     *
     * @param nbt The tag for this instance, only access data written in this#write(nbt)
     */
    @Override
    public void read(CompoundNBT nbt) {
        if (nbt != null)
            ItemStackHelper.loadAllItems(nbt, inventoryContents);
    }

    /**
     * Set the variable for the given id, this is
     *
     * @param id    The id of this variable
     * @param value The value to set
     */
    @Override
    public void setVariable(int id, double value) {
        if(id == PROGRESS_SYNC_VARIABLE)
            currentProgress = (int) value;
    }

    /**
     * Get a variable from this program
     *
     * @param id The id for this variable
     * @return The variable
     */
    @Override
    public double getVariable(int id) {
        if(id == PROGRESS_SYNC_VARIABLE)
            return currentProgress;
        return 0;
    }

    /**
     * Used to set your programs syncable ids, these should be class level final values generated from the OS class
     * method. You should map all relevant ids to your identifier
     *
     * @param programSyncMap The operating systems map for syncing, put your ids here and point to us
     */
    @Override
    public void mapVariables(Map<Integer, String> programSyncMap) {
        programSyncMap.put(PROGRESS_SYNC_VARIABLE, getIdentifier());
    }
}
