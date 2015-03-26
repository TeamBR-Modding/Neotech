package com.dyonovan.neotech.common.tileentity.machine;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import com.dyonovan.neotech.common.blocks.IExpellable;
import com.dyonovan.neotech.common.tileentity.BaseMachine;
import com.dyonovan.neotech.common.tileentity.InventoryTile;
import com.dyonovan.neotech.handlers.BlockHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.oredict.OreDictionary;

public class TileThermalBinder extends BaseMachine implements IEnergyReceiver, IUpdatePlayerListBox, IExpellable {

    public int currentProcessTime;
    public EnergyStorage energyRF;
    public FluidTank tank;

    private static final int RF_TICK= 100;
    public static final int TOTAL_PROCESS_TIME = 200;
    public static final int MB_PER_INGOT = 1000;
    public static final int INPUT_SLOT_1 = 0;
    public static final int INPUT_SLOT_2 = 1;
    public static final int INPUT_SLOT_3 = 2;
    public static final int INPUT_SLOT_4 = 3;
    public static final int MB_SLOT = 4;
    public static final int INGOT_SLOT = 5;

    public TileThermalBinder() {
        energyRF = new EnergyStorage(10000);
        currentProcessTime = 0;
        inventory = new InventoryTile(6);
        tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 4);
    }

    @Override
    public void update() {
        if (!this.hasWorldObj() || getWorld().isRemote) return;

        meltTin();
    }

    public void meltTin() {
        if (inventory.getStackInSlot(INGOT_SLOT) == null) return;

        if (tank.getFluid() == null || tank.getFluid().amount <= 3000 ) {
            inventory.modifyStack(INGOT_SLOT, -1);
            if (tank.getFluid() == null) tank.setFluid(new FluidStack(BlockHandler.moltenTin, 1000));
            else tank.getFluid().amount += 1000;
        }
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
            case INGOT_SLOT:
                return OreDictionary.getOres("ingotTin").contains(stack.getItem());
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
