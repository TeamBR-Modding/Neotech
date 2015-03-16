package com.dyonovan.jatm.common.tileentity.generator;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileGenerator extends TileEntity implements IEnergyHandler, IUpdatePlayerListBox, ISidedInventory {

    public EnergyStorage energyRF;
    public ItemStack[] inventory;
    private int currentBurnTime;
    private int totalBurnTime;

    public int test;

    /**
     * Energy Use per Tick
     */
    private static final int RF_TICK = 20;

    public static final int FUEL_SLOT = 0;

    public TileGenerator() {
        energyRF = new EnergyStorage(10000, 20);
        inventory = new ItemStack[1];
        currentBurnTime = 0;
        totalBurnTime = 0;
        test = 10;
    }

    public void generatePower() {
        if (currentBurnTime > 0 || (canRun() && inventory[0] != null)) {
            if (currentBurnTime == 0) {
                totalBurnTime = getFuelValue(inventory[0]);
                if (totalBurnTime == 0) return;
                currentBurnTime = 1;
                if (inventory[0].stackSize == 1) inventory[0] = null;
                else inventory[0].stackSize -= 1;
                worldObj.markBlockForUpdate(this.pos);
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
        return energyRF.getEnergyStored() < energyRF.getMaxEnergyStored();
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
        currentBurnTime = tag.getInteger("CurrentBurnTime");
        totalBurnTime = tag.getInteger("TotalBurnTime");

        NBTTagList itemsTag = tag.getTagList("Items", 10);
        for (int i = 0; i < itemsTag.tagCount(); i++)
        {
            NBTTagCompound nbtTagCompound1 = itemsTag.getCompoundTagAt(i);
            NBTBase nbt = nbtTagCompound1.getTag("Slot");
            int j;
            if ((nbt instanceof NBTTagByte)) {
                j = nbtTagCompound1.getByte("Slot") & 0xFF;
            } else {
                j = nbtTagCompound1.getShort("Slot");
            }
            if ((j >= 0) && (j < this.inventory.length)) {
                this.inventory[j] = ItemStack.loadItemStackFromNBT(nbtTagCompound1);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        energyRF.writeToNBT(tag);
        tag.setInteger("CurrentBurnTime", currentBurnTime);
        tag.setInteger("TotalBurnTime", totalBurnTime);

        NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < this.inventory.length; i++) {
            if (this.inventory[i] != null)
            {
                NBTTagCompound nbtTagCompound1 = new NBTTagCompound();
                nbtTagCompound1.setShort("Slot", (short)i);
                this.inventory[i].writeToNBT(nbtTagCompound1);
                nbtTagList.appendTag(nbtTagCompound1);
            }
        }
        tag.setTag("Items", nbtTagList);
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
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return inventory[index];
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
        inventory[index] = stack;
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
        for (int i = 0; i < inventory.length; i++) {
            inventory[i] = null;
        }
    }

    @Override
    public String getCommandSenderName() {
        return "container.modernalchemy:generator.name";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public IChatComponent getDisplayName() {
        return new ChatComponentTranslation(this.getCommandSenderName());
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
    }

}
