package com.dyonovan.neotech.common.tileentity.machine;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import com.dyonovan.neotech.common.blocks.IExpellable;
import com.dyonovan.neotech.common.tileentity.BaseMachine;
import com.dyonovan.neotech.common.tileentity.InventoryTile;
import com.dyonovan.neotech.handlers.ItemHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.EnumFacing;

public class TileThermalBinder extends BaseMachine implements IEnergyReceiver, IUpdatePlayerListBox, IExpellable {

    public int currentProcessTime;
    public EnergyStorage energyRF;

    private static final int RF_TICK= 100;
    public static final int BASE_PROCESS_TIME = 200;
    public static final int INPUT_SLOT_1 = 0;
    public static final int INPUT_SLOT_2 = 1;
    public static final int INPUT_SLOT_3 = 2;
    public static final int INPUT_SLOT_4 = 3;
    public static final int MB_SLOT_INPUT = 4;
    public static final int MB_SLOT_OUTPUT = 5;

    public TileThermalBinder() {
        energyRF = new EnergyStorage(10000);
        currentProcessTime = 0;
        inventory = new InventoryTile(6);
    }

    @Override
    public void update() {
        if (!this.hasWorldObj() || getWorld().isRemote || currentProcessTime <= 0) return;
        //todo add into slots when mb inserted
        if (currentProcessTime > 0 && currentProcessTime <= BASE_PROCESS_TIME) {
            if (inventory.getStackInSlot(MB_SLOT_INPUT) == null) {
                currentProcessTime = 0;
                worldObj.markBlockForUpdate(pos);
                return;
            }
            if (energyRF.getEnergyStored() >= RF_TICK) {
                energyRF.extractEnergy(RF_TICK, false);
                ++currentProcessTime;
            }
        }
        if (currentProcessTime >= BASE_PROCESS_TIME) {
            int speed = 0;
            int efficiency = 0;
            int capacity = 0;
            boolean io = false;
            for (int i = 0; i < 4; i++) {
                if (getStackInSlot(i) != null) {
                    if (getStackInSlot(i).getItem() == ItemHandler.speedProcessor)
                        speed += getStackInSlot(i).stackSize;
                    else if (getStackInSlot(i).getItem() == ItemHandler.capRam)
                        capacity += getStackInSlot(i).stackSize;
                    else if (getStackInSlot(i).getItem() == ItemHandler.effFan)
                        efficiency += getStackInSlot(i).stackSize;
                    else if (getStackInSlot(i).getItem() == ItemHandler.ioPort)
                        io = true;

                    inventory.setStackInSlot(null, i);
                }
            }
            inventory.setStackInSlot(null, MB_SLOT_INPUT);
            writeToMB(speed, efficiency, capacity, io);
        }
    }

    public void writeToMB(int speed, int efficiency, int capacity, boolean io) {

        ItemStack newMB = new ItemStack(ItemHandler.upgradeMB, 1);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Speed", speed);
        tag.setInteger("Efficiency", efficiency);
        tag.setInteger("Capacity", capacity);
        tag.setBoolean("AutoOutput", io);
        newMB.setTagCompound(tag);
        inventory.setStackInSlot(newMB, MB_SLOT_OUTPUT);
    }

    public void mergeMB() {
        currentProcessTime = 1;
    }

    /*******************************************************************************************************************
     ************************************** Energy Functions ***********************************************************
     *******************************************************************************************************************/

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        int actual = energyRF.receiveEnergy(maxReceive, simulate);
        if (actual > 0) this.getWorld().markBlockForUpdate(this.pos);
        return actual;
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

    @Override
    public void spawnActiveParticles(double x, double y, double z) {

    }

    /*******************************************************************************************************************
     ************************************** Inventory Functions ********************************************************
     *******************************************************************************************************************/

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[] {};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return false;
    } //TODO

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        switch (index) {

        }
        return true;
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0:
                return currentProcessTime;
            default:
                return 0;
        }

    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0:
                currentProcessTime = value;
        }
    }

    @Override
    public int getFieldCount() {
        return 1;
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
        inventory.readFromNBT(tag, this, ":MainInv");
        currentProcessTime = tag.getInteger("CurrentProcessTime");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        energyRF.writeToNBT(tag);
        inventory.writeToNBT(tag, ":MainInv");
        tag.setInteger("CurrentProcessTime", currentProcessTime);
    }
}
