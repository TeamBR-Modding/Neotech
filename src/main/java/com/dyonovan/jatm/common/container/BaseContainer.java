package com.dyonovan.jatm.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class BaseContainer extends Container {

    protected int tileSlots = -1;
    protected int InventoryMin;
    protected int InventoryMax;
    protected int HotBarMax;

    protected boolean canSendToTile = true;

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    public void setCanSendToTile(boolean bool) {
        canSendToTile = bool;
    }

    @Override
    public Slot addSlotToContainer(Slot slot) {
        if(!(slot.inventory instanceof InventoryPlayer))
            tileSlots++;
        return super.addSlotToContainer(slot);
    }

    protected void bindPlayerInventory(InventoryPlayer playerInventory, int pixelX, int pixelY)
    {
        // Inventory
        InventoryMin = InventoryMax = tileSlots + 1;
        for(int y = 0; y < 3; y++)
            for(int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(playerInventory, x + y * 9 + 9, pixelX + x * 18, pixelY + y * 18));
                InventoryMax++;
            }

        // Action Bar
        for(int x = 0; x < 9; x++)
            addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18, pixelY + 58));

        HotBarMax = InventoryMax + 9;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
        ItemStack stack = null;
        Slot slotObject = (Slot)this.inventorySlots.get(slot);
        //null checks and checks if the item can be stacked (maxStackSize > 1)
        if (slotObject != null && slotObject.getHasStack()) {
            ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();

            //merges the item into player inventory since its in the tileEntity
            if (slot <= tileSlots) {
                if (!this.mergeItemStack(stackInSlot, InventoryMin, HotBarMax, true))
                    return null;
            }

            //Merge into tile (if allowed)
            else if(canSendToTile && slot > tileSlots) {
                if(!this.mergeItemStack(stackInSlot, 0, tileSlots + 1, false))
                    return null;
            }

            //Inventory to HotBar
            else if(slot >= InventoryMin && slot <= InventoryMax - 1) {
                if(!this.mergeItemStack(stackInSlot, InventoryMax, HotBarMax, false))
                    return null;
            }

            //HotBar to inventory
            else if(slot >= InventoryMax && slot <= HotBarMax - 1) {
                if(!this.mergeItemStack(stackInSlot, InventoryMin, InventoryMax, false))
                    return null;
            }

            if (stackInSlot.stackSize == 0) {
                slotObject.putStack(null);
            } else {
                slotObject.onSlotChanged();
            }

            if (stackInSlot.stackSize == stack.stackSize) {
                return null;
            }
            slotObject.onPickupFromSlot(player, stackInSlot);
        }
        return stack;
    }
}
