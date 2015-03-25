package com.dyonovan.jatm.common.pipe.energy;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import com.dyonovan.jatm.common.pipe.Pipe;
import com.dyonovan.jatm.common.pipe.storage.EnergyBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class PipeBasicEnergy extends Pipe<EnergyBuffer> implements IEnergyReceiver, IEnergyProvider {

    @Override
    public void setBuffer() {
        buffer = new EnergyBuffer();
    }

    @Override
    public void initBuffers() {
        EnergyStorage[] builtBuffers = new EnergyStorage[6];
        for(int i = 0; i < 6; i++) {
            builtBuffers[i] = new EnergyStorage(getMaximumTransferRate());
        }
        buffer.setBuffers(builtBuffers);
        buffer.setParentPipe(this);
    }

    @Override
    public int getMaximumTransferRate() {
        return 200;
    }

    @Override
    public boolean isPipeConnected(BlockPos pos, EnumFacing facing) {
        TileEntity te = worldObj.getTileEntity(pos);
        return ((te instanceof IEnergyProvider && ((IEnergyProvider)te).canConnectEnergy(facing)) || (te instanceof IEnergyReceiver && ((IEnergyReceiver)te).canConnectEnergy(facing)));
    }


    @Override
    public boolean canTileReceive(TileEntity tile) {
        return tile instanceof IEnergyReceiver;
    }

    @Override
    public void giveToTile(TileEntity tile, EnumFacing face) {
        buffer.getBuffers()[face.ordinal()].extractEnergy(((IEnergyReceiver)tile).receiveEnergy(face.getOpposite(), buffer.getBuffers()[face.ordinal()].extractEnergy(getMaximumTransferRate(), true), false), false);
    }

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return buffer.acceptResource(maxReceive, from, null, simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return buffer.getBuffers()[from.ordinal()].getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return getMaximumTransferRate();
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }
}
