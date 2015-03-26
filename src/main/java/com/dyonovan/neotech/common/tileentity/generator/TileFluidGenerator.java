package com.dyonovan.neotech.common.tileentity.generator;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import com.dyonovan.neotech.common.blocks.BlockMachine;
import com.dyonovan.neotech.common.blocks.IExpellable;
import com.dyonovan.neotech.common.tileentity.BaseMachine;
import com.dyonovan.neotech.common.tileentity.InventoryTile;
import com.dyonovan.neotech.handlers.BlockHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;

import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.fluids.FluidContainerRegistry.*;


public class TileFluidGenerator extends BaseMachine implements IUpdatePlayerListBox, IFluidHandler, IEnergyProvider, IExpellable {

    private enum validFuels {
        lava;

        public static boolean contains(String fluid) {
            for (validFuels f : validFuels.values()) {
                if (f.name().equalsIgnoreCase(fluid)) return true;
            }
            return false;
        }
    }

    public FluidTank fluidTank;
    public EnergyStorage energyRF;

    /**
     * Energy Creation per Tick
     */
    private static final int RF_TICK = 80;
    private static final int MB_TICK = 1;
    public static final int TANK_CAPACITY = BUCKET_VOLUME * 10;
    public static final int BUCKET_IN = 0;
    public static final int BUCKET_OUT = 1;

    public TileFluidGenerator() {
        energyRF = new EnergyStorage(10000, RF_TICK);
        inventory = new InventoryTile(2);
        fluidTank = new FluidTank(TANK_CAPACITY);
    }

    @Override
    public void update() {
        if (!this.hasWorldObj()) return;
        World world = this.getWorld();
        if (world.isRemote) return;

        transferEnergy();

        transferFluid();

        if (energyRF.getEnergyStored() + RF_TICK <= energyRF.getMaxEnergyStored() &&
                fluidTank.getFluid() != null && fluidTank.getFluidAmount() >= MB_TICK) {
            energyRF.modifyEnergyStored(RF_TICK);
            fluidTank.drain(MB_TICK, true);
            BlockMachine.setState(worldObj, pos, BlockHandler.fluidGeneratorActive);
            world.markBlockForUpdate(this.pos);
        } else if(worldObj.getBlockState(pos).getBlock() == BlockHandler.fluidGeneratorActive)
            BlockMachine.setState(worldObj, pos, BlockHandler.fluidGenerator);

    }

    private void transferEnergy() {
        List<EnumFacing> availDir = new ArrayList<>();
        if (energyRF.getEnergyStored() > 0) {
            for (EnumFacing dir : EnumFacing.VALUES) {
                TileEntity tile = getWorld().getTileEntity(this.pos.offset(dir));
                if (tile instanceof IEnergyReceiver) availDir.add(dir);
            }
        }
        if (availDir.size() <= 0) return;
        int availRF = Math.min(energyRF.getEnergyStored() / availDir.size() , energyRF.getMaxExtract() / availDir.size());
        for (EnumFacing dir : availDir) {
            TileEntity tile = getWorld().getTileEntity(this.pos.offset(dir));
            energyRF.extractEnergy(((IEnergyReceiver) tile).receiveEnergy(dir.getOpposite(), energyRF.extractEnergy(availRF, true), false), false);
        }
        getWorld().markBlockForUpdate(this.pos);
    }

    private void transferFluid() {
        if (inventory.getStackInSlot(BUCKET_IN) == null ||
                !isFilledContainer(inventory.getStackInSlot(BUCKET_IN))) return;

        FluidStack fluidIn = getFluidForFilledItem(inventory.getStackInSlot(BUCKET_IN));
        if (fluidIn == null) return;

        if (!(validFuels.contains(fluidIn.getFluid().getName()))) return;
        if (fluidTank.getFluid() != null && fluidTank.getFluid().getFluid() != fluidIn.getFluid()) return;
        if (fluidTank.getFluid() != null && fluidTank.getFluid().amount + fluidIn.amount > fluidTank.getCapacity()) return;


        fluidTank.fill(fluidIn, true);

        if (inventory.getStackInSlot(BUCKET_OUT) == null)
            inventory.setStackInSlot(drainFluidContainer(inventory.getStackInSlot(BUCKET_IN)),BUCKET_OUT);
        else inventory.modifyStack(BUCKET_OUT, 1);
        inventory.modifyStack(BUCKET_IN, -1);


        this.getWorld().markBlockForUpdate(this.pos);
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
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        int actual = energyRF.extractEnergy(maxExtract, simulate);
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
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        energyRF.writeToNBT(tag);
        inventory.writeToNBT(tag);
        fluidTank.writeToNBT(tag);
    }
}
