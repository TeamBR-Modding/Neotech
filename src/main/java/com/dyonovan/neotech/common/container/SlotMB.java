package com.dyonovan.neotech.common.container;

import com.dyonovan.neotech.handlers.ItemHandler;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SlotMB extends Slot {

    public SlotMB(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public void putStack(ItemStack stack) {
        if (stack == null) return;

        if (stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();
            int count = 0;
            if (tag.hasKey("Speed")) {
                inventory.setInventorySlotContents(count, new ItemStack(ItemHandler.speedProcessor, tag.getInteger("Speed")));
                ++count;
            }
            if (tag.hasKey("Efficiency")) {
                inventory.setInventorySlotContents(count, new ItemStack(ItemHandler.effFan, tag.getInteger("Efficiency")));
                ++count;
            }
            if (tag.hasKey("Capacity")) {
                inventory.setInventorySlotContents(count, new ItemStack(ItemHandler.capRam, tag.getInteger("Capacity")));
                ++count;
            }
            if (tag.hasKey("AutoOutput")) {
                inventory.setInventorySlotContents(count, new ItemStack(ItemHandler.ioPort, 1));
                ++count;
            }
        }
        super.putStack(new ItemStack(ItemHandler.upgradeMB));
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        if (stack.getItem() == ItemHandler.upgradeMB) return true;
        if (stack.getItem() == ItemHandler.upgradeMBFull) {
            if (!stack.hasTagCompound()) return true;
            for (int i = 0; i < 4; i++) {
                if (inventory.getStackInSlot(i) != null) return false;
            }
            return true;
        }
        return false;
    }
}
