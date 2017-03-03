package com.teambrmodding.neotech.common.tiles.storage;

import com.teambr.bookshelf.common.container.IInventoryCallback;
import com.teambr.bookshelf.common.tiles.EnergyHandler;
import com.teambr.bookshelf.util.EnergyUtils;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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
    private List<IInventoryCallback> callBacks = new ArrayList<>();
    // List of Inventory contents
    public NonNullList<ItemStack> inventoryContents = NonNullList.withSize(2, ItemStack.EMPTY);;

    // NBT Tags
    protected static final String SIZE_INVENTORY_NBT_TAG = "Inventory";

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
    }

    /*******************************************************************************************************************
     * Tile Methods                                                                                                    *
     *******************************************************************************************************************/
    @Override
    protected void onServerTick() {
        super.onServerTick();

        // Move out power
        EnergyUtils.distributePowerToFaces(this, world, pos, getMaxEnergyStored() / 6, false);

        // Transfer Energy In
        if(!getStackInSlot(DRAIN_SLOT).isEmpty() && getStackInSlot(DRAIN_SLOT).hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage drainingStack = getStackInSlot(DRAIN_SLOT).getCapability(CapabilityEnergy.ENERGY, null);
            EnergyUtils.transferPower(drainingStack, this, getDefaultEnergyStorageSize() / 200, false);
        } else if(!getStackInSlot(FILL_SLOT).isEmpty() && getStackInSlot(FILL_SLOT).hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage fillStack = getStackInSlot(FILL_SLOT).getCapability(CapabilityEnergy.ENERGY, null);
            EnergyUtils.transferPower(this, fillStack, getDefaultEnergyStorageSize() / 200, false);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        ItemStackHelper.saveAllItems(compound, inventoryContents);
        compound.setInteger("Tier", tier);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        ItemStackHelper.loadAllItems(compound, inventoryContents);
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
        return BASE_STORAGE * (tier * 8);
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
        callBacks.add(iInventoryCallback);
        return this;
    }

    /**
     * Called when the inventory has a change
     *
     * @param slot The slot that changed
     */
    protected void onInventoryChanged(int slot) {
        callBacks.forEach((IInventoryCallback callback) -> {
            callback.onInventoryChanged(this, slot);
        });
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
                if(!stack.isEmpty())
                    inventoryContents.set(i, stack.copy());
                else
                    inventoryContents.set(i, ItemStack.EMPTY);
            }
        }
    }

    /**
     * Makes sure this slot is within our range
     * @param slot Which slot
     */
    protected boolean isValidSlot(int slot) {
        return slot > 0 || slot <= inventoryContents.size();
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
                stack.hasCapability(CapabilityEnergy.ENERGY, null);
    }

    /**
     * Overrides the stack in the given slot. This method is used by the
     * standard Forge helper methods and classes. It is not intended for
     * general use by other mods, and the handler may throw an error if it
     * is called unexpectedly.
     *
     * @param slot  Slot to modify
     * @param stack ItemStack to set slot to (may be null)
     * @throws RuntimeException if the handler is called in a way that the handler
     * was not expecting.
     **/
    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if(!isValidSlot(slot))
            return;
        if (ItemStack.areItemStacksEqual(this.inventoryContents.get(slot), stack))
            return;
        this.inventoryContents.set(slot, stack);
        onInventoryChanged(slot);
    }

    /**
     * Returns the number of slots available
     *
     * @return The number of slots available
     **/
    @Override
    public int getSlots() {
        return inventoryContents.size();
    }

    /**
     * Returns the ItemStack in a given slot.
     *
     * The result's stack size may be greater than the itemstacks max size.
     *
     * If the result is null, then the slot is empty.
     * If the result is not null but the stack size is zero, then it represents
     * an empty slot that will only accept* a specific itemstack.
     *
     * <p/>
     * IMPORTANT: This ItemStack MUST NOT be modified. This method is not for
     * altering an inventories contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     * <p/>
     * SERIOUSLY: DO NOT MODIFY THE RETURNED ITEMSTACK
     *
     * @param slot Slot to query
     * @return ItemStack in given slot. May not be null.
     **/
    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        if(!isValidSlot(slot))
            return ItemStack.EMPTY;
        return inventoryContents.get(slot);
    }

    /**
     * Inserts an ItemStack into the given slot and return the remainder.
     * The ItemStack should not be modified in this function!
     * Note: This behaviour is subtly different from IFluidHandlers.fill()
     *
     * @param slot     Slot to insert into.
     * @param stack    ItemStack to insert.
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return null).
     *         May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     **/
    @Nonnull
    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack == null || stack.getCount() == 0 || !isItemValidForSlot(slot, stack))
            return ItemStack.EMPTY;

        if(!isValidSlot(slot))
            return ItemStack.EMPTY;

        ItemStack existing = this.inventoryContents.get(slot);

        int limit = getSlotLimit(slot);

        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                this.inventoryContents.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            }
            else {
                existing.setCount(existing.getCount() + (reachedLimit ? limit : stack.getCount()));
            }
            onInventoryChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    /**
     * Extracts an ItemStack from the given slot. The returned value must be null
     * if nothing is extracted, otherwise it's stack size must not be greater than amount or the
     * itemstacks getMaxStackSize().
     *
     * @param slot     Slot to extract from.
     * @param amount   Amount to extract (may be greater than the current stacks max limit)
     * @param simulate If true, the extraction is only simulated
     * @return ItemStack extracted from the slot, must be null, if nothing can be extracted
     **/
    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        if(!isValidSlot(slot))
            return ItemStack.EMPTY;
        ItemStack existing = this.inventoryContents.get(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.inventoryContents.set(slot, ItemStack.EMPTY);
                onInventoryChanged(slot);
            }
            return existing;
        }
        else {
            if (!simulate) {
                this.inventoryContents.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                onInventoryChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    /**
     * Retrieves the maximum stack size allowed to exist in the given slot.
     *
     * @param slot Slot to query.
     * @return The maximum stack size allowed in the slot.
     */
    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }
}
