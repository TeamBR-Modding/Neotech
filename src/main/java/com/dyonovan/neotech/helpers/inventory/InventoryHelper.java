package com.dyonovan.neotech.helpers.inventory;

import com.dyonovan.neotech.common.pipe.item.PipeBasicItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

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
                        inventory.setInventorySlotContents(i, new ItemStack(stack.getItem(), stack.stackSize > 0 ? stack.stackSize : 1, stack.getItemDamage()));
                        sizeMoved += stack.stackSize;
                        stack.stackSize = 0;
                    } else if (compareStack(inventory.getStackInSlot(i), stack)) {
                        int actual = Math.min(stack.stackSize, inventory.getStackInSlot(i).getMaxStackSize() - inventory.getStackInSlot(i).stackSize);
                        inventory.getStackInSlot(i).stackSize += actual;
                        sizeMoved += actual;
                        stack.stackSize -= actual;
                    }
                }
            }
            return sizeMoved;
        } else {
            int sizeMoved = 0;
            for (int i = 0; i < tile.getSizeInventory(); i++) {
                if (stack.stackSize <= 0) return sizeMoved;
                if (tile.getStackInSlot(i) == null) {
                    tile.setInventorySlotContents(i, new ItemStack(stack.getItem(), stack.stackSize > 0 ? stack.stackSize : 1, stack.getItemDamage()));
                    sizeMoved += stack.stackSize;
                    stack.stackSize = 0;
                } else if (compareStack(tile.getStackInSlot(i), stack)) {
                    int actual = Math.min(stack.stackSize, tile.getStackInSlot(i).getMaxStackSize() - tile.getStackInSlot(i).stackSize);
                    tile.getStackInSlot(i).stackSize += actual;
                    stack.stackSize -= actual;
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

    //Original Code From openBlocksLib
    public static IInventory doubleChestFix(TileEntity tile) {
        final World world = tile.getWorld();
        final BlockPos pos = tile.getPos();
        if (world.getBlockState(pos.west()).getBlock() == Blocks.chest)
            return new InventoryLargeChest("Large chest", (ILockableContainer)world.getTileEntity(pos.west()), (ILockableContainer)tile);
        if (world.getBlockState(pos.north()).getBlock() == Blocks.chest)
            return new InventoryLargeChest("Large chest", (ILockableContainer)tile, (ILockableContainer)world.getTileEntity(pos.north()));
        if (world.getBlockState(pos.east()).getBlock() == Blocks.chest)
            return new InventoryLargeChest("Large chest", (ILockableContainer)world.getTileEntity(pos.east()), (ILockableContainer)tile);
        if (world.getBlockState(pos.south()).getBlock() == Blocks.chest)
            return new InventoryLargeChest("Large chest", (ILockableContainer)tile, (ILockableContainer)world.getTileEntity(pos.south()));
        return (tile instanceof IInventory)? (IInventory)tile : null;
    }
}