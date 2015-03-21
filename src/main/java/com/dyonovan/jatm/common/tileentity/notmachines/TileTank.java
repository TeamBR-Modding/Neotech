package com.dyonovan.jatm.common.tileentity.notmachines;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.*;

public class TileTank extends TileEntity implements IFluidHandler, IUpdatePlayerListBox {
    public FluidTank tank;

    public TileTank() {
        tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 8);
    }

    public float getFluidLevelScaled() {
        return Math.min(15.99F, 16 * tank.getFluidAmount() / tank.getCapacity());
    }

    public Fluid getCurrentFluid() {
        return tank.getFluid() != null ? tank.getFluid().getFluid() : null;
    }


    public int getBrightness () {
        if (tank.getFluid() != null) {
            return (tank.getFluid().getFluid().getLuminosity() * tank.getFluidAmount()) / tank.getCapacity();
        }
        return 0;
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        worldObj.markBlockForUpdate(pos);
        return canFill(from, resource.getFluid()) ? tank.fill(resource, doFill) : 0;
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        return drain(from, resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        FluidStack fluidAmount = tank.drain(maxDrain, false);
        if(fluidAmount != null && doDrain) {
            tank.drain(maxDrain, true);
            worldObj.markBlockForUpdate(pos);
        }
        return fluidAmount;
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return tank.getFluid() == null || tank.getFluid().getFluid() == fluid;
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        return new FluidTankInfo[] { tank.getInfo() };
    }

    @Override
    public void update() {
        if(tank.getFluid() != null && worldObj.getWorldTime() % 40 == 0) {
            if(worldObj.getTileEntity(pos.offset(EnumFacing.DOWN)) instanceof IFluidHandler) {
                IFluidHandler otherTank = (IFluidHandler)worldObj.getTileEntity(pos.offset(EnumFacing.DOWN));
                if(otherTank.canFill(EnumFacing.UP, tank.getFluid().getFluid())) {
                    tank.drain(otherTank.fill(EnumFacing.UP, new FluidStack(tank.getFluid().getFluid(), 100), true), true);
                }
            }
        }
    }

    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        readCustomNBT(tags);
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        writeCustomNBT(tags);
    }

    public void readCustomNBT (NBTTagCompound tags) {
        if (tags.getBoolean("hasFluid")) {
            if (tags.getInteger("itemID") != 0) {
                tank.setFluid(new FluidStack(tags.getInteger("itemID"), tags.getInteger("amount")));
            }
            else {
                tank.setFluid(FluidRegistry.getFluidStack(tags.getString("fluidName"), tags.getInteger("amount")));
            }
        }
        else
            tank.setFluid(null);
    }

    public void writeCustomNBT (NBTTagCompound tags)
    {
        FluidStack liquid = tank.getFluid();
        tags.setBoolean("hasFluid", liquid != null);
        if (liquid != null)
        {
            tags.setString("fluidName", liquid.getFluid().getName());
            tags.setInteger("amount", liquid.amount);
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new S35PacketUpdateTileEntity(this.pos, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
        worldObj.markBlockForUpdate(pos);
    }
}
