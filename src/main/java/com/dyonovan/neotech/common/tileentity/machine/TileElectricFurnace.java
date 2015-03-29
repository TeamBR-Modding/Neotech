package com.dyonovan.neotech.common.tileentity.machine;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import com.dyonovan.neotech.common.blocks.BlockMachine;
import com.dyonovan.neotech.common.blocks.IExpellable;
import com.dyonovan.neotech.common.tileentity.BaseMachine;
import com.dyonovan.neotech.common.tileentity.InventoryTile;
import com.dyonovan.neotech.handlers.BlockHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;

public class TileElectricFurnace extends BaseMachine implements IUpdatePlayerListBox, IEnergyReceiver, IExpellable {

    public int currentProcessTime;
    private ItemStack input, output;
    public EnergyStorage energyRF;

    public static final int RF_TICK = 20;
    public static final int TOTAL_PROCESS_TIME = 150;
    public static final int DEFAULT_RF_CAPACITY = 10000;
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    public static final int UPGRADE_SLOT = 2;

    public TileElectricFurnace() {
        currentProcessTime = 0;
        inventory = new InventoryTile(3);
        speed = 0;
        capacity = DEFAULT_RF_CAPACITY;
        efficiency = 0;
        io = false;
        energyRF = new EnergyStorage(capacity);
    }

    @Override
    public void update() {
        if (!this.hasWorldObj() || getWorld().isRemote) return;

        if (currentProcessTime > 0 || canSmelt()) {
            if (currentProcessTime == 0) {
                currentProcessTime = 1;
                input = inventory.getStackInSlot(INPUT_SLOT);
                BlockMachine.setState(worldObj, pos, BlockHandler.electricFurnaceActive);
            }
            if (currentProcessTime > 0 && currentProcessTime < findSpeed(TOTAL_PROCESS_TIME, speed)) {
                if (inventory.getStackInSlot(INPUT_SLOT) == null || !inventory.getStackInSlot(INPUT_SLOT).isItemEqual(input)) {
                    currentProcessTime = 0;
                    worldObj.markBlockForUpdate(this.pos);
                    BlockMachine.setState(worldObj, pos, BlockHandler.electricFurnace);
                    return;
                }
                if (energyRF.getEnergyStored() >= findEff(RF_TICK, speed, efficiency) ) {
                    energyRF.modifyEnergyStored(-RF_TICK);
                    ++currentProcessTime;
                }
            }
            if (currentProcessTime >= findSpeed(TOTAL_PROCESS_TIME, speed)) {
                inventory.modifyStack(INPUT_SLOT, -1);
                if (inventory.getStackInSlot(OUTPUT_SLOT) == null)
                    inventory.setStackInSlot(new ItemStack(output.getItem(), output.stackSize > 0 ? output.stackSize : 1), OUTPUT_SLOT);
                else inventory.getStackInSlot(OUTPUT_SLOT).stackSize += (output.stackSize > 0 ? output.stackSize : 1);
                doReset();
            }
            worldObj.markBlockForUpdate(pos);
        }
    }

    private void doReset() {
        currentProcessTime = 0;
        BlockMachine.setState(worldObj, pos, BlockHandler.electricFurnace);
        worldObj.markBlockForUpdate(this.pos);
    }

    private boolean canSmelt() {
        if (inventory.getStackInSlot(INPUT_SLOT) == null) return false;

        output = FurnaceRecipes.instance().getSmeltingResult(inventory.getStackInSlot(INPUT_SLOT));

        return output != null &&
                !(inventory.getStackInSlot(OUTPUT_SLOT) != null &&
                        !inventory.getStackInSlot(OUTPUT_SLOT).isItemEqual(output)) &&
                !(inventory.getStackInSlot(OUTPUT_SLOT) != null &&
                        inventory.getStackInSlot(OUTPUT_SLOT).stackSize >= inventory.getStackInSlot(OUTPUT_SLOT).getMaxStackSize());
    }

    /*******************************************************************************************************************
     ************************************** Energy Functions ***********************************************************
     *******************************************************************************************************************/

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        int actual = energyRF.receiveEnergy(maxReceive, simulate);
        if (actual > 0) this.getWorld().markBlockForUpdate(this.pos);
        return actual;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return energyRF.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return energyRF.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    @Override
    public void spawnActiveParticles(double x, double y, double z) {
        worldObj.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0.01, 0.49, 0.72);
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0, 0);
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
        return index == INPUT_SLOT;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == OUTPUT_SLOT;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case SPEED:
                return speed;
            case EFFICIENCY:
                return efficiency;
            case CAPACITY:
                return capacity;
            case IO:
                return !io ? 0 : 1;
            case 4:
                return currentProcessTime;
            default:
                return 0;
        }

    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case SPEED:
                speed = value;
                break;
            case EFFICIENCY:
                efficiency = value;
                break;
            case CAPACITY:
                capacity = value;
                energyRF.setCapacity(DEFAULT_RF_CAPACITY + capacity * 1000);
                break;
            case IO:
                io = value != 0;
                break;
            case 4:
                currentProcessTime = value;
                break;
        }
        worldObj.markBlockForUpdate(pos);
    }

    @Override
    public int getFieldCount() {
        return 5;
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
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energyRF.readFromNBT(tag);
        inventory.readFromNBT(tag, this, ":main");
        NBTTagList itemsTag = tag.getTagList("Stacks", 10);
        for (int i = 0; i < itemsTag.tagCount(); i++)
        {
            NBTTagCompound nbtTagCompound1 = itemsTag.getCompoundTagAt(i);
            NBTBase nbt = nbtTagCompound1.getTag("Stack");
            int j;
            if ((nbt instanceof NBTTagByte)) {
                j = nbtTagCompound1.getByte("Stack") & 0xFF;
            } else {
                j = nbtTagCompound1.getShort("Stack");
            }
            switch (j) {
                case 0:
                    input = ItemStack.loadItemStackFromNBT(nbtTagCompound1);
                    break;
                case 1:
                    output = ItemStack.loadItemStackFromNBT(nbtTagCompound1);
                    break;
            }
        }
        currentProcessTime = tag.getInteger("CurrentProcessTime");
        speed = tag.getInteger("Speed");
        efficiency = tag.getInteger("Efficiency");
        capacity = tag.getInteger("Capacity");
        io = tag.getBoolean("AutoOutput");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        energyRF.writeToNBT(tag);
        inventory.writeToNBT(tag, ":main");
        NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < 2; i++) {
            NBTTagCompound nbtTagCompound1 = new NBTTagCompound();
            nbtTagCompound1.setShort("Stack", (short)i);
            switch (i)
            {
                case 0:
                    if (input != null)
                        input.writeToNBT(nbtTagCompound1);
                    break;
                case 1:
                    if (output != null)
                        output.writeToNBT(nbtTagCompound1);
                    break;
            }
            nbtTagList.appendTag(nbtTagCompound1);
        }
        tag.setTag("Stacks", nbtTagList);
        tag.setInteger("CurrentProcessTime", currentProcessTime);
        tag.setInteger("Speed", speed);
        tag.setInteger("Efficiency", efficiency);
        tag.setInteger("Capacity", capacity);
        tag.setBoolean("AutoOutput", io);
    }
}
