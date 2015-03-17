package com.dyonovan.jatm.common.tileentity;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryTile {

    private ItemStack inventory[];
    public InventoryTile(int size) {
        inventory = new ItemStack[size];
    }
    public ItemStack getStackInSlot(int slot) {
        return inventory[slot];
    }
    public void setStackInSlot(ItemStack stack, int slot) {
        inventory[slot] = stack;
    }
    public int getSizeInventory() {
        return inventory.length;
    }
    public ItemStack [] getValues() { return inventory; }
    public void clear() {
        for(int i = 0; i < inventory.length; i++)
            inventory[i] = null;
    }
    public void readFromNBT(NBTTagCompound tagCompound, IInventory parent) {
        NBTTagList itemsTag = tagCompound.getTagList("Items", 10);
        this.inventory = new ItemStack[parent.getSizeInventory()];
        for (int i = 0; i < itemsTag.tagCount(); i++)
        {
            NBTTagCompound nbtTagCompound1 = itemsTag.getCompoundTagAt(i);
            NBTBase nbt = nbtTagCompound1.getTag("Slot");
            int j;
            if ((nbt instanceof NBTTagByte)) {
                j = nbtTagCompound1.getByte("Slot") & 0xFF;
            } else {
                j = nbtTagCompound1.getShort("Slot");
            }
            if ((j >= 0) && (j < this.inventory.length)) {
                this.inventory[j] = ItemStack.loadItemStackFromNBT(nbtTagCompound1);
            }
        }
    }
    public void writeToNBT(NBTTagCompound tagCompound) {
        NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < this.inventory.length; i++) {
            if (this.inventory[i] != null)
            {
                NBTTagCompound nbtTagCompound1 = new NBTTagCompound();
                nbtTagCompound1.setShort("Slot", (short)i);
                this.inventory[i].writeToNBT(nbtTagCompound1);
                nbtTagList.appendTag(nbtTagCompound1);
            }
        }
        tagCompound.setTag("Items", nbtTagList);
    }
}
