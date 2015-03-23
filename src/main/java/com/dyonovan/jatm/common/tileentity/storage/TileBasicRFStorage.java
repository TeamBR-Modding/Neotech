package com.dyonovan.jatm.common.tileentity.storage;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import com.dyonovan.jatm.common.blocks.BlockBakeable;
import com.dyonovan.jatm.common.blocks.IExpellable;
import com.dyonovan.jatm.common.tileentity.BaseMachine;
import com.dyonovan.jatm.common.tileentity.InventoryTile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class TileBasicRFStorage extends BaseMachine implements IRFStorage, IExpellable, IUpdatePlayerListBox, IEnergyReceiver, IEnergyProvider {

    private static final int RF_TOTAL_1 = 250000;
    private static final int RF_TICK_1 = 200;
    public static final int CHARGE_SLOT_1 = 0;
    public static final int CHARGE_SLOT_2 = 1;
    public static final int CHARGE_SLOT_3 = 2;

    protected EnergyStorage energyRF;

    public TileBasicRFStorage() {
        energyRF = new EnergyStorage(RF_TOTAL_1, RF_TICK_1);
        inventory = new InventoryTile(1);
    }

    @Override
    public void update() {
        if(worldObj != null && !worldObj.isRemote)
            transferEnergy();
    }

    private void transferEnergy() {
        EnumFacing out = (EnumFacing) getWorld().getBlockState(this.pos).getValue(BlockBakeable.PROPERTY_FACING);
        TileEntity tile = getWorld().getTileEntity(this.pos.offset(out));
        if (tile instanceof IEnergyReceiver) {
            int avail = Math.min(energyRF.getMaxExtract(), energyRF.getEnergyStored());
            int amount = ((IEnergyReceiver) tile).receiveEnergy(out.getOpposite(), avail, true);
            int actual = ((IEnergyReceiver) tile).receiveEnergy(out.getOpposite(), amount, false);
            energyRF.extractEnergy(actual, false);
        }
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
        if (from != getWorld().getBlockState(pos).getValue(BlockBakeable.PROPERTY_FACING)) {
            int amount = energyRF.receiveEnergy(maxReceive, simulate);
            worldObj.markBlockForUpdate(this.pos);
            return amount;
        }
        return 0;
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
        return new int[] {0};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return true;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return true;
    }

    @Override
    public int getSizeInventory() {
        return this.inventory.getSizeInventory();
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

    @Override
    public int getTier() {
        return 1;
    }

    @Override
    public EnergyStorage getRF() {
        return energyRF;
    }

    @Override
    public BlockPos getTilePos() {
        return this.pos;
    }
}
