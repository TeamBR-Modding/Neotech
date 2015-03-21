package com.dyonovan.jatm.common.tileentity.notmachines;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import com.dyonovan.jatm.common.tileentity.BaseMachine;
import com.dyonovan.jatm.common.tileentity.InventoryTile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.EnumFacing;

public class TileRFStorage extends BaseMachine implements IUpdatePlayerListBox, IEnergyReceiver, IEnergyProvider {

    private static final int RF_TOTAL_1 = 250000;
    private static final int RF_TICK_1 = 200;
    public static final int CHARGE_SLOT_1 = 0;

    private int tier;
    public EnergyStorage energyRF;

    public TileRFStorage(int tier) {
        if (tier == 1) energyRF = new EnergyStorage(RF_TOTAL_1, RF_TICK_1);
        inventory = new InventoryTile(tier);
        this.tier = tier;
    }

    @Override
    public void update() {

    }

    /*******************************************************************************************************************
     ************************************** Energy Functions ***********************************************************
     *******************************************************************************************************************/

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return energyRF.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return energyRF.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return energyRF.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return energyRF.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    /*******************************************************************************************************************
     **************************************** Inventory Functions ******************************************************
     *******************************************************************************************************************/

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        switch (tier) {
            case 1:
                return new int [] {0};
            default:
                return new int[] {0};
        }
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return true;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true; //TODO
    }

    @Override
    public int getField(int id) {
        switch (id) {
            default:
                return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {

        }
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            inventory.setStackInSlot(null, i);
        }
    }

    /*******************************************************************************************************************
     **************************************** Tile Functions ***********************************************************
     *******************************************************************************************************************/

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energyRF.readFromNBT(tag);
        inventory.readFromNBT(tag, this);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        energyRF.writeToNBT(tag);
        inventory.writeToNBT(tag);
    }


}
