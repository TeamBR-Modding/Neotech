package com.dyonovan.jatm.common.tileentity.machine;

import com.dyonovan.jatm.common.tileentity.InventoryTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
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
        inventoryCraftingGrid1.readFromNBT(tag, this, ":crafter1");
        inventoryCraftingGrid2.readFromNBT(tag, this, ":crafter2");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        inventoryCraftingGrid1.writeToNBT(tag, ":crafter1");
        inventoryCraftingGrid2.writeToNBT(tag, ":crafter2");
    }

    @Override
    public int getSizeInventory() {
        return 10;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index < 10 ? inventoryCraftingGrid1.getStackInSlot(index) : inventoryCraftingGrid2.getStackInSlot(index - 10);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemstack = getStackInSlot(index);
        if(itemstack != null) {
            if(itemstack.stackSize <= count) {
                setInventorySlotContents(index, null);
            }
            itemstack = itemstack.splitStack(count);
        }
        worldObj.markBlockForUpdate(this.getPos());
        return itemstack;
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
        worldObj.markBlockForUpdate(pos);
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

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(pos, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        if (pkt.getTileEntityType() == 0)
            readFromNBT(pkt.getNbtCompound());
    }
}
