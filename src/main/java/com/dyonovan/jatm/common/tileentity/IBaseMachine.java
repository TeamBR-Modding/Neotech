package com.dyonovan.jatm.common.tileentity;

import cofh.api.energy.IEnergyHandler;
import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public interface IBaseMachine extends IEnergyHandler, ISidedInventory {

    /**
     * Adds energy to the storage. Returns quantity of energy that was accepted.
     *
     * @param maxReceive
     *            Maximum amount of energy to be inserted.
     * @param simulate
     *            If TRUE, the insertion will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
     */
    @Override
    int receiveEnergy(EnumFaceDirection from, int maxReceive, boolean simulate);

    @Override
    int extractEnergy(EnumFaceDirection from, int maxExtract, boolean simulate);

    @Override
    int[] getSlotsForFace(EnumFacing side);

    @Override
    boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction);

    @Override
    boolean canExtractItem(int index, ItemStack stack, EnumFacing direction);

    @Override
    boolean isItemValidForSlot(int index, ItemStack stack);

    @Override
    int getField(int id);

    @Override
    void setField(int id, int value);

    @Override
    int getFieldCount();

    @Override
    void clear();

}
