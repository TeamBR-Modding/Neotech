package com.dyonovan.jatm.common.tileentity.machine;

import com.dyonovan.jatm.common.tileentity.InventoryTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IChatComponent;

public class TileEntityCrafter extends TileEntity implements IInventory {
    private InventoryTile inventoryCraftingGrid1;
    private InventoryTile inventoryCraftingGrid2;

    public TileEntityCrafter() {
        inventoryCraftingGrid1 = new InventoryTile(10);
        inventoryCraftingGrid2 = new InventoryTile(10);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        inventoryCraftingGrid1.readFromNBT(tag);
        inventoryCraftingGrid2.readFromNBT(tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        inventoryCraftingGrid1.writeToNBT(tag);
        inventoryCraftingGrid2.writeToNBT(tag);
    }

    @Override
    public int getSizeInventory() {
        return 0;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index < 10 ? inventoryCraftingGrid1.getStackInSlot(index) : inventoryCraftingGrid2.getStackInSlot(index - 10);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return index < 10 ? inventoryCraftingGrid1.modifyStack(index, -count) : inventoryCraftingGrid2.modifyStack(index - 10, -count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return index < 10 ? inventoryCraftingGrid1.getStackInSlot(index) : inventoryCraftingGrid2.getStackInSlot(index - 10);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if(index < 10)
            inventoryCraftingGrid1.setStackInSlot(stack, index);
        else
            inventoryCraftingGrid2.setStackInSlot(stack, index - 10);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index != 9 && index != 18;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public String getCommandSenderName() {
        return null;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public IChatComponent getDisplayName() {
        return null;
    }
}
