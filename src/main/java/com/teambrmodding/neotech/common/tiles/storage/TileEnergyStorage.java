package com.teambrmodding.neotech.common.tiles.storage;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyReceiver;
import com.teambr.bookshelf.common.container.IInventoryCallback;
import com.teambr.bookshelf.common.tiles.EnergyHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/16/2017
 */
public class TileEnergyStorage extends EnergyHandler implements IItemHandlerModifiable {
    // Class Variables
    public static final int DRAIN_SLOT = 0;
    public static final int FILL_SLOT  = 1;
    public static final int BASE_STORAGE = 32000;

    /*******************************************************************************************************************
     * Inventory Variables                                                                                             *
     *******************************************************************************************************************/

    // A list to hold all callback objects
    private List<IInventoryCallback> inventoryCallbacks = new ArrayList<>();
    // List of Inventory contents
    public Stack<ItemStack> inventoryContents = new Stack<>();

    // NBT Tags
    protected static final String SIZE_INVENTORY_NBT_TAG = "Size:";
    protected static final String SLOT_INVENTORY_NBT_TAG = "Slot:";
    protected static final String ITEMS_NBT_TAG          = "Items:";

    /*******************************************************************************************************************
     * Energy Variables                                                                                                *
     *******************************************************************************************************************/

    // The current tier
    protected int tier;

    public TileEnergyStorage() {}

    /**
     * Second constructor for tiers
     * @param tier The tier
     */
    public TileEnergyStorage(int tier) {
        super();
        this.tier = tier;
        inventoryContents.setSize(2);
    }

    /*******************************************************************************************************************
     * Tile Methods                                                                                                    *
     *******************************************************************************************************************/
    @Override
    protected void onServerTick() {
        super.onServerTick();

        if(inventoryContents.size() != 2)
            inventoryContents.setSize(2);

        for (EnumFacing dir : EnumFacing.values()) {
            if(worldObj.getTileEntity(pos.offset(dir)) != null &&
                    worldObj.getTileEntity(pos.offset(dir)) instanceof IEnergyReceiver) {
                IEnergyReceiver otherEnergy = (IEnergyReceiver) worldObj.getTileEntity(pos.offset(dir));
                int demandedEnergy = otherEnergy.receiveEnergy(dir.getOpposite(), energyStorage.getEnergyStored(), true);
                if(demandedEnergy > 0) {
                    int actual = extractEnergy(dir.getOpposite(), demandedEnergy, false);
                    otherEnergy.receiveEnergy(dir.getOpposite(), actual, false);
                }
            }
        }

        // Drain Item
        if(getStackInSlot(DRAIN_SLOT) != null && getStackInSlot(DRAIN_SLOT).getItem() instanceof IEnergyContainerItem) {
            IEnergyContainerItem energyItem = (IEnergyContainerItem) getStackInSlot(DRAIN_SLOT).getItem();
            int drainAmount = receiveEnergy(energyItem.extractEnergy(getStackInSlot(DRAIN_SLOT), energyStorage.getMaxInsert(), true), true);
            if(drainAmount > 0)
                receiveEnergy(energyItem.extractEnergy(getStackInSlot(DRAIN_SLOT), drainAmount, false), false);
        }

        // Fill Item
        if(getStackInSlot(FILL_SLOT) != null && getStackInSlot(FILL_SLOT).getItem() instanceof IEnergyContainerItem) {
            IEnergyContainerItem energyItem = (IEnergyContainerItem) getStackInSlot(FILL_SLOT).getItem();
            extractEnergy(energyItem.receiveEnergy(getStackInSlot(FILL_SLOT), Math.min(energyStorage.getMaxExtract(), tier == 4 ?
                    energyStorage.getMaxEnergyStored() : energyStorage.getCurrentStored()), false), false);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        // Inventory
        String inventoryName = "";
        // Set the size
        compound.setInteger(SIZE_INVENTORY_NBT_TAG + inventoryName, inventoryContents.size());

        // Write the inventory
        NBTTagList tagList = new NBTTagList();
        for(int i = 0; i < inventoryContents.size(); i++) {
            if(inventoryContents.get(i) != null) {
                NBTTagCompound stackTag = new NBTTagCompound();
                stackTag.setByte(SLOT_INVENTORY_NBT_TAG + inventoryName, (byte) i);
                inventoryContents.get(i).writeToNBT(stackTag);
                tagList.appendTag(stackTag);
            }
        }
        compound.setTag(ITEMS_NBT_TAG + inventoryName, tagList);

        compound.setInteger("Tier", tier);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        // Inventory
        String inventoryName = "";
        // Read Items
        NBTTagList tagList = compound.getTagList(ITEMS_NBT_TAG + inventoryName, 10);
        inventoryContents = new Stack<>();
        if(compound.hasKey(SIZE_INVENTORY_NBT_TAG + inventoryName))
            inventoryContents.setSize(compound.getInteger(SIZE_INVENTORY_NBT_TAG + inventoryName));
        else
            inventoryContents.setSize(2);
        for(int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound stackTag = tagList.getCompoundTagAt(i);
            int slot = stackTag.getByte(SLOT_INVENTORY_NBT_TAG + inventoryName);
            if(slot >= 0 && slot < inventoryContents.size())
                inventoryContents.set(slot, ItemStack.loadItemStackFromNBT(stackTag));
        }

        tier = compound.getInteger("Tier");
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return super.hasCapability(capability, facing) || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) this;
        return super.getCapability(capability, facing);
    }

    /**
     * Used to output the redstone single from this structure
     *
     * Use a range from 0 - 16.
     *
     * 0 Usually means that there is nothing in the tile, so take that for lowest level. Like the generator has no energy while
     * 16 is usually the flip side of that. Output 16 when it is totally full and not less
     *
     * @return int range 0 - 16
     */
    public int getRedstoneOutput() {
        return (energyStorage.getEnergyStored() * 16) / energyStorage.getMaxStored();
    }

    /*******************************************************************************************************************
     * Energy Methods                                                                                                  *
     *******************************************************************************************************************/

    /**
     * Used to define the default size of this energy bank
     *
     * @return The default size of the energy bank
     */
    @Override
    protected int getDefaultEnergyStorageSize() {
        return (int) (BASE_STORAGE * Math.pow(tier, tier));
    }

    /**
     * Is this tile an energy provider
     *
     * @return True to allow energy out
     */
    @Override
    protected boolean isProvider() {
        return true;
    }

    /**
     * Is this tile an energy reciever
     *
     * @return True to accept energy
     */
    @Override
    protected boolean isReceiver() {
        return true;
    }

    /**
     * Remove energy from an IEnergyProvider, internal distribution is left entirely to the IEnergyProvider.
     *
     * @param maxExtract Maximum amount of energy to extract.
     * @param simulate   If TRUE, the extraction will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) extracted.
     */
    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return super.extractEnergy(maxExtract, tier == 4 || simulate);
    }

    /*******************************************************************************************************************
     * InventoryHandler Methods                                                                                        *
     *******************************************************************************************************************/

    /**
     * Add a callback to this inventory
     * @param iInventoryCallback The callback you wish to add
     * @return This object, to enable chaining
     */
    public TileEnergyStorage addCallback(IInventoryCallback iInventoryCallback) {
        inventoryCallbacks.add(iInventoryCallback);
        return this;
    }

    /**
     * Called when the inventory has a change
     *
     * @param slot The slot that changed
     */
    protected void onInventoryChanged(int slot) {
        inventoryCallbacks.forEach((IInventoryCallback callback) -> {
            callback.onInventoryChanged(this, slot);
        });
    }

    /**
     * Used to add just one stack into the end of the inventory
     *
     * @param stack The stack to push
     */
    public void addInventorySlot(ItemStack stack) {
        inventoryContents.push(stack);
    }

    /**
     * Used to push x amount of slots into the inventory
     *
     * @param count How many slots to add
     */
    public void addInventorySlots(int count) {
        for(int i = 0; i < count; i++)
            addInventorySlot(null);
    }

    /**
     * Used to remove the last element of the stack
     *
     * @return The last stack, now popped
     */
    public ItemStack removeInventorySlot() {
        return inventoryContents.pop();
    }

    /**
     * Used to remove a specific amount of items
     *
     * @param count The count of slots to remove
     * @return The array of the stacks that were there
     */
    public ItemStack[] removeInventorySlots(int count) {
        ItemStack[] poppedStacks = new ItemStack[count];
        for(int i = 0; i < count; i++)
            poppedStacks[i] = removeInventorySlot();
        return poppedStacks;
    }

    /**
     * Used to copy from an existing inventory
     *
     * @param inventory The inventory to copy from
     */
    public void copyFrom(IItemHandler inventory) {
        for(int i = 0; i < inventory.getSlots(); i++) {
            if(i < inventoryContents.size()) {
                ItemStack stack = inventory.getStackInSlot(i);
                if(stack != null)
                    inventoryContents.set(i, stack.copy());
                else
                    inventoryContents.set(i, null);
            }
        }
    }

    /**
     * Makes sure this slot is within our range
     * @param slot Which slot
     */
    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= inventoryContents.size())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + inventoryContents.size() + ")");
    }

    /**
     * Gets the stack limit of a stack
     * @param slot The slot
     * @param stack The stack
     * @return Max stack size
     */
    protected int getStackLimit(int slot, ItemStack stack)
    {
        return stack.getMaxStackSize();
    }

    /**
     * Used to define if an item is valid for a slot
     *
     * @param index The slot id
     * @param stack The stack to check
     * @return True if you can put this there
     */
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return stack.getItem() != null &&
                stack.getItem() instanceof IEnergyContainerItem;
    }

    @Override
    public int getSlots() {
        return inventoryContents.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return inventoryContents.get(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack == null || stack.stackSize == 0 || !isItemValidForSlot(slot, stack))
            return null;

        validateSlotIndex(slot);

        ItemStack existing = this.inventoryContents.get(slot);

        int limit = getStackLimit(slot, stack);

        if (existing != null) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.stackSize;
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.stackSize > limit;

        if (!simulate) {
            if (existing == null) {
                this.inventoryContents.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            }
            else {
                existing.stackSize += reachedLimit ? limit : stack.stackSize;
            }
            onInventoryChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize - limit) : null;    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return null;

        validateSlotIndex(slot);

        ItemStack existing = this.inventoryContents.get(slot);

        if (existing == null)
            return null;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.stackSize <= toExtract) {
            if (!simulate) {
                this.inventoryContents.set(slot, null);
                onInventoryChanged(slot);
            }
            return existing;
        }
        else {
            if (!simulate) {
                this.inventoryContents.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.stackSize - toExtract));
                onInventoryChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        validateSlotIndex(slot);
        if (ItemStack.areItemStacksEqual(this.inventoryContents.get(slot), stack))
            return;
        this.inventoryContents.set(slot, stack);
        onInventoryChanged(slot);
    }
}
