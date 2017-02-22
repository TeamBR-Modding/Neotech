package com.teambrmodding.neotech.common.tiles;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/16/2017
 */
public abstract class MachineGenerator extends AbstractMachine {
    protected int burnTime, currentObjectBurnTime = 0;
    protected boolean didWork = false;

    /*******************************************************************************************************************
     * Abstract Methods                                                                                                *
     *******************************************************************************************************************/

    /**
     * Called to tick generation. This is where you add power to the generator
     */
    public abstract void generate();

    /**
     * Called per tick to manage burn time. You can do nothing here if there is nothing to generate. You should decrease burn time here
     * You should be handling checks if burnTime is 0 in this method, otherwise the tile won't know what to do
     *
     * @return True if able to continue generating
     */
    public abstract boolean manageBurnTime();

    /**
     * This method handles how much energy to produce per tick
     *
     * @return How much energy to produce per tick
     */
    public abstract int getEnergyProduced();

    /*******************************************************************************************************************
     * Generator Methods                                                                                               *
     *******************************************************************************************************************/

    /**
     * Used to actually do the processes needed. For processors this should be cooking items and generators should
     * generate RF. This is called every tick allowed, provided redstone mode requirements are met
     */
    @Override
    protected void doWork() {
        didWork = burnTime == 1;

        // Transfer
        if(energyStorage.getEnergyStored() > 0) {
            for(EnumFacing dir : EnumFacing.values()) {
                if(worldObj.getTileEntity(pos.offset(dir)) instanceof IEnergyReceiver) {
                    IEnergyReceiver energyReceiver = (IEnergyReceiver) worldObj.getTileEntity(pos.offset(dir));
                    int energyDemand = energyReceiver.receiveEnergy(dir.getOpposite(), energyStorage.getEnergyStored(), true);
                    if(energyDemand > 0) {
                        int consumedEnergy = extractEnergy(energyDemand, false);
                        energyReceiver.receiveEnergy(dir.getOpposite(), consumedEnergy, false);
                        didWork = true;
                    }
                }
            }
        }

        // Generate
        if(manageBurnTime()) {
            generate();
            didWork = true;
        } else
            reset();

        if(didWork)
            markForUpdate(6);
    }

    /**
     * Use this to set all variables back to the default values, usually means the operation failed
     */
    @Override
    public void reset() {
        burnTime = 0;
        currentObjectBurnTime = 0;
    }

    /**
     * Used to check if this tile is active or not
     *
     * @return True if active state
     */
    @Override
    public boolean isActive() {
        return burnTime > 0;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("BurnTime", burnTime);
        compound.setInteger("CurrentObjectBurnTime", currentObjectBurnTime);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        burnTime              = compound.getInteger("BurnTime");
        currentObjectBurnTime = compound.getInteger("CurrentObjectBurnTime");
    }

    /**
     * Client side method to get how far along this process is to a scale variable
     *
     * @param scaleVal What scale to move to, usually pixels
     * @return What value on new scale this is complete
     */
    @SideOnly(Side.CLIENT)
    public int getBurnProgressScaled(int scaleVal) {
        return (int) ((burnTime * scaleVal) / Math.max(currentObjectBurnTime, 0.001));
    }

    /*******************************************************************************************************************
     * Energy Methods                                                                                                  *
     *******************************************************************************************************************/

    /**
     * Used to define the default size of this energy bank
     *
     * @return The default size of the energy bank
     */
    @Override
    public int getDefaultEnergyStorageSize() {
        return 32000;
    }

    /**
     * Is this tile an energy provider
     *
     * @return True to allow energy out
     */
    @Override
    protected boolean isProvider() {
        return true;
    }

    /**
     * Is this tile an energy reciever
     *
     * @return True to accept energy
     */
    @Override
    protected boolean isReceiver() {
        return false;
    }

    /*******************************************************************************************************************
     * Inventory Methods                                                                                               *
     *******************************************************************************************************************/

    /**
     * Get the slots for the given face
     *
     * @param face The face
     * @return What slots can be accessed
     */
    @Override
    public int[] getSlotsForFace(EnumFacing face) {
        if(isDisabled(face))
            return new int[] {};
        switch (face) {
            case UP:
                return getInputSlots(getModeForSide(face));
            case DOWN:
                return getOutputSlots(getModeForSide(face));
            default:
                int[] inputSlots  = getInputSlots(getModeForSide(face));
                int[] outputSlots = getOutputSlots(getModeForSide(face));
                int[] combinedInOut = new int[inputSlots.length + outputSlots.length];
                System.arraycopy(inputSlots,  0, combinedInOut, 0, inputSlots.length);
                System.arraycopy(outputSlots, 0, combinedInOut, inputSlots.length, outputSlots.length);
                return combinedInOut;
        }
    }
}
