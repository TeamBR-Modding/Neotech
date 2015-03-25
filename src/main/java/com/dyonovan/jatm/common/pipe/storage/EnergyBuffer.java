package com.dyonovan.jatm.common.pipe.storage;

import cofh.api.energy.EnergyStorage;
import com.dyonovan.jatm.common.pipe.Pipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;

public class EnergyBuffer<P extends Pipe> implements IPipeBuffer<EnergyStorage, P> {

    protected EnergyStorage[] buffers;
    protected P pipe;

    @Override
    public void setParentPipe(P parent) {
        this.pipe = parent;
    }

    @Override
    public void setBuffers(EnergyStorage[] array) {
        buffers = array;
    }

    @Override
    public EnergyStorage[] getBuffers() {
        return buffers;
    }

    @Override
    public EnergyStorage getStorageForFace(EnumFacing face) {
        return buffers[face.ordinal()];
    }

    @Override
    public boolean canBufferSend(EnergyStorage buffer) {
        return buffer.getEnergyStored() > 0;
    }

    @Override
    public int acceptResource(int maxAmount, EnumFacing inputFace, EnergyStorage resource, boolean simulate) {
        int remainingEnergy = maxAmount;
        int total = 0;
        ArrayList<Integer> sides = new ArrayList<>();
        int startingEnergy;
        for(EnumFacing en : EnumFacing.values()) {
            if(pipe.isPipeConnected(pipe.getPos().offset(en), en) && en != inputFace)
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
                    int drained = this.buffers[i].receiveEnergy(share, simulate);
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

    @Override
    public EnergyStorage removeResource(int maxAmount, EnumFacing outputFace, boolean simulate) {
        return null;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        if(buffers != null) {
            int[] energyarray = new int[6];
            for (int energy = 0; energy < 6; ++energy) {
                energyarray[energy] = this.buffers[energy].getEnergyStored();
            }
            NBTTagIntArray var4 = new NBTTagIntArray(energyarray);
            tag.setTag("buffers", var4);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if(buffers != null) {
            int[] energy = tag.getIntArray("buffers");
            if (energy != null && energy.length == 6) {
                for (int n = 0; n < 6; ++n) {
                    this.buffers[n].setEnergyStored(energy[n]);
                }
            }
        }
    }
}
