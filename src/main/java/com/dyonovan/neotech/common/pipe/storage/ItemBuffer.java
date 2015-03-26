package com.dyonovan.neotech.common.pipe.storage;

import com.dyonovan.neotech.common.pipe.Pipe;
import com.dyonovan.neotech.common.tileentity.InventoryTile;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemBuffer<P extends Pipe> implements IPipeBuffer<InventoryTile, ItemStack, P> {

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
        return null;
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
    public ItemStack acceptResource(int maxAmount, EnumFacing inputFace, ItemStack resource, boolean simulate) {

        List<EnumFacing> dirs = new ArrayList<>();
        for(EnumFacing facing : EnumFacing.values()) {
            if(pipe.isPipeConnected(pipe.getPos().offset(facing), facing) && facing != inputFace)
                dirs.add(facing);
        }

        Collections.shuffle(dirs);

        for(EnumFacing face : dirs) {
            if(buffers[face.ordinal()].getStackInSlot(0) == null) {
                if(pipe.getWorld().getTileEntity(pipe.getPos().offset(face)) instanceof ISidedInventory) {
                    ISidedInventory otherInv = (ISidedInventory)pipe.getWorld().getTileEntity(pipe.getPos().offset(face));
                    boolean flag = false;
                    for(int i : otherInv.getSlotsForFace(face.getOpposite())) {
                        if(!otherInv.canInsertItem(i, resource, face.getOpposite()))
                            flag = true;
                    }
                    if(flag)
                        continue;
                }
                if(!simulate)
                    buffers[face.ordinal()].setStackInSlot(resource.copy(), 0);
                return resource;
            }
        }

        return null;
    }

    @Override
    public ItemStack removeResource(int maxAmount, EnumFacing outputFace, boolean simulate) {
        ItemStack outputStack = buffers[outputFace.ordinal()].getStackInSlot(0).copy();
        if(!simulate)
            buffers[outputFace.ordinal()].setStackInSlot(null, 0);
        return outputStack;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        for(int i = 0; i < 6; i++)
            buffers[i].writeToNBT(tag, String.valueOf(i));
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        buffers = new InventoryTile[6];
        for(int i = 0; i < 6; i++) {
            buffers[i] = new InventoryTile(1);
            buffers[i].readFromNBT(tag, 6, String.valueOf(i));
        }
    }
}
