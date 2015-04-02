package com.dyonovan.neotech.helpers.inventory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

import java.util.Set;

/**
 * From OpenModLib
 */

public class InventoryHelperOld {

    /**
     * Try to merge the supplied stack into the supplied slot in the target
     * inventory
     *
     * @param targetInventory Although it doesn't return anything, it'll REDUCE the stack
     *                        size of the stack that you pass in
     * @param slot
     * @param stack
     */
    public static int tryInsertStack(IInventory targetInventory, int slot, ItemStack stack, boolean canMerge) {
        if (targetInventory.isItemValidForSlot(slot, stack)) {
            ItemStack targetStack = targetInventory.getStackInSlot(slot);
            if (targetStack == null) {
                targetInventory.setInventorySlotContents(slot, stack.copy());
                return stack.stackSize;
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
                    return mergeAmount;
                }
            }
        }
        return 0;
    }

    public static boolean areItemAndTagEqual(final ItemStack stackA, ItemStack stackB) {
        return stackA.isItemEqual(stackB) && ItemStack.areItemStackTagsEqual(stackA, stackB);
    }

    public static boolean areMergeCandidates(ItemStack source, ItemStack target) {
        return areItemAndTagEqual(source, target) && target.stackSize < target.getMaxStackSize();
    }

    public static void insertItemIntoInventory(IInventory inventory, ItemStack stack) {
        insertItemIntoInventory(inventory, stack, null, -1);
    }

    public static void insertItemIntoInventory(IInventory inventory, ItemStack stack, EnumFacing side, int intoSlot) {
        insertItemIntoInventory(inventory, stack, side, intoSlot, true);
    }

    public static void insertItemIntoInventory(IInventory inventory, ItemStack stack, EnumFacing side, int intoSlot, boolean doMove) {
        insertItemIntoInventory(inventory, stack, side, intoSlot, doMove, true);
    }

    public static int insertItemIntoInventory(IInventory inventory, ItemStack stack, EnumFacing side, int intoSlot, boolean doMove, boolean canStack) {
        if (stack == null) return 0;

        int actual = 0;
        IInventory targetInventory = inventory;
        // if we're not meant to move, make a clone of the inventory
        if (!doMove) {
            GenericInventory copy = new GenericInventory("temporary.inventory", false, targetInventory.getSizeInventory());
            copy.copyFrom(inventory);
            targetInventory = copy;
        }
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
        if (attemptSlots.isEmpty()) return 0;
        for (Integer slot : attemptSlots) {
            if (stack.stackSize <= 0) break;
            if (isSidedInventory && !((ISidedInventory) inventory).canInsertItem(slot, stack, side)) continue;
            int attempt = tryInsertStack(targetInventory, slot, stack, canStack);
            stack.stackSize -= attempt;
            actual += attempt;
        }
        return actual;
    }

    /**
     * Move an item from the fromInventory, into the target. The target can be
     * an inventory or pipe.
     * Double checks are automagically wrapped. If you're not bothered what slot
     * you insert into, pass -1 for intoSlot. If you're passing false for
     * doMove, it'll create a dummy inventory and its calculations on that
     * instead
     *
     * @param itemStack     the ItemStack to be moved
     * //@param fromSlot      the slot the item is coming from
     * @param target        the inventory you want the item to be put into. can be BC pipe
     *                      or IInventory
     * @param intoSlot      the target slot. Pass -1 for any slot
     * @param maxAmount     The maximum amount you wish to pass
     * @param direction     The direction of the move. Pass UNKNOWN if not applicable
     * @param doMove
     * @param canStack
     * @return The amount of items moved
     */
    public static int moveItemInto(ItemStack itemStack, Object target, int intoSlot, int maxAmount, EnumFacing direction, boolean doMove, boolean canStack) {

        if (itemStack == null) {
            return 0;
        }
        // create a clone of our source stack and set the size to either
        // maxAmount or the stackSize
        ItemStack clonedSourceStack = itemStack.copy();
        clonedSourceStack.stackSize = Math.min(clonedSourceStack.stackSize, maxAmount);
        int amountToMove = clonedSourceStack.stackSize;
        int inserted = 0;
        if (target instanceof IInventory) {
            IInventory targetInventory = getInventory((IInventory) target);
            EnumFacing side = direction.getOpposite();
            // try insert the item into the target inventory. this'll reduce the
            // stackSize of our stack
            insertItemIntoInventory(targetInventory, clonedSourceStack, side, intoSlot, doMove, canStack);
            inserted = amountToMove - clonedSourceStack.stackSize;
        }
        // if we've done the move, reduce/remove the stack from our source
        // inventory
        if (doMove) {
            ItemStack newSourcestack = itemStack.copy();
            newSourcestack.stackSize -= inserted;
            /*if (newSourcestack.stackSize == 0) {
                fromInventory.setInventorySlotContents(fromSlot, null);
            } else {
                fromInventory.setInventorySlotContents(fromSlot, newSourcestack);
            }*/
        }
        return inserted;
    }

    private static IInventory doubleChestFix(net.minecraft.tileentity.TileEntity te) {
        final World world = te.getWorld();
        BlockPos blockPos = te.getPos();
        if (world.getBlockState(blockPos.add(-1, 0, 0)).getBlock() == Blocks.chest)
            return new InventoryLargeChest("Large chest", (ILockableContainer) world.getTileEntity(blockPos.add(-1, 0, 0)), (ILockableContainer) te);
        if (world.getBlockState(blockPos.add(1, 0, 0)).getBlock() == Blocks.chest)
            return new InventoryLargeChest("Large chest", (ILockableContainer) world.getTileEntity(blockPos.add(1, 0, 0)), (ILockableContainer) te);
        if (world.getBlockState(blockPos.add(0, 0, -1)).getBlock() == Blocks.chest)
            return new InventoryLargeChest("Large chest", (ILockableContainer) world.getTileEntity(blockPos.add(0, 0, -1)), (ILockableContainer) te);
        if (world.getBlockState(blockPos.add(0, 0, 1)).getBlock() == Blocks.chest)
            return new InventoryLargeChest("Large chest", (ILockableContainer) world.getTileEntity(blockPos.add(0, 0, 1)), (ILockableContainer) te);
        return (te instanceof IInventory) ? (IInventory) te : null;
    }

    public static IInventory getInventory(World world, BlockPos blockPos) {
        TileEntity tileEntity = world.getTileEntity(blockPos);
        if (tileEntity instanceof TileEntityChest) return doubleChestFix(tileEntity);
        if (tileEntity instanceof IInventory) return (IInventory) tileEntity;
        return null;
    }

    /*public static IInventory getInventory(World world, int x, int y, int z, EnumFacing direction) {
        if (direction != null) {
            direction.
        }
        return getInventory(world, x, y, z);
    }*/
    public static IInventory getInventory(IInventory inventory) {
        if (inventory instanceof TileEntityChest) return doubleChestFix((TileEntity) inventory);
        return inventory;
    }
}
