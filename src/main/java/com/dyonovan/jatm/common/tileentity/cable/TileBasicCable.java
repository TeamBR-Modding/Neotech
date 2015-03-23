package com.dyonovan.jatm.common.tileentity.cable;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
//Design inspired from FluxDucts
public class TileBasicCable extends TileEntity implements IEnergyReceiver, IEnergyProvider, IUpdatePlayerListBox {

    protected static int transferRate = 100;
    protected EnergyStorage[] faceBuffers = new EnergyStorage[6];

    public TileBasicCable() {
        for(int i = 0; i < 6; i++)
            faceBuffers[i] = new EnergyStorage(transferRate);
    }

    @Override
    public void update() {
        if(this.worldObj != null) {
            int energyPerTick = transferRate;
            for (EnumFacing face : EnumFacing.values()) {
                EnergyStorage buffer = this.faceBuffers[face.ordinal()];
                if (buffer.getEnergyStored() >= 1) {
                    TileEntity te = this.worldObj.getTileEntity(this.pos.offset(face));
                    if (te instanceof IEnergyReceiver) {
                        IEnergyReceiver receiver = (IEnergyReceiver) te;
                        if (receiver.canConnectEnergy(face) && buffer.extractEnergy(energyPerTick, true) > 0)
                            buffer.extractEnergy(receiver.receiveEnergy(face.getOpposite(), buffer.extractEnergy(energyPerTick, true), false), false);
                    }
                }
            }
        }
    }

    @Override
    public int receiveEnergy(EnumFacing facing, int maxReceive, boolean simulate) {
        int remainingEnergy = maxReceive;
        int total = 0;
        ArrayList<Integer> sides = new ArrayList<>();
        int startingEnergy;
        for(EnumFacing en : EnumFacing.values()) {
            if(isCableConnected(pos.offset(en)) && en != facing)
                sides.add(en.ordinal());
        }
       if(!sides.isEmpty()) {
           do {
               startingEnergy = remainingEnergy;
               int share = remainingEnergy / sides.size();
               if (share < 1) {
                   share = remainingEnergy;
               }
               for (Integer i : sides) {
                   int drained = this.faceBuffers[i].receiveEnergy(share, simulate);
                   remainingEnergy -= drained;
                   total += drained;
                   if (remainingEnergy < 1) {
                       break;
                   }
               }
           } while (remainingEnergy != startingEnergy && remainingEnergy >= 1);
       }
        return total;
    }

    public boolean isCableConnected(BlockPos pos) {
        TileEntity te = worldObj.getTileEntity(pos);
        return te instanceof IEnergyProvider || te instanceof IEnergyReceiver;
    }

    @Override
    public int extractEnergy(EnumFacing facing, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public boolean canConnectEnergy(EnumFacing facing) {
        return true;
    }

    @Override
    public int getEnergyStored(EnumFacing facing) {
        return faceBuffers[facing.ordinal()].getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing facing) {
        return transferRate;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        int[] energyarray = new int[6];
        for(int energy = 0; energy < 6; ++energy) {
            energyarray[energy] = this.faceBuffers[energy].getEnergyStored();
        }
        NBTTagIntArray var4 = new NBTTagIntArray(energyarray);
        nbt.setTag("buffers", var4);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        int[] energy = nbt.getIntArray("buffers");
        if(energy != null && energy.length == 6) {
            for(int n = 0; n < 6; ++n) {
                this.faceBuffers[n].setEnergyStored(energy[n]);
            }
        }
    }
}