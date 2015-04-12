package com.dyonovan.neotech.common.tileentity.machine;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import com.dyonovan.neotech.common.blocks.BlockBakeable;
import com.dyonovan.neotech.common.blocks.IExpellable;
import com.dyonovan.neotech.common.tileentity.BaseMachine;
import com.dyonovan.neotech.common.tileentity.InventoryTile;
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
import net.minecraft.world.ILockableContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileElectricMiner extends BaseMachine implements IExpellable, IUpdatePlayerListBox, IEnergyReceiver {

    public static final int RF_TICK = 250;
    public static final int DEFAULT_SIZE = 9;
    public static final int DEFAULT_SPEED = 40;
    public static final int DEFAULT_RF_CAPACITY = 10000;

    public static final int BTN_SCAN = 0;
    public static final int BTN_START = 1;
    public static final int BTN_STOP = 2;

    public static final int UPGRADE_SLOT = 0;

    private int tickWait;
    public int areaSize;
    public int numBlock;
    public boolean isRunning;
    private boolean isWorking;
    private ArrayList<BlockPos> miningArea;
    private BlockPos currentPos, start, finish;

    public EnergyStorage energyRF;

    public TileElectricMiner() {
        inventory = new InventoryTile(1);
        speed = 0;
        capacity = 0;
        efficiency = 0;
        energyRF = new EnergyStorage(DEFAULT_RF_CAPACITY);
        tickWait = 0;
        numBlock = 0;
        areaSize = 0;
        minerSize = 0;
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

            if (tickWait < findSpeed(DEFAULT_SPEED, speed)) {
                ++tickWait;
                //worldObj.markBlockForUpdate(pos);
                return;
            }

            if (energyRF.getEnergyStored() < findEff(RF_TICK, speed, efficiency, silkTouch, minerSize)) return;
            energyRF.modifyEnergyStored(-findEff(RF_TICK, speed, efficiency, silkTouch, minerSize));
            if (!(worldObj.getTileEntity(pos.up()) instanceof IInventory)) {
                isRunning = isWorking = false;
                return;
            }
            isWorking = true;

            IInventory storage = (IInventory) worldObj.getTileEntity(pos.up());

            Block currentBlock = worldObj.getBlockState(currentPos).getBlock();

            if (currentBlock == null || currentBlock == Blocks.air || currentBlock == Blocks.bedrock) {
                moveNextPos();
                worldObj.markBlockForUpdate(pos);
                return;
            }
            if (worldObj.getTileEntity(currentPos) instanceof ILockableContainer) {
                IInventory chest = (IInventory) worldObj.getTileEntity(currentPos);
                for (int i = 0; i < chest.getSizeInventory(); i++) {
                    if (chest.getStackInSlot(i) == null) continue;
                    int stacksize = chest.getStackInSlot(i).stackSize;
                    stacksize -= InventoryHelper.moveStack(storage, chest.getStackInSlot(i), EnumFacing.UP);
                    if (stacksize > 0) {
                        doStop();
                        return;
                    } else {
                        chest.setInventorySlotContents(i, null);
                    }
                }
            } else {
                List<ItemStack> dropList = currentBlock.getDrops(worldObj, currentPos, worldObj.getBlockState(currentPos), 0);
                for (ItemStack minedItem : dropList) {
                    int stacksize = minedItem.stackSize;
                    stacksize -= InventoryHelper.moveStack(storage, minedItem, EnumFacing.UP);
                    if (stacksize > 0) {
                        worldObj.destroyBlock(currentPos, false);
                        moveNextPos();
                        doStop();
                        return;
                    }
                }
            }
            worldObj.destroyBlock(currentPos, false);
            moveNextPos();
        }
    }

    private void doStop() {
        isRunning = false;
        setRunning(false);
    }

    public void setRunning(boolean state) {
        /*NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        if (state)
            BlockMachine.setState(worldObj, pos, BlockHandler.electricMinerActive);
        else
            BlockMachine.setState(worldObj, pos, BlockHandler.electricMiner);
        worldObj.markBlockForUpdate(pos);
        readFromNBT(tag);*/
        isRunning = state;
    }

    public void setArea() {
        EnumFacing rear = ((EnumFacing) getWorld().getBlockState(this.pos).getValue(BlockBakeable.PROPERTY_FACING)).getOpposite();
        start = getPos().offset(rear, 1);
        start = minerSize == 0 ? start.offset(rear.rotateYCCW(), DEFAULT_SIZE / 2) :
                start.offset(rear.rotateYCCW(), (DEFAULT_SIZE * (minerSize * 3)) / 2);
        start = start.offset(EnumFacing.DOWN, pos.getY() - 1);

        finish = minerSize == 0 ? getPos().offset(rear, DEFAULT_SIZE) :
                getPos().offset(rear, DEFAULT_SIZE * (minerSize * 3));
        finish = minerSize == 0 ? finish.offset(rear.rotateY(), DEFAULT_SIZE / 2) :
                finish.offset(rear.rotateY(), (DEFAULT_SIZE * (minerSize * 3)) / 2);
        finish = finish.offset(EnumFacing.DOWN);

        //noinspection unchecked
        miningArea = Lists.newArrayList(BlockPos.getAllInBox(start, finish));
        Collections.sort(miningArea, Collections.reverseOrder());
        areaSize = miningArea.size();
        currentPos = miningArea.get(0);
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

    /*******************************************************************************************************************
     ******************************************* Tile Functions ********************************************************
     *******************************************************************************************************************/

    @Override
    public int getField(int id) {
        switch (id) {
            case SPEED:
                return speed;
            case EFFICIENCY:
                return efficiency;
            case CAPACITY:
                return capacity;
            case SILKTOUCH:
                return !silkTouch ? 0 : 1;
            case SIZE:
                return minerSize;
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
            case SIZE:
                minerSize = value;
                break;
            case SILKTOUCH:
                silkTouch = value != 0;
                break;
        }
        worldObj.markBlockForUpdate(pos);
    }

    @Override
    public int getFieldCount() { return 5; }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        capacity = tag.getInteger("Capacity");
        energyRF.setCapacity(DEFAULT_RF_CAPACITY + capacity * 1000);
        energyRF.readFromNBT(tag);
        inventory.readFromNBT(tag, this, ":main");
        speed = tag.getInteger("Speed");
        efficiency = tag.getInteger("Efficiency");
        tickWait = tag.getInteger("TickWait");
        areaSize = tag.getInteger("AreaSize");
        numBlock = tag.getInteger("NumBlock");
        minerSize = tag.getInteger("MinerSize");
        silkTouch = tag.getBoolean("SilkTouch");
        currentPos = new BlockPos(tag.getInteger("CurrentX"), tag.getInteger("CurrentY"), tag.getInteger("CurrentZ"));
        //noinspection unchecked
        start = new BlockPos(tag.getInteger("StartX"), tag.getInteger("StartY"), tag.getInteger("StartZ"));
        finish =  new BlockPos(tag.getInteger("FinishX"), tag.getInteger("FinishY"), tag.getInteger("FinishZ"));
        //noinspection unchecked
        miningArea = Lists.newArrayList(BlockPos.getAllInBox(start, finish));
        Collections.sort(miningArea, Collections.reverseOrder());
        isRunning = tag.getBoolean("IsRunning");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        energyRF.writeToNBT(tag);
        inventory.writeToNBT(tag, ":main");
        tag.setInteger("Speed", speed);
        tag.setInteger("Efficiency", efficiency);
        tag.setInteger("Capacity", capacity);
        tag.setInteger("TickWait", tickWait);
        tag.setInteger("AreaSize", areaSize);
        tag.setInteger("NumBlock", numBlock);
        tag.setInteger("MinerSize", minerSize);
        tag.setBoolean("SilkTouch", silkTouch);
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
