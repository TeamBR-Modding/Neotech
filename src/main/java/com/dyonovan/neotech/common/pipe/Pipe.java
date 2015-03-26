package com.dyonovan.neotech.common.pipe;

import com.dyonovan.neotech.common.pipe.storage.IPipeBuffer;
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

    public abstract boolean canTileProvide(TileEntity tile);

    public abstract void giveToTile(TileEntity tile, EnumFacing face);

    public abstract void extractFromTile(TileEntity tile, EnumFacing face);

    public Pipe() {
        setBuffer();
        initBuffers();
    }

    @Override
    public void update() {
        if(worldObj.isRemote) return;
        for(EnumFacing face : EnumFacing.values()) {
            //Extract
            if(buffer.canBufferExtract(buffer.getStorageForFace(face)) &&
                    isPipeConnected(pos.offset(face), face) &&
                    canTileProvide(worldObj.getTileEntity(pos.offset(face)))) {
                extractFromTile(worldObj.getTileEntity(pos.offset(face)), face);
            }

            //Transfer
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
