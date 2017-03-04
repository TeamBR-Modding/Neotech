package com.teambrmodding.neotech.common.tiles;

import net.minecraft.item.ItemStack;
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
 * The base for all processors.
 * I - Input
 * O - Output
 *
 * @author Paul Davis - pauljoda
 * @since 2/16/2017
 */
public abstract class MachineProcessor<I, O> extends AbstractMachine {
    public static final int COOK_TIME = 30;

    protected int cookTime = 0;
    protected boolean didWork = false;

    /*******************************************************************************************************************
     * Abstract Methods                                                                                                *
     *******************************************************************************************************************/

    /**
     * Get the output of the recipe
     *
     * @param input The input
     * @return The output
     */
    public abstract O getOutput(I input);

    /**
     * Get the output of the recipe (used in insert options)
     *
     * @param input The input
     * @return The output
     */
    public abstract ItemStack getOutputForStack(ItemStack input);

    /**
     * Used to actually cook the item
     */
    protected abstract void cook();

    /**
     * Called when the tile has completed the cook process
     */
    protected abstract void completeCook();

    /**
     * Used to tell if this tile is able to process
     *
     * @return True if you are able to process
     */
    public abstract boolean canProcess();

    /**
     * Used to get how long it takes to cook things, you should check for upgrades at this point
     *
     * @return The time it takes in ticks to cook the current item
     */
    public abstract int getCookTime();

    /**
     * Used to get how much energy to drain per tick, you should check for upgrades at this point
     *
     * @return How much energy to drain per tick
     */
    public abstract int getEnergyCostPerTick();

    /*******************************************************************************************************************
     * Processor Methods                                                                                               *
     *******************************************************************************************************************/

    /**
     * Used to actually do the processes needed. For processors this should be cooking items and generators should
     * generate RF. This is called every tick allowed, provided redstone mode requirements are met
     */
    protected int failCoolDown = 0;
    @Override
    protected void doWork() {
        failCoolDown -= 1;
        didWork = false;

        // Do operations
        if(failCoolDown < 0 && canProcess()) {
            /** We want to check if we are above the value needed before we actually start checking for cooking, this will ensure
             *  we don't run into issues with going one tick over */
            if(cookTime >= getCookTime()) {
                completeCook();
                reset();
                markForUpdate(6);
            }

            // For the moments where we complete and result
            if(canProcess()) {
                cook();
                energyStorage.providePower(getEnergyCostPerTick(), true);
            }
            didWork = true;
        } else {
            boolean update = cookTime > 0;
            if(update) {
                cookTime -= 1;
                sendValueToClient(COOK_TIME, cookTime);
            }
        }

        if(didWork)
            sendValueToClient(COOK_TIME, cookTime);
    }

    /**
     * Use this to set all variables back to the default values, usually means the operation failed
     */
    @Override
    public void reset() {
        cookTime = 0;
    }

    /**
     * Used to check if this tile is active or not
     *
     * @return True if active state
     */
    @Override
    public boolean isActive() {
        return cookTime > 0 && super.isActive();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("CookTime", cookTime);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        cookTime = compound.getInteger("CookTime");
    }

    /**
     * Client side method to get how far along this process is to a scale variable
     *
     * @param scaleValue What scale to move to, usually pixels
     * @return What value on new scale this is complete
     */
    @SideOnly(Side.CLIENT)
    public int getCookProgressScaled(int scaleValue) {
        return (int) Math.min(((cookTime * scaleValue) / Math.max(getCookTime(), 0.001)), scaleValue);
    }

    /*******************************************************************************************************************
     * Energy Methods                                                                                                  *
     *******************************************************************************************************************/

    /**
     * Is this tile an energy provider
     *
     * @return True to allow energy out
     */
    @Override
    protected boolean isProvider() {
        return false;
    }

    /**
     * Is this tile an energy receiver
     *
     * @return True to accept energy
     */
    @Override
    protected boolean isReceiver() {
        return true;
    }

    /**
     * Used to define the default size of this energy bank
     *
     * @return The default size of the energy bank
     */
    @Override
    protected int getDefaultEnergyStorageSize() {
        return 10000;
    }

    /*******************************************************************************************************************
     * Inventory Methods                                                                                               *
     *******************************************************************************************************************/

    /**
     * Used to define if an item is valid for a slot
     *
     * @param index The slot id
     * @param stack The stack to check
     * @return True if you can put this there
     */
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0 && canInsertItem(index, stack, null);
    }

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

    /**
     * Can insert the item into the inventory
     *
     * @param slot        The slot
     * @param itemStackIn The stack to insert
     * @param dir         The dir
     * @return True if can insert
     */
    @Override
    public boolean canInsertItem(int slot, ItemStack itemStackIn, EnumFacing dir) {
        return !isDisabled(dir) && slot == 0 && getOutputForStack(itemStackIn) != null;
    }

    /**
     * Can this extract the item
     *
     * @param slot  The slot
     * @param stack The stack
     * @param dir   The dir
     * @return True if can extract
     */
    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing dir) {
        return slot == 1 && !isDisabled(dir);
    }

    /*******************************************************************************************************************
     * Syncable                                                                                                        *
     *******************************************************************************************************************/

    /**
     * Used to set the variable for this tile, the Syncable will use this when you send a value to the server
     *
     * @param id    The ID of the variable to send
     * @param value The new value to set to (you can use this however you want, eg using the ordinal of EnumFacing)
     */
    @Override
    public void setVariable(int id, double value) {
        if(id == COOK_TIME) {
            this.cookTime = (int) value;
            return;
        }
        super.setVariable(id, value);
    }
}
