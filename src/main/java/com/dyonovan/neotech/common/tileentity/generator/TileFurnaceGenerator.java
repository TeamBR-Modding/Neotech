package com.dyonovan.neotech.common.tileentity.generator;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import com.dyonovan.neotech.common.blocks.IExpellable;
import com.dyonovan.neotech.common.tileentity.BaseMachine;
import com.dyonovan.neotech.common.tileentity.InventoryTile;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.fluids.FluidContainerRegistry.drainFluidContainer;
import static net.minecraftforge.fluids.FluidContainerRegistry.isFilledContainer;

//TODO : Dont consume containers (buckets)
public class TileFurnaceGenerator extends BaseMachine implements IUpdatePlayerListBox, IEnergyProvider, IExpellable {

    public int currentBurnTime;
    public int totalBurnTime;
    public EnergyStorage energyRF;

    /**
     * Energy Creation per Tick
     */
    private static final int RF_TICK = 20;

    public static final int FUEL_SLOT = 0;

    public TileFurnaceGenerator() {
        energyRF = new EnergyStorage(10000);
        currentBurnTime = 0;
        totalBurnTime = 0;
        inventory = new InventoryTile(1);
    }

    @Override
    public void update() {
        if (!this.hasWorldObj()) return;
        World world = this.getWorld();
        if (world.isRemote) return;

        transferEnergy();

        if (currentBurnTime > 0 || (energyRF.getEnergyStored() < energyRF.getMaxEnergyStored() && inventory != null)) {
            if (currentBurnTime == 0) {
                totalBurnTime = getFuelValue(inventory.getStackInSlot(FUEL_SLOT));
                if (totalBurnTime == 0) return;
                currentBurnTime = 1;
                if (isFilledContainer(inventory.getStackInSlot(FUEL_SLOT)))
                        inventory.setStackInSlot(drainFluidContainer(inventory.getStackInSlot(FUEL_SLOT)),FUEL_SLOT);
                else if (inventory.getStackInSlot(FUEL_SLOT).stackSize == 1) inventory.setStackInSlot(null, FUEL_SLOT);
                else inventory.getStackInSlot(FUEL_SLOT).stackSize -= 1;
            }
            if (currentBurnTime > 0 && currentBurnTime < totalBurnTime) {
                energyRF.modifyEnergyStored(RF_TICK);
                currentBurnTime += 1;
            }
            if (currentBurnTime > 0 && currentBurnTime >= totalBurnTime) {
                currentBurnTime = 0;
                totalBurnTime = 0;
            }
            world.markBlockForUpdate(this.pos);
        }
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

    private int getFuelValue(ItemStack itemStack) {
        if (itemStack == null) return 0;

        Item item = itemStack.getItem();
        if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.air) {
            Block block = Block.getBlockFromItem(item);
            if (block == Blocks.wooden_slab)
            {
                return 150;
            }

            if (block.getMaterial() == Material.wood)
            {
                return 300;
            }

            if (block == Blocks.coal_block)
            {
                return 16000;
            }
        }
        if (item instanceof ItemTool && ((ItemTool)item).getToolMaterialName().equals("WOOD")) return 200;
        if (item instanceof ItemSword && ((ItemSword)item).getToolMaterialName().equals("WOOD")) return 200;
        if (item instanceof ItemHoe && ((ItemHoe)item).getMaterialName().equals("WOOD")) return 200;
        if (item == Items.stick) return 100;
        if (item == Items.coal) return 1600;
        if (item == Items.lava_bucket) return 20000;
        if (item == Item.getItemFromBlock(Blocks.sapling)) return 100;
        if (item == Items.blaze_rod) return 2400;
        return net.minecraftforge.fml.common.registry.GameRegistry.getFuelValue(itemStack);
    }

    /*******************************************************************************************************************
     ************************************** Energy Functions ***********************************************************
     *******************************************************************************************************************/

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return energyRF.extractEnergy(maxExtract, simulate);
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
     **************************************** Tile Functions ***********************************************************
     *******************************************************************************************************************/

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
        tag.setInteger("CurrentBurnTime", currentBurnTime);
        tag.setInteger("TotalBurnTime", totalBurnTime);
    }

    @Override
    public void spawnActiveParticles(double x, double y, double z) {
        List<EnumParticleTypes> particles = new ArrayList<>();
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
        return 1;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return getFuelValue(stack) > 0;
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
        inventory.setStackInSlot(null, 0);
    }

    /*******************************************************************************************************************
     **************************************** Tile Functions ***********************************************************
     *******************************************************************************************************************/

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new S35PacketUpdateTileEntity(this.pos, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }
}
