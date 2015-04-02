package com.dyonovan.neotech.common.tileentity.machine;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import com.dyonovan.neotech.common.blocks.BlockBakeable;
import com.dyonovan.neotech.common.blocks.IExpellable;
import com.dyonovan.neotech.common.tileentity.BaseMachine;
import com.dyonovan.neotech.helpers.inventory.InventoryHelper;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.List;

public class TileElectricMiner extends BaseMachine implements IExpellable, IUpdatePlayerListBox, IEnergyReceiver {

    public static final int RF_TICK = 250;
    public static final int DEFAULT_SIZE = 9;
    public static final int DEFAULT_SPEED = 20;

    public static final int BTN_SCAN = 0;
    public static final int BTN_START = 1;
    public static final int BTN_STOP = 2;

    private int tickWait;
    public int areaSize;
    public int numBlock;
    public boolean isRunning;
    private boolean isWorking;
    private ArrayList<BlockPos> miningArea;
    private BlockPos currentPos, start, finish;

    public EnergyStorage energyRF;

    public TileElectricMiner() {
        energyRF = new EnergyStorage(10000);
        tickWait = 0;
        numBlock = 0;
        areaSize = 0;
        isRunning = false;
        isWorking = false;
        currentPos = new BlockPos(0, 0, 0);
        start = new BlockPos(0, 0, 0);
        finish = new BlockPos(0, 0, 0);
    }

    @Override
    public void update() {
        if(worldObj != null && !worldObj.isRemote) {

            if (miningArea == null || !isRunning || isWorking) return;

            if (tickWait < DEFAULT_SPEED) {
                ++tickWait;
                worldObj.markBlockForUpdate(pos);
                return;
            }

            if (energyRF.getEnergyStored() < RF_TICK) return;
            energyRF.modifyEnergyStored(RF_TICK);
            if (!(worldObj.getTileEntity(pos.up()) instanceof IInventory)) return;
            isWorking = true;

            IInventory storage = (IInventory) worldObj.getTileEntity(pos.up());

            Block currentBlock = worldObj.getBlockState(currentPos).getBlock();

            if (currentBlock == null || currentBlock == Blocks.air || currentBlock == Blocks.bedrock) {
                moveNextPos();
                return;
            }

            List<ItemStack> dropList = currentBlock.getDrops(worldObj, currentPos, worldObj.getBlockState(currentPos), 0);
            //TODO deal with chests, etc. Placing Item from inv in place of block
            for (ItemStack minedItem : dropList) {
                int stacksize = minedItem.stackSize;
                stacksize -= InventoryHelper.moveStack(storage, minedItem, EnumFacing.UP);
                if (stacksize > 0) {
                    worldObj.destroyBlock(currentPos, false);
                    moveNextPos();
                    isRunning = false;
                }
            }
            worldObj.destroyBlock(currentPos, false);
            moveNextPos();
        }
    }

    public void setArea() {
        EnumFacing rear = ((EnumFacing) getWorld().getBlockState(this.pos).getValue(BlockBakeable.PROPERTY_FACING)).getOpposite();
        start = getPos().offset(rear, 1);
        start = start.offset(rear.rotateYCCW(), DEFAULT_SIZE / 2);
        start = start.offset(EnumFacing.DOWN, pos.getY() - 1);

        finish = getPos().offset(rear, DEFAULT_SIZE);
        finish = finish.offset(rear.rotateY(), (DEFAULT_SIZE / 2));
        finish = finish.offset(EnumFacing.DOWN);

        //noinspection unchecked
        miningArea = Lists.newArrayList(BlockPos.getAllInBox(start, finish));
        areaSize = miningArea.size();
        currentPos = start;
        worldObj.markBlockForUpdate(pos);
    }

    private void moveNextPos() {
        ++numBlock;
        if (numBlock < areaSize) {
            currentPos = miningArea.get(numBlock);
            tickWait = 0;
            isWorking = false;
        } else isRunning = false;
        worldObj.markBlockForUpdate(pos);
    }

    /*******************************************************************************************************************
     ************************************** Energy Functions ***********************************************************
     *******************************************************************************************************************/

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        int actual = energyRF.receiveEnergy(maxReceive, simulate);
        worldObj.markBlockForUpdate(pos);
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

    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energyRF.readFromNBT(tag);
        tickWait = tag.getInteger("TickWait");
        areaSize = tag.getInteger("AreaSize");
        numBlock = tag.getInteger("NumBlock");
        currentPos = new BlockPos(tag.getInteger("CurrentX"), tag.getInteger("CurrentY"), tag.getInteger("CurrentZ"));
        //noinspection unchecked
        start = new BlockPos(tag.getInteger("StartX"), tag.getInteger("StartY"), tag.getInteger("StartZ"));
        finish =  new BlockPos(tag.getInteger("FinishX"), tag.getInteger("FinishY"), tag.getInteger("FinishZ"));
        //noinspection unchecked
        miningArea = Lists.newArrayList(BlockPos.getAllInBox(start, finish));
        isRunning = tag.getBoolean("IsRunning");
        //worldObj.markBlockForUpdate(pos);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        energyRF.writeToNBT(tag);
        tag.setInteger("TickWait", tickWait);
        tag.setInteger("AreaSize", areaSize);
        tag.setInteger("NumBlock", numBlock);
        tag.setInteger("CurrentX", currentPos.getX());
        tag.setInteger("CurrentY", currentPos.getY());
        tag.setInteger("CurrentZ", currentPos.getZ());
        tag.setInteger("StartX", start.getX());
        tag.setInteger("StartY", start.getY());
        tag.setInteger("StartZ", start.getZ());
        tag.setInteger("FinishX", finish.getX());
        tag.setInteger("FinishY", finish.getY());
        tag.setInteger("FinishZ", finish.getZ());
        tag.setBoolean("IsRunning", isRunning);
    }
}
