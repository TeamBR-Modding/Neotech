package com.dyonovan.neotech.common.pipe.storage;

import com.dyonovan.neotech.common.pipe.Pipe;
import com.dyonovan.neotech.common.tileentity.InventoryTile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import java.util.Arrays;
import java.util.HashMap;

public class ItemBuffer<P extends Pipe> implements IPipeBuffer<InventoryTile, ItemStack, P> {

    public InventoryTile inventory;
    protected boolean[] extractSides;
    protected boolean[] insertSides;
    protected P pipe;

    @Override
    public void setParentPipe(P parent) {
        this.pipe = parent;
    }

    /**
     * For this situation don't pass anything
     * @param array Make it NULL
     */
    @Override
    public void setBuffers(InventoryTile[] array) {
        inventory = new InventoryTile(6);
        extractSides = new boolean[6];
        insertSides = new boolean[6];
        Arrays.fill(extractSides, Boolean.FALSE);
        Arrays.fill(insertSides, Boolean.TRUE);
    }

    @Override
    public InventoryTile[] getBuffers() {
        return null;
    }

    /**
     * For this instance, will just return the inventory
     */
    @Override
    public InventoryTile getStorageForFace(EnumFacing face) {
        return inventory;
    }

    @Override
    public boolean canBufferSend(InventoryTile buffer, EnumFacing face) {
        return insertSides[face.ordinal()];
    }

    public void setCanInsert(EnumFacing face, boolean value) {
        insertSides[face.ordinal()] = value;
    }

    @Override
    public boolean canBufferExtract(InventoryTile buffer, EnumFacing face) {
        return extractSides[face.ordinal()];
    }

    public void setCanExtract(EnumFacing face, boolean value) {
        extractSides[face.ordinal()] = value;
    }

    @Override
    public ItemStack acceptResource(int maxAmount, EnumFacing inputFace, ItemStack resource, boolean simulate) {
        if(resource != null && resource.stackSize > 0) {
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                if (inventory.getStackInSlot(i) == null) {
                    ItemStack mover = resource.copy();
                    if (mover.stackSize >= maxAmount) {
                        mover.stackSize = maxAmount;
                        if(!simulate)
                            resource.stackSize -= mover.stackSize;
                    } else if(mover.stackSize < maxAmount && !simulate) {
                        resource.stackSize -= resource.stackSize;
                    }
                    if (!simulate)
                        inventory.setStackInSlot(mover, i);
                    return resource;
                }
            }
        }
        return null;
    }

    @Override
    public ItemStack removeResource(int maxAmount, EnumFacing outputFace, boolean simulate) {
        for(int i = 0; i < inventory.getSizeInventory(); i++) {
            if(inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).stackSize > 0) {
                ItemStack resource = inventory.getStackInSlot(i).copy();
                if(resource.stackSize > maxAmount) {
                    resource.stackSize = maxAmount;
                    if(!simulate) {
                        inventory.getStackInSlot(i).stackSize -= maxAmount;
                    }
                    return resource;
                }
                if(!simulate)
                    inventory.setStackInSlot(null, i);
                return resource;
            }
        }
        return null;
    }

    public void removeDeadStacks() {
        for(int i = 0; i < inventory.getSizeInventory(); i++) {
            if(inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).stackSize <= 0)
                inventory.setStackInSlot(null, i);
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        inventory.writeToNBT(tag, "");
        for(int i = 0; i < 6; i++) {
            tag.setBoolean("InsertSide" + String.valueOf(i), insertSides[i]);
            tag.setBoolean("ExtractSide" + String.valueOf(i), extractSides[i]);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        inventory.readFromNBT(tag, 6, "");
        insertSides = new boolean[6];
        extractSides = new boolean[6];
        for(int i = 0; i < 6; i++) {
            insertSides[i] = tag.getBoolean("InsertSide" + String.valueOf(i));
            extractSides[i] = tag.getBoolean("ExtractSide" + String.valueOf(i));
        }
    }
}
