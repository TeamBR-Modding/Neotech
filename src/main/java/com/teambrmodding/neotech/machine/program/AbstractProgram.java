package com.teambrmodding.neotech.machine.program;

import net.minecraft.nbt.CompoundNBT;

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
public abstract class AbstractProgram {

    // The string name for this kind of program
    private String identifier;

    /**
     * Creates the program
     * @param id The ID of this, should be unique to this type, example "furnace", "storage", etc
     */
    protected AbstractProgram(String id) {
        this.identifier = id;
    }

    /*******************************************************************************************************************
     * Abstract Methods                                                                                                *
     *******************************************************************************************************************/

    /**
     * Called by the operating system, this is not guaranteed to be on each tick, but should be used to increment all
     * processes being run at this time
     */
    public abstract void cpuCycle();

    /**
     * Write the current data to be saved/synced
     * @param nbt The NBT, this will be just your data, does not have access to parent NBT, write data directly
     *            into this
     */
    public abstract void write(CompoundNBT nbt);

    /**
     * Read the data from the NBT
     * @param nbt The tag for this instance, only access data written in this#write(nbt)
     */
    public abstract void read (CompoundNBT nbt);

    /**
     * Set the variable for the given id, this is
     * @param id The id of this variable
     * @param value The value to set
     */
    public abstract void setVariable(int id, double value);

    /**
     * Get a variable from this program
     * @param id The id for this variable
     * @return The variable
     */
    public abstract double getVariable(int id);

    /**
     * Used to set your programs syncable ids, these should be class level final values generated from the OS class
     * method. You should map all relevant ids to your identifier
     * @param programSyncMap The operating systems map for syncing, put your ids here and point to us
     */
    public abstract void mapVariables(Map<Integer, String> programSyncMap);

    /*******************************************************************************************************************
     * Program                                                                                                         *
     *******************************************************************************************************************/

    /**
     * Used to create and read data from compound
     * @param nbt The data to load
     * @return This, with data loaded
     */
    public AbstractProgram createProgramFromData(CompoundNBT nbt) {
        read(nbt);
        return this;
    }

    /**
     * Used to get the name for this tag on the compound, this should be unique to this type, since there are not
     * going to be multiple types in the same instance this can be something like "smelting" or "crusher"
     * @return The id for this program
     */
    public String getIdentifier() {
        return identifier;
    }
}
