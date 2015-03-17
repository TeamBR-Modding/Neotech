package com.dyonovan.jatm.common.tileentity.machine;

import cofh.api.energy.EnergyStorage;
import com.dyonovan.jatm.common.tileentity.BaseMachine;
import com.dyonovan.jatm.common.tileentity.InventoryTile;
import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class TileElectricFurnace extends BaseMachine implements IUpdatePlayerListBox {

    public EnergyStorage energyRF;
    public InventoryTile inventory;
    public int currentProcessTime;
    private ItemStack input, output;

    private static final int RF_TICK = 20;
    public static final int TOTAL_PROCESS_TIME = 150;
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;

    public TileElectricFurnace() {
        energyRF = new EnergyStorage(10000);
        currentProcessTime = 0;
        inventory = new InventoryTile(2);
    }

    private void doSmelt() {
        if (canSmelt() || currentProcessTime > 0) {
            if (currentProcessTime == 0) {
                input = inventory.getStackInSlot(INPUT_SLOT);
                currentProcessTime = 1;
            }
            if (currentProcessTime > 0 && currentProcessTime < TOTAL_PROCESS_TIME) {
                if (inventory.getStackInSlot(INPUT_SLOT) == null || inventory.getStackInSlot(INPUT_SLOT).isItemEqual(input)) {
                    currentProcessTime = 0;
                    return;
                }
                if (energyRF.getEnergyStored() >= RF_TICK ) {
                    energyRF.modifyEnergyStored(-RF_TICK);
                    ++currentProcessTime;
                }
            }
            if (currentProcessTime >= TOTAL_PROCESS_TIME) {
                inventory.modifyStack(INPUT_SLOT, -1);
                if (inventory.getStackInSlot(OUTPUT_SLOT) == null)
                    inventory.setStackInSlot(output, OUTPUT_SLOT);
                else inventory.getStackInSlot(OUTPUT_SLOT).stackSize += output.stackSize;
                currentProcessTime = 0;
            }
        }
    }

    private boolean canSmelt() {
        if (inventory.getStackInSlot(INPUT_SLOT) == null) return false;

        output = FurnaceRecipes.instance().getSmeltingResult(inventory.getStackInSlot(INPUT_SLOT));
        return output != null && !(inventory.getStackInSlot(OUTPUT_SLOT) != null &&
                !inventory.getStackInSlot(OUTPUT_SLOT).isItemEqual(output) &&
                inventory.getStackInSlot(OUTPUT_SLOT).stackSize < inventory.getStackInSlot(OUTPUT_SLOT).getMaxStackSize());
    }

    /*******************************************************************************************************************
     ************************************** Energy Functions ***********************************************************
     *******************************************************************************************************************/

    @Override
    public int receiveEnergy(EnumFaceDirection from, int maxReceive, boolean simulate) {
        return energyRF.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(EnumFaceDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    /*******************************************************************************************************************
     ************************************** Inventory Functions ********************************************************
     *******************************************************************************************************************/

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[] {0,1};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return true;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;//TODO
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0:
                return currentProcessTime;
            default:
                return 0;
        }

    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0:
                currentProcessTime = value;
        }
    }

    @Override
    public int getFieldCount() {
        return 1;
    }

    @Override
    public void clear() {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            inventory.setStackInSlot(null, i);
        }
    }

    /*******************************************************************************************************************
     **************************************** Tile Functions ***********************************************************
     *******************************************************************************************************************/

    @Override
    public void update() {
        if (!this.hasWorldObj()) return;
        World world = this.getWorld();
        if (world.isRemote) return;
        doSmelt();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energyRF.readFromNBT(tag);
        inventory.readFromNBT(tag, this);
        currentProcessTime = tag.getInteger("CurrentProcessTime");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        energyRF.writeToNBT(tag);
        inventory.writeToNBT(tag);
        tag.setInteger("CurrentProcessTime", currentProcessTime);
    }
}
