package com.dyonovan.jatm.common.tileentity.cable;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class TileBasicCable extends TileEntity implements IEnergyHandler, IUpdatePlayerListBox {

    protected EnergyStorage energyRF;

    public TileBasicCable() {
        energyRF = new EnergyStorage(1200, 200, 200);
    }

    @Override
    public void update() {
        if (!this.hasWorldObj()) return;
        World world = this.getWorld();
        if (world.isRemote) return;

        if ((energyRF.getEnergyStored() > 0)) {
            for (int i = 0; i < EnumFacing.values().length; i++) {
            //for (EnumFacing dir : EnumFacing.values()) {
                TileEntity tile = world.getTileEntity(this.pos.offset(EnumFacing.VALUES[i]));
                if (tile instanceof IEnergyReceiver) {
                    int actual = energyRF.extractEnergy(((IEnergyHandler) tile).receiveEnergy(EnumFacing.VALUES[i], energyRF.extractEnergy(energyRF.getMaxExtract(), true), false), false);
                }
            }
        }
    }

    @Override
    public int receiveEnergy(EnumFacing facing, int maxReceive, boolean simulate) {
        int actual = energyRF.receiveEnergy(maxReceive, simulate);
        return actual;
    }

    @Override
    public int extractEnergy(EnumFacing facing, int maxExtract, boolean simulate) {
        return energyRF.extractEnergy(maxExtract, simulate);
    }

    @Override
    public boolean canConnectEnergy(EnumFacing facing) {
        return true;
    }

    @Override
    public int getEnergyStored(EnumFacing facing) {
        return energyRF.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing facing) {
        return energyRF.getMaxEnergyStored();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        energyRF.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
    }
}