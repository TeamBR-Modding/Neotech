package com.teambrmodding.neotech.machine;

import com.mojang.datafixers.util.Pair;
import com.teambr.nucleus.helper.LogHelper;
import com.teambrmodding.neotech.common.tileentity.MachineTile;
import com.teambrmodding.neotech.machine.program.AbstractProgram;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
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
public class MachineOS {

    /*******************************************************************************************************************
     * Global Info                                                                                                     *
     *******************************************************************************************************************/

    // Holder of next usable syncable id
    private static int NEXT_FREE_ID = 0;

    // Main registry of all programs, you should be registering this on load for any new programs
    public static Map<String, Class<? extends AbstractProgram>> PROGRAM_REGISTRY = new HashMap<>();

    /**
     * Get a new id for your program, should only be called from static class creation to reference their id
     * @return The next free id, increments the local counter
     */
    public static int getNextFreeSyncID() {
        int toSend = NEXT_FREE_ID;
        NEXT_FREE_ID += 1;
        return toSend;
    }

    /*******************************************************************************************************************
     * Variables                                                                                                       *
     *******************************************************************************************************************/

    // List of currently installed programs, when installed, the id will become the key for that program
    private Map<String, AbstractProgram> installedPrograms = new HashMap<>();

    /**
     * This is the map of syncing data, the integer is passed from the parent tile, finds the name of the program that
     * uses this
     */
    private Map<Integer, String> programSyncMap = new HashMap<>();

    /**
     * A map of slots to program specific slots
     *
     * The left side is for the overall slot handled from tile
     * The right side is a pair or the program that uses this slot and the slot specific to that program
     */
    private Map<Integer, Pair<String, Integer>> inventoryMap = new HashMap<>();

    // The reference to the machine itself
    public MachineTile hardware;

    /**
     * Creates a operating system and attaches the the passed machine
     * @param machine The machine to attach
     */
    public MachineOS(MachineTile machine) {
        this.hardware = machine;
    }

    /*******************************************************************************************************************
     * Operating System                                                                                                *
     *******************************************************************************************************************/

    /**
     * Called on each cpu cycle, determined by the tile
     */
    public void onCpuCycle() {
        for(AbstractProgram program : installedPrograms.values()) {
            program.cpuCycle();
        }
    }

    /**
     * Install a program to this operating system
     * @param program The program to install
     */
    public void installProgram(AbstractProgram program) {
        // Check if already installed
        if(!isProgramInstalled(program.getIdentifier())) {
            // Map the syncable variables
            program.mapVariables(programSyncMap);
            // Install the program
            installedPrograms.put(program.getIdentifier(), program);
        } else // Can't install, already installed, should be called, tile should be checking
            LogHelper.logger.info("Unable to install program, already installed");
    }

    /**
     * Checks if the given program is installed already
     * @param program The program
     * @return True if installed
     */
    public boolean isProgramInstalled(String program) {
        return installedPrograms.containsKey(program);
    }

    /**
     * Gets the installed program, null if not found
     * @param program The program id
     * @return The program, null if not found. You should always check before accessing
     */
    @Nullable
    public AbstractProgram getInstalledProgram(String program) {
        if(isProgramInstalled(program))
            return installedPrograms.get(program);
        return null;
    }

    /**
     * Get a list of all installed programs
     * @return A collection of all programs
     */
    public Collection<AbstractProgram> getInstalledPrograms() {
        return installedPrograms.values();
    }

    /*******************************************************************************************************************
     * Item Distribution                                                                                               *
     *******************************************************************************************************************/

    /**
     * Distribute items from the input directory
     */
    public void distributeToPrograms() {

    }

    /*******************************************************************************************************************
     * Sync                                                                                                            *
     *******************************************************************************************************************/

    /**
     * Takes the tile's id, and passes it to translated program id
     * @param id Main id to lookup
     * @param value Value to pass
     */
    public void setVariable(int id, double value) {
        if(programSyncMap.containsKey(id)
                && installedPrograms.containsKey(programSyncMap.get(id))) { // Installed program knows how to handle data
            // Find the installed program, send the translated id and value to it for processing
            installedPrograms.get(programSyncMap.get(id)).setVariable(id, value);
        }
    }

    /**
     * Get a variable by id
     * @param id Id, will be translated to child
     * @return The variable from program
     */
    public double getVariable(int id) {
        if(programSyncMap.containsKey(id)
                && installedPrograms.containsKey(programSyncMap.get(id))) { // Installed program knows how to handle data
            // Find the installed program, send the translated id and value to it for processing
            return installedPrograms.get(programSyncMap.get(id)).getVariable(id);
        }
        return 0.0; // No program found
    }
}
