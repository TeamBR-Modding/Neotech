package com.dyonovan.jatm.common.tileentity.machine;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import com.dyonovan.jatm.common.blocks.BlockBakeable;
import com.dyonovan.jatm.common.blocks.IExpellable;
import com.dyonovan.jatm.common.tileentity.BaseMachine;
import com.dyonovan.jatm.common.tileentity.InventoryTile;
import com.dyonovan.jatm.helpers.inventory.InventoryHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.List;

public class TileElectricMiner extends BaseMachine implements IExpellable, IUpdatePlayerListBox, IEnergyReceiver {

    public static final int INPUT_SLOT = 0;

    public static final int RF_TICK = 250;
    public static final int DEFAULT_SIZE = 9;
    public static final int DEFAULT_SPEED = 5;

    private int currentX, currentY, currentZ, tickWait;
    private boolean isRunning, isWorking;

    public EnergyStorage energyRF;

    public TileElectricMiner() {
        energyRF = new EnergyStorage(10000);
        inventory = new InventoryTile(1);
        currentX = 0;
        currentY = 0;
        currentZ = 0;
        tickWait = 0;
        isRunning = false;
        isWorking = false;
    }

    @Override
    public void update() {
        if (isWorking) return;
        if (!isRunning) {
            EnumFacing rear = ((EnumFacing) getWorld().getBlockState(this.pos).getValue(BlockBakeable.PROPERTY_FACING)).getOpposite();
            if (rear.getAxis() == EnumFacing.Axis.X && rear.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) {
                currentX = this.pos.getX() + 1;
                currentZ = this.pos.getZ();
            } else if (rear.getAxis() == EnumFacing.Axis.X && rear.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE) {
                currentX = this.pos.getX() + DEFAULT_SIZE + 1;
                currentZ = this.pos.getZ();
            } else if (rear.getAxis() == EnumFacing.Axis.Z && rear.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) {
                currentX = this.pos.getZ() + 1;
                currentZ = this.pos.getX();
            } else if (rear.getAxis() == EnumFacing.Axis.Z && rear.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE) {
                currentX = this.pos.getZ() + DEFAULT_SIZE + 1;
                currentZ = this.pos.getX();
            }
            currentY -= 1;

            System.out.println("Currently Trying " + new BlockPos(currentX, currentY, currentZ).toString());

            /*currentX = this.pos.getX() - DEFAULT_SIZE / 2;
            currentY = this.pos.getY() - 1;
            currentZ = this.pos.getZ() - DEFAULT_SIZE / 2;*/
            isRunning = true;
        }
        if (tickWait < DEFAULT_SPEED) {
            ++tickWait;
            return;
        }


        if (energyRF.getEnergyStored() < RF_TICK) return;//todo reduce energy
        if (!(worldObj.getTileEntity(pos.up()) instanceof IInventory)) return;
        BlockPos newBlock = new BlockPos(currentX, currentY, currentZ);
        isWorking = true;
        IInventory storage = (IInventory) worldObj.getTileEntity(pos.up());
        Block currentBlock = worldObj.getBlockState(newBlock).getBlock();
        //System.out.println("Currently Trying " + newBlock.toString() + "of type " + currentBlock.getLocalizedName());
        if (currentBlock == null || currentBlock == Blocks.air || currentBlock == Blocks.bedrock) {
            moveNextPos();
            return;
        }

        List<ItemStack> dropList =  currentBlock.getDrops(worldObj, newBlock, worldObj.getBlockState(newBlock), 0);
        //TODO deal with chests, etc. Placing Item from inv in place of block
        for (ItemStack minedItem : dropList) {
            do {
                int actual = InventoryHelper.moveItemInto(minedItem, storage, -1, minedItem.stackSize, EnumFacing.UP, true, true);
                minedItem.stackSize -= actual;
            } while (minedItem.stackSize > 0);

            worldObj.destroyBlock(newBlock, false);
            moveNextPos();
        }
    }



    private void moveNextPos() {
        ++currentX;
        if (currentX >= this.pos.getX() - (DEFAULT_SIZE / 2) + DEFAULT_SIZE) {
            ++currentZ;
            currentX = this.pos.getX() - DEFAULT_SIZE / 2;
            if (currentZ >= this.pos.getZ() - (DEFAULT_SIZE / 2) + DEFAULT_SIZE) {
                --currentY;
                currentZ = this.pos.getZ() - DEFAULT_SIZE / 2;
                if (currentY == 0) {
                    isRunning = false;
                }
            }
        }
        tickWait = 0;
        isWorking = false;
    }


    /*******************************************************************************************************************
     ************************************** Energy Functions ***********************************************************
     *******************************************************************************************************************/

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return energyRF.receiveEnergy(maxReceive, simulate);
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

    /*******************************************************************************************************************
     ************************************** Inventory Functions ********************************************************
     *******************************************************************************************************************/

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[] {0};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return index == 0;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) {
        switch (id) {
            default:
                return 0;
        }

    }

    @Override
    public void setField(int id, int value) {
        switch (id) {

        }
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            inventory.setStackInSlot(null, i);
        }
    }

    @Override
    public void spawnActiveParticles(double x, double y, double z) {

    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energyRF.readFromNBT(tag);
        inventory.readFromNBT(tag, this);
        currentX = tag.getInteger("CurrentX");
        currentY = tag.getInteger("CurrentY");
        currentZ = tag.getInteger("CurrentZ");
        tickWait = tag.getInteger("TickWait");
        isRunning = tag.getBoolean("IsRunning");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        energyRF.writeToNBT(tag);
        inventory.writeToNBT(tag);
        tag.setInteger("CurrentX", currentX);
        tag.setInteger("CurrentY", currentY);
        tag.setInteger("CurrentZ", currentZ);
        tag.setInteger("TickWait", tickWait);
        tag.setBoolean("IsRunning", isRunning);
    }
}
