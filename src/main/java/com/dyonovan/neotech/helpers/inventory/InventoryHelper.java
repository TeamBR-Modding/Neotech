package com.dyonovan.neotech.helpers.inventory;

import com.dyonovan.neotech.common.pipe.item.PipeBasicItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class InventoryHelper {

    public static int moveStack(IInventory tile, ItemStack stack, EnumFacing facing) {
        if (tile == null) return 0;

        if (tile instanceof PipeBasicItem) {
            return ((PipeBasicItem)tile).getBuffer().acceptResource(stack.stackSize, facing.getOpposite(), stack, false).stackSize;
        } else if (tile instanceof ISidedInventory) {
            ISidedInventory inventory = (ISidedInventory) tile;
            int sizeMoved = 0;
            for (int i : inventory.getSlotsForFace(facing.getOpposite())) {
                if (stack.stackSize <= 0) return sizeMoved;
                if (inventory.canInsertItem(i, stack, facing.getOpposite())) {
                    if (inventory.getStackInSlot(i) == null) {
                        inventory.setInventorySlotContents(i, stack);
                        sizeMoved += stack.stackSize;
                    } else if (compareStack(inventory.getStackInSlot(i), stack)) {
                        int actual = Math.min(stack.stackSize, inventory.getStackInSlot(i).getMaxStackSize() - inventory.getStackInSlot(i).stackSize);
                        inventory.getStackInSlot(i).stackSize += actual;
                        sizeMoved += actual;
                    }
                }
            }
            return sizeMoved;
        } else {
            int sizeMoved = 0;
            for (int i = 0; i < tile.getSizeInventory(); i++) {
                if (tile.getStackInSlot(i) == null) {
                    tile.setInventorySlotContents(i, stack);
                    sizeMoved += stack.stackSize;
                } else if (compareStack(tile.getStackInSlot(i), stack)) {
                    int actual = Math.min(stack.stackSize, tile.getStackInSlot(i).getMaxStackSize() - tile.getStackInSlot(i).stackSize);
                    tile.getStackInSlot(i).stackSize += actual;
                    sizeMoved += actual;
                }
            }
            return sizeMoved;
        }
    }

    private static boolean compareStack(ItemStack stack1, ItemStack stack2) {
        return !(stack1 == null || stack2 == null) &&
                stack1.getItem() == stack2.getItem() &&
                stack1.getItemDamage() == stack2.getItemDamage() &&
                ((!stack1.hasTagCompound() && !stack2.hasTagCompound()) ||
                        stack1.getTagCompound() == stack2.getTagCompound());
    }
}