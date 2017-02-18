package com.teambrmodding.neotech.common.tiles;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/11/2017
 */
public class AbstractMachineSidedWrapper implements IItemHandlerModifiable {
        protected final AbstractMachine inv;
        protected final EnumFacing side;

    public AbstractMachineSidedWrapper(AbstractMachine inv, EnumFacing side) {
        this.inv = inv;
        this.side = side;
    }

    public static int getSlot(AbstractMachine inv, int slot, EnumFacing side) {
        int[] slots = inv.getSlotsForFace(side);
        if (slot < slots.length)
            return slots[slot];
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AbstractMachineSidedWrapper that = (AbstractMachineSidedWrapper)o;

        return inv.equals(that.inv) && side == that.side;
    }

    @Override
    public int hashCode() {
        int result = inv.hashCode();
        result = 31 * result + side.hashCode();
        return result;
    }

    @Override
    public int getSlots() {
        return inv.getSlotsForFace(side).length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        int i = getSlot(inv, slot, side);
        return i == -1 ? null : inv.getStackInSlot(i);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        int slot1 = getSlot(inv, slot, side);

        if (slot1 == -1)
            return null;

        if (!inv.canInsertItem(slot1, stack, side))
            return stack;
        else
            return inv.insertItem(slot, stack, simulate);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        inv.inventoryContents.set(slot, stack);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return null;

        int slot1 = getSlot(inv, slot, side);

        if (slot1 == -1)
            return null;

        ItemStack stackInSlot = inv.getStackInSlot(slot1);

        if (stackInSlot == null)
            return null;

        if (!inv.canExtractItem(slot1, stackInSlot, side))
            return null;

        if (simulate) {
            if (stackInSlot.stackSize < amount) {
                return stackInSlot.copy();
            } else {
                ItemStack copy = stackInSlot.copy();
                copy.stackSize = amount;
                return copy;
            }
        } else {
            return inv.extractItem(slot1, amount, simulate);
        }
    }
}