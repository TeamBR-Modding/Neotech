package com.dyonovan.jatm.common.tileentity.storage;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import com.dyonovan.jatm.common.blocks.BlockBakeable;
import com.dyonovan.jatm.common.tileentity.InventoryTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileAdvancedRFStorage extends TileBasicRFStorage implements IRFStorage {

    private static final int RF_TOTAL_2 = 1000000;
    private static final int RF_TICK_2 = 1000;

    public TileAdvancedRFStorage() {
        inventory = new InventoryTile(2);
        energyRF = new EnergyStorage(RF_TOTAL_2, RF_TICK_2);
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

    @Override
    public int getTier() { return 2; }

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
        return new int[] {0, 1};
    }
}
