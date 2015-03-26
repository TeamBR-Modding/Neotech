package com.dyonovan.neotech.common.pipe.storage;

import com.dyonovan.neotech.common.pipe.Pipe;
import com.dyonovan.neotech.common.tileentity.InventoryTile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class ItemBuffer<P extends Pipe> implements IPipeBuffer<InventoryTile, P> {

    protected InventoryTile[] buffers;
    protected P pipe;

    @Override
    public void setParentPipe(P parent) {
        this.pipe = parent;
    }

    @Override
    public void setBuffers(InventoryTile[] array) {
        buffers = array;
    }

    @Override
    public InventoryTile[] getBuffers() {
        return buffers;
    }

    @Override
    public InventoryTile getStorageForFace(EnumFacing face) {
        return buffers[face.ordinal()];
    }

    @Override
    public boolean canBufferSend(InventoryTile buffer) {
        return buffer.getStackInSlot(0) != null;
    }

    @Override
    public boolean canBufferExtract(InventoryTile buffer) {
        return buffer.getStackInSlot(0) == null;
    }

    @Override
    public int acceptResource(int maxAmount, EnumFacing inputFace, InventoryTile resource, boolean simulate) {
        return 0;
    }

    @Override
    public int removeResource(int maxAmount, EnumFacing outputFace, boolean simulate) {
        return 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        for(int i = 0; i < buffers.length; i++)
            buffers[i].writeToNBT(tag, String.valueOf(i));
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        buffers = new InventoryTile[6];
        for(int i = 0; i < 6; i++)
            buffers[i].readFromNBT(tag, 6, String.valueOf(i));
    }
}
