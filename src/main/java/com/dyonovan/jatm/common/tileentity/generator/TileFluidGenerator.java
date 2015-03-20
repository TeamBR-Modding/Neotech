package com.dyonovan.jatm.common.tileentity.generator;

import cofh.api.energy.EnergyStorage;
import com.dyonovan.jatm.common.tileentity.BaseMachine;
import com.dyonovan.jatm.common.tileentity.InventoryTile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;

import static net.minecraftforge.fluids.FluidContainerRegistry.*;


public class TileFluidGenerator extends BaseMachine implements IUpdatePlayerListBox, IFluidHandler {

    private enum validFuels {
        lava;

        public static boolean contains(String fluid) {
            for (validFuels f : validFuels.values()) {
                if (f.name().equals(fluid)) return true;
            }
            return false;
        }
    }

    public FluidTank fluidTank;
    public int currentBurnTime;

    /**
     * Energy Creation per Tick
     */
    private static final int RF_TICK = 80;
    public static final int TOTAL_BURN_TIME = 20000;
    public static final int TANK_CAPACITY = BUCKET_VOLUME * 10;
    public static final int BUCKET_IN = 0;
    public static final int BUCKET_OUT = 1;

    public TileFluidGenerator() {
        energyRF = new EnergyStorage(10000, 80);
        inventory = new InventoryTile(2);
        fluidTank = new FluidTank(TANK_CAPACITY);
        currentBurnTime = 0;
    }

    @Override
    public void update() {
        if (!this.hasWorldObj()) return;
        World world = this.getWorld();
        if (world.isRemote) return;

        transferFluid();

        if (currentBurnTime > 0 || canWork()) {
            if (currentBurnTime == 0) {

            }
            if (currentBurnTime > 0 && currentBurnTime < TOTAL_BURN_TIME) {

            }
            if (currentBurnTime >= TOTAL_BURN_TIME) {

            }
        }
    }

    private boolean canWork() {
        return false;
    }

    private void transferFluid() {
        if (inventory.getStackInSlot(BUCKET_IN) == null ||
                !isFilledContainer(inventory.getStackInSlot(BUCKET_IN))) return;

        FluidStack fluidIn = getFluidForFilledItem(inventory.getStackInSlot(BUCKET_IN));
        String temp = fluidIn.getFluid().getName();
        if (!(validFuels.contains(fluidIn.getFluid().getName())) ||
                (fluidTank.getFluid() != null &&
                fluidTank.getFluid().getFluid() != fluidIn.getFluid())) return;
        String string = "test";





    }



    /*******************************************************************************************************************
     *************************************** Fluid Functions ***********************************************************
     *******************************************************************************************************************/

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
            int actual =  fluidTank.fill(resource, doFill);
            this.getWorld().markBlockForUpdate(this.pos);
            return actual;
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(fluidTank.getFluid()))
        {
            return null;
        }
        FluidStack actual = fluidTank.drain(resource.amount, doDrain);
        this.getWorld().markBlockForUpdate(this.pos);
        return actual;
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        FluidStack actual = fluidTank.drain(maxDrain, doDrain);
        this.getWorld().markBlockForUpdate(this.pos);
        return actual;
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return true;
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        return new FluidTankInfo[] { fluidTank.getInfo() };
    }

    /*******************************************************************************************************************
     ************************************** Energy Functions ***********************************************************
     *******************************************************************************************************************/

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        int actual = energyRF.extractEnergy(maxExtract, simulate);
        if (actual > 0) this.getWorld().markBlockForUpdate(this.pos);
        return actual;
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
        return index == BUCKET_IN;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == BUCKET_OUT;
    }

    @Override
    public int getSizeInventory() {
        return 2;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return isFilledContainer(inventory.getStackInSlot(BUCKET_IN));
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0:
                return currentBurnTime;
            default:
                return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0:
                currentBurnTime = value;
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
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energyRF.readFromNBT(tag);
        inventory.readFromNBT(tag, this);
        fluidTank.readFromNBT(tag);
        currentBurnTime = tag.getInteger("CurrentBurnTime");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        energyRF.writeToNBT(tag);
        inventory.writeToNBT(tag);
        fluidTank.writeToNBT(tag);
        tag.setInteger("CurrentBurnTime", currentBurnTime);
    }
}
