package com.dyonovan.neotech.common.container;

import com.dyonovan.neotech.common.tileentity.BaseMachine;
import com.dyonovan.neotech.handlers.ItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SlotUpgrade extends Slot {

    private IInventory tile;

    public SlotUpgrade(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);

        tile = inventoryIn;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return stack.getItem() == ItemHandler.upgradeMBFull;
    }

    @Override
    public void putStack(ItemStack stack) {
        if (stack == null) return;

        if (stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag.hasKey("Speed")) {
                tile.setField(BaseMachine.SPEED, tag.getInteger("Speed"));
                //tile.setField(BaseMachine.EFFICIENCY, tag.getInteger("Speed") * -1);
            }
            if (tag.hasKey("Capacity")) {
                tile.setField(BaseMachine.CAPACITY, tag.getInteger("Capacity"));
            }
            if (tag.hasKey("MinerSize")) {
                tile.setField(BaseMachine.SIZE, tag.getInteger("MinerSize"));
            }
            if (tag.hasKey("AutoOutput")) {
                tile.setField(BaseMachine.IO, tag.getBoolean("AutoOutput") ? 1 : 0);
            }
            if (tag.hasKey("SilkTouch")) {
                tile.setField(BaseMachine.SILKTOUCH, tag.getBoolean("SilkTouch") ? 1 : 0);
            }
            int eff = 0;
            if (tag.hasKey("Efficiency")) eff += tile.getField(BaseMachine.EFFICIENCY) + tag.getInteger("Efficiency");
            if (tag.hasKey("Speed")) eff += tag.getInteger("Speed") * -1;
            if (tag.hasKey("MinerSize")) eff += tag.getInteger("MinerSize") * -3;
            if (tag.hasKey("SilkTouch")) if (tag.getBoolean("SilkTouch")) eff += -5;
            tile.setField(BaseMachine.EFFICIENCY, eff);
        }
        super.putStack(stack);
    }

    @Override
    public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
       for (int i = 0; i < 4; i++) {
           tile.setField(i, 0);
       }
        super.onPickupFromSlot(playerIn, stack);
    }
}
