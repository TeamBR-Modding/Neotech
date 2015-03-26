package com.dyonovan.neotech.common.pipe.storage;

import com.dyonovan.neotech.common.pipe.Pipe;
import com.dyonovan.neotech.common.tileentity.InventoryTile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class ItemBuffer<P extends Pipe> implements IPipeBuffer<InventoryTile, ItemStack, P> {

    protected InventoryTile central;
    public EnumFacing receivedDirection;
    protected int moveInTimer;
    protected int moveOutTimer;
    protected P pipe;

    @Override
    public void setParentPipe(P parent) {
        this.pipe = parent;
    }

    @Override
    public void setBuffers(InventoryTile[] array) {
        central = array[0];
    }

    @Override
    public InventoryTile[] getBuffers() {
        return null;
    }

    @Override
    public InventoryTile getStorageForFace(EnumFacing face) {
        return central;
    }

    @Override
    public boolean canBufferSend(InventoryTile buffer) {
        moveOutTimer--;
        return central.getStackInSlot(0) != null && moveOutTimer < 20;
    }

    @Override
    public boolean canBufferExtract(InventoryTile buffer) {
        moveInTimer--;
        return central.getStackInSlot(0) == null && moveInTimer < 20;
    }

    @Override
    public ItemStack acceptResource(int maxAmount, EnumFacing inputFace, ItemStack resource, boolean simulate) {
        receivedDirection = inputFace;
        moveInTimer = 20;
        moveOutTimer = 20;
        central.setStackInSlot(resource, 0);
        return null;
    }

    @Override
    public ItemStack removeResource(int maxAmount, EnumFacing outputFace, boolean simulate) {
        if(central.getStackInSlot(0) != null) {
            receivedDirection = null;
            ItemStack output = central.getStackInSlot(0).copy();
            if(!simulate) {
                central.getStackInSlot(0).stackSize -= maxAmount;
                if (central.getStackInSlot(0).stackSize < 1)
                    central.setStackInSlot(null, 0);
            }
            return output;
        }
        return null;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        central.writeToNBT(tag, "");
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        central.readFromNBT(tag, 1, "");
    }
}
