package com.dyonovan.jatm.common.cable;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;

public class TileCable extends TileEntity implements IEnergyHandler, IUpdatePlayerListBox {
    public static int fdTransfer = 200;
    EnergyStorage[] storageBuffers = new EnergyStorage[6];

    public TileCable() {
        for(int n = 0; n < 6; ++n) {
            this.storageBuffers[n] = new EnergyStorage(fdTransfer);
        }
    }

    public void update() {
        if(this.worldObj != null) {
            int leftThisTick = fdTransfer;
            EnumFacing[] var2 = EnumFacing.values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                EnumFacing face = var2[var4];
                EnergyStorage buffer = this.storageBuffers[face.ordinal()];
                if(buffer.getEnergyStored() >= 1) {
                    BlockPos currentPos = this.pos.offset(face);
                    TileEntity te = this.worldObj.getTileEntity(currentPos);
                    if(te instanceof IEnergyReceiver) {
                        IEnergyReceiver receiver = (IEnergyReceiver)te;
                        if(!receiver.canConnectEnergy(face)) {
                            continue;
                        }

                        int availableToSend = buffer.extractEnergy(leftThisTick, true);
                        if(availableToSend < 1) {
                            continue;
                        }

                        int transferred = receiver.receiveEnergy(face, availableToSend, false);
                        if(transferred == 0) {
                            continue;
                        }

                        buffer.extractEnergy(transferred, false);
                        worldObj.markBlockForUpdate(currentPos);
                    }

                    if(leftThisTick < 1) {
                        break;
                    }
                }
            }

        }
    }

    public int receiveEnergy(EnumFacing facing, int maxReceive, boolean simulate) {
        int left = maxReceive;
        int total = 0;
        ArrayList sides = new ArrayList();

        int startLeft;
        for(startLeft = 0; startLeft < 6; ++startLeft) {
            sides.add(Integer.valueOf(startLeft));
        }

        do {
            startLeft = left;
            int share = left / 5;
            if(share < 1) {
                share = left;
            }

            for(int n = 0; n < 6; ++n) {
                if(((Integer)sides.get(n)).intValue() != facing.getOpposite().ordinal()) {
                    int result = this.storageBuffers[((Integer)sides.get(n)).intValue()].receiveEnergy(share, simulate);
                    left -= result;
                    total += result;
                    if(left < 1) {
                        break;
                    }
                }
            }
        } while(left != startLeft && left >= 1);

        return total;
    }

    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        int[] energyarray = new int[6];

        for(int energy = 0; energy < 6; ++energy) {
            energyarray[energy] = this.storageBuffers[energy].getEnergyStored();
        }

        NBTTagIntArray var4 = new NBTTagIntArray(energyarray);
        nbt.setTag("buffers", var4);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        int[] energy = nbt.getIntArray("buffers");
        if(energy != null && energy.length == 6) {
            for(int n = 0; n < 6; ++n) {
                this.storageBuffers[n].setEnergyStored(energy[n]);
            }

        }
    }

    public boolean canConnectEnergy(EnumFacing facing) {
        return true;
    }

    public int extractEnergy(EnumFacing facing, int maxExtract, boolean simulate) {
        return 0;
    }

    public int getEnergyStored(EnumFacing facing) {
        return this.storageBuffers[facing.ordinal()].getEnergyStored();
    }

    public int getMaxEnergyStored(EnumFacing facing) {
        return fdTransfer;
    }
}