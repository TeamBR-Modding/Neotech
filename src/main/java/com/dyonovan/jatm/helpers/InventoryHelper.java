package com.dyonovan.jatm.helpers;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import java.util.Set;

public class InventoryHelper {

    public static boolean insertItemIntoInventory(IInventory inventory, ItemStack stack, EnumFacing side, int intoSlot, boolean canStack) {
        if (stack == null) return false;
        final Set<Integer> attemptSlots = Sets.newTreeSet();

        // if it's a sided inventory, get all the accessible slots
        final boolean isSidedInventory = inventory instanceof ISidedInventory && side != null;
        if (isSidedInventory) {
            int[] accessibleSlots = ((ISidedInventory) inventory).getSlotsForFace(side);
            for (int slot : accessibleSlots)
                attemptSlots.add(slot);
        } else {
            // if it's just a standard inventory, get all slots
            for (int a = 0; a < inventory.getSizeInventory(); a++) {
                attemptSlots.add(a);
            }
        }
        // if we've defining a specific slot, we'll just use that
        if (intoSlot > -1) attemptSlots.retainAll(ImmutableSet.of(intoSlot));
        if (attemptSlots.isEmpty()) return false;
        for (Integer slot : attemptSlots) {
            if (stack.stackSize <= 0) break;
            if (isSidedInventory && !((ISidedInventory) inventory).canInsertItem(slot, stack, side)) continue;
            do {
                int actual = tryInsertStack(inventory, slot, stack, canStack);
                stack.stackSize -= actual;
            } while (stack.stackSize > 0);
            return true;
        }
        return false;
    }

    public static int tryInsertStack(IInventory targetInventory, int slot, ItemStack stack, boolean canMerge) {
        if (targetInventory.isItemValidForSlot(slot, stack)) {
            ItemStack targetStack = targetInventory.getStackInSlot(slot);
            if (targetStack == null) {
                targetInventory.setInventorySlotContents(slot, stack.copy());
                stack.stackSize = 0;
                return 0;
            } else if (canMerge) {
                if (targetInventory.isItemValidForSlot(slot, stack) &&
                        areMergeCandidates(stack, targetStack)) {
                    int space = targetStack.getMaxStackSize()
                            - targetStack.stackSize;
                    int mergeAmount = Math.min(space, stack.stackSize);
                    ItemStack copy = targetStack.copy();
                    copy.stackSize += mergeAmount;
                    targetInventory.setInventorySlotContents(slot, copy);
                    stack.stackSize -= mergeAmount;
                    return stack.stackSize;
                }
            }
        }
        return stack.stackSize;
    }

    public static boolean areItemAndTagEqual(final ItemStack stackA, ItemStack stackB) {
        return stackA.isItemEqual(stackB) && ItemStack.areItemStackTagsEqual(stackA, stackB);
    }

    public static boolean areMergeCandidates(ItemStack source, ItemStack target) {
        return areItemAndTagEqual(source, target) && target.stackSize < target.getMaxStackSize();
    }
}
