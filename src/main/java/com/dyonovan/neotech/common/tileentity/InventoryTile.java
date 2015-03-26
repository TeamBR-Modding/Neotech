package com.dyonovan.neotech.common.tileentity;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryTile {

    private ItemStack inventory[];

    /**
     * Create an inventory with the given size (number of slots)
     * @param size How many slots the inventory has
     */
    public InventoryTile(int size) {
        inventory = new ItemStack[size];
    }

    /**
     * Get the stack in the specified slot
     * @param slot Slot index
     * @return {@link ItemStack} in the slot (can be null)
     */
    public ItemStack getStackInSlot(int slot) {
        return inventory[slot];
    }

    /**
     * Put a stack in the given slot
     * @param stack {@link ItemStack} to put in the slot
     * @param slot Slot index
     */
    public void setStackInSlot(ItemStack stack, int slot) {
        inventory[slot] = stack;
    }

    /**
     * Get the size of the inventory in slots
     * @return How many slots the inventory has
     */
    public int getSizeInventory() {
        return inventory.length;
    }

    /**
     * Modify the stack size in the given slot
     * @param slot Slot index
     * @param amount Offset (positive of negative)
     * @return The resulting itemstack
     */
    public ItemStack modifyStack(int slot, int amount) {
        inventory[slot].stackSize = inventory[slot].stackSize + amount;
        if (inventory[slot].stackSize <= 0) inventory[slot] = null;
        return inventory[slot];
    }

    /**
     * Return the array of items in the inventory
     * @return An array of {@link ItemStack}s
     */
    public ItemStack [] getValues() { return inventory; }

    /**
     * Remove all items from the inventory
     */
    public void clear() {
        for(int i = 0; i < inventory.length; i++)
            inventory[i] = null;
    }

    /**
     * Helper method to communicate data
     *
     * @deprecated remove the need for parent, can cause issues in inventories with more than one Inventory use readFromNBT(NBTTagCompound tagCompound)
     *
     * @param tagCompound Tag to read from
     * @param parent The parent inventory
     */
    @Deprecated
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

    /**
     * Helper method to communicate data
     * @param tagCompound Tag to read from
     */
    public void readFromNBT(NBTTagCompound tagCompound, IInventory parent, String separation) {
        NBTTagList itemsTag = tagCompound.getTagList("Items" + separation, 10);
        this.inventory = new ItemStack[parent.getSizeInventory()];
        for (int i = 0; i < itemsTag.tagCount(); i++)
        {
            NBTTagCompound nbtTagCompound1 = itemsTag.getCompoundTagAt(i);
            NBTBase nbt = nbtTagCompound1.getTag("Slot" + separation);
            int j;
            if ((nbt instanceof NBTTagByte)) {
                j = nbtTagCompound1.getByte("Slot" + separation) & 0xFF;
            } else {
                j = nbtTagCompound1.getShort("Slot" + separation);
            }
            if ((j >= 0) && (j < this.inventory.length)) {
                this.inventory[j] = ItemStack.loadItemStackFromNBT(nbtTagCompound1);
            }
        }
    }

    @Deprecated
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

    public void writeToNBT(NBTTagCompound tagCompound, String separation) {
        NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < this.inventory.length; i++) {
            if (this.inventory[i] != null)
            {
                NBTTagCompound nbtTagCompound1 = new NBTTagCompound();
                nbtTagCompound1.setShort("Slot" + separation, (short)i);
                this.inventory[i].writeToNBT(nbtTagCompound1);
                nbtTagList.appendTag(nbtTagCompound1);
            }
        }
        tagCompound.setTag("Items" + separation, nbtTagList);
    }
}
