package com.teambrmodding.neotech.common.tileentity;

import com.teambr.nucleus.common.tiles.Syncable;
import com.teambrmodding.neotech.machine.MachineOS;
import com.teambrmodding.neotech.machine.program.AbstractProgram;
import com.teambrmodding.neotech.machine.program.FurnaceProgram;
import com.teambrmodding.neotech.managers.TileEntityManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntityType;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 9/7/2019
 */
public class MachineTile extends Syncable {

    // NBT references, to prevent typo issues
    private static final String PROGRAM_NAME_NBT = "program_name";
    private static final String PROGRAM_NBT      = "program";

    // The instance of this OS
    public MachineOS operatingSystem;

    /**
     * Creates the machine tile
     */
    public MachineTile() {
        super(TileEntityManager.machine);
        operatingSystem = new MachineOS();
        operatingSystem.installProgram(new FurnaceProgram());
    }

    /*******************************************************************************************************************
     * TileEntity                                                                                                      *
     *******************************************************************************************************************/

    /**
     * Called only on the server side tick. Override for server side operations
     */
    @Override
    protected void onServerTick() {
        operatingSystem.onCpuCycle();
    }

    /**
     * Read the data from the compound
     * @param compound Saved data
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);

        // Find the list of programs that are installed
        ListNBT programsList = compound.getList(PROGRAM_NBT, 9);

        // Cycle programs
        if(!programsList.isEmpty()) {
            for(INBT nbt : programsList) {
                // Usable data for program
                if(nbt instanceof CompoundNBT) {
                    CompoundNBT programNBT = (CompoundNBT) nbt;
                    if(MachineOS.PROGRAM_REGISTRY.containsKey(programNBT.getString(PROGRAM_NAME_NBT))) {
                        // Is already installed? Called when already made but needs to sync
                        if(operatingSystem.isProgramInstalled(programNBT.getString(PROGRAM_NAME_NBT)))
                            operatingSystem.getInstalledProgram(programNBT.getString(PROGRAM_NAME_NBT)).read(programNBT);
                        else
                            // Try to create a new program and load the data from this tag, we are a new machine loading
                            try {
                                operatingSystem
                                        .installProgram(MachineOS.PROGRAM_REGISTRY.get(programNBT.getString(PROGRAM_NAME_NBT))
                                                .newInstance().createProgramFromData(programNBT));
                            } catch (InstantiationException | IllegalAccessException e) {
                                e.printStackTrace(); // Unable to create
                            }
                    }
                }
            }
        }
    }

    /**
     * Write the current state to the compound
     * @param compound NBT
     * @return The finished data
     */
    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);

        // Write the list
        ListNBT programList = new ListNBT();
        int listId = 0;

        // Write programs to compound
        for(AbstractProgram program : operatingSystem.getInstalledPrograms()) {
            CompoundNBT programCompound = new CompoundNBT();
            programCompound.putString(PROGRAM_NAME_NBT, program.getIdentifier());
            program.write(programCompound);
            programList.add(listId, programCompound);
            listId += 1;
        }

        compound.put(PROGRAM_NBT, programList);
        return compound;
    }

    /*******************************************************************************************************************
     * Syncable                                                                                                        *
     *******************************************************************************************************************/

    /**
     * Used to set the value of a field
     *
     * @param id    The field id
     * @param value The value of the field
     */
    @Override
    public void setVariable(int id, double value) {
        operatingSystem.setVariable(id, value);
    }

    /**
     * Used to get the field on the server, this will fetch the server value and overwrite the current
     *
     * @param id The field id
     * @return The value on the server, now set to ourselves
     */
    @Override
    public Double getVariable(int id) {
        return operatingSystem.getVariable(id);
    }
}
