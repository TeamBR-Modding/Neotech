package com.dyonovan.jatm.tileentity;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileGenerator extends TileEntity implements IEnergyHandler, IUpdatePlayerListBox, ISidedInventory {

    protected EnergyStorage energyRF;
    public InventoryTile inventory;
    private int currentBurnTime;
    private int totalBurnTime;

    /**
     * Energy Use per Tick
     */
    private static final int RF_TICK = 20;

    public TileGenerator() {
        energyRF = new EnergyStorage(10000, 80);
        inventory = new InventoryTile(1);
        currentBurnTime = 0;
        totalBurnTime = 0;
    }

    public void generatePower() {
        if (currentBurnTime > 0 || canRun()) {
            if (currentBurnTime == 0) {
                totalBurnTime = GameRegistry.getFuelValue(inventory.getStackInSlot(0));
                if (totalBurnTime == 0) return;
                currentBurnTime = 1;

                if (inventory.getStackInSlot(0).stackSize == 1) inventory.setStackInSlot(null, 0);
                else inventory.getStackInSlot(0).stackSize -= 1;
                this.markDirty();
            }
            if (currentBurnTime > 0 && currentBurnTime < totalBurnTime) {
                energyRF.modifyEnergyStored(RF_TICK);
                currentBurnTime += 1;
            }
            if (currentBurnTime > 0 && currentBurnTime >= totalBurnTime) {
                currentBurnTime = 0;
                totalBurnTime = 0;
            }
        }

    }

    public boolean canRun() {
        return energyRF.getEnergyStored() < energyRF.getMaxEnergyStored() && inventory.getStackInSlot(0) != null;
    }

    /*******************************************************************************************************************
     ************************************** Energy Functions ***********************************************************
     *******************************************************************************************************************/

    @Override
    public int receiveEnergy(EnumFaceDirection from, int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(EnumFaceDirection from, int maxExtract, boolean simulate) {
        return energyRF.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored(EnumFaceDirection from) {
        return energyRF.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFaceDirection from) {
        return energyRF.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(EnumFaceDirection from) {
        return true;
    }

    /*******************************************************************************************************************
     **************************************** Tile Functions ***********************************************************
     *******************************************************************************************************************/
    @Override
    public void update() {
        if (!worldObj.isRemote) return;

        generatePower();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energyRF.readFromNBT(tag);
        inventory.readFromNBT(tag, this);
        currentBurnTime = tag.getInteger("CurrentBurnTime");
        totalBurnTime = tag.getInteger("TotalBurnTime");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        energyRF.writeToNBT(tag);
        inventory.writeToNBT(tag);

    }

    /*******************************************************************************************************************
     **************************************** Inventory Functions ******************************************************
     *******************************************************************************************************************/

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[] {0};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return true;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return inventory.getStackInSlot(index);
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
        super.markDirty();
        return itemstack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        ItemStack stack = getStackInSlot(index);
        if (stack != null) {
            setInventorySlotContents(index, null);
        }
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory.setStackInSlot(stack, index);
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
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return GameRegistry.getFuelValue(stack) > 0;
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0:
                return currentBurnTime;
            case 1:
                return totalBurnTime;
            default:
                return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0:
                currentBurnTime = value;
                break;
            case 1:
                totalBurnTime = value;
                break;
        }
    }

    @Override
    public int getFieldCount() {
        return 2;
    }

    @Override
    public void clear() {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            inventory.setStackInSlot(null, i);
        }
    }

    @Override
    public String getName() {
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
