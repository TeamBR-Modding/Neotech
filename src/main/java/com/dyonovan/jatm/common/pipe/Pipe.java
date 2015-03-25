package com.dyonovan.jatm.common.pipe;

import com.dyonovan.jatm.common.pipe.storage.IPipeBuffer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public abstract class Pipe<T extends IPipeBuffer> extends TileEntity implements IUpdatePlayerListBox {
    protected T buffer;

    public abstract void setBuffer();

    public abstract void initBuffers();

    public abstract int getMaximumTransferRate();

    public abstract boolean isPipeConnected(BlockPos pos, EnumFacing facing);

    public abstract boolean canTileReceive(TileEntity tile);

    public abstract void giveToTile(TileEntity tile, EnumFacing face);

    public Pipe() {
        setBuffer();
        initBuffers();
    }

    @Override
    public void update() {
        for(EnumFacing face : EnumFacing.values()) {
            if(buffer.canBufferSend(buffer.getStorageForFace(face)) &&
                    isPipeConnected(pos.offset(face), face) &&
                    canTileReceive(worldObj.getTileEntity(pos.offset(face)))) {
                giveToTile(worldObj.getTileEntity(pos.offset(face)), face);
            }
        }
    }


    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        buffer.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        buffer.readFromNBT(nbt);
    }
}
