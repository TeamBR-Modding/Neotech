package com.teambrmodding.neotech.common.container.slot;

import com.teambr.bookshelf.common.container.slots.ICustomSlot;
import com.teambr.bookshelf.common.container.slots.SLOT_SIZE;
import com.teambr.bookshelf.common.tiles.traits.Inventory;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.SlotItemHandler;
import scala.Enumeration;
import scala.Tuple2;

import java.awt.*;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis <pauljoda>
 * @since 1/27/2016
 */

public class SlotFurnaceOutputItemHandler extends SlotItemHandler implements ICustomSlot {
    /** The player that is using the GUI where this slot resides. */
    private EntityPlayer thePlayer;
    private int stackSize;

    public SlotFurnaceOutputItemHandler(EntityPlayer player, Inventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
        super(inventoryIn, slotIndex, xPosition, yPosition);
        this.thePlayer = player;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    public ItemStack decrStackSize(int amount) {
        if (this.getHasStack()) {
            this.stackSize += Math.min(amount, this.getStack().stackSize);
        }

        return super.decrStackSize(amount);
    }

    public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
        this.onCrafting(stack);
        super.onPickupFromSlot(playerIn, stack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    protected void onCrafting(ItemStack stack, int amount) {
        this.stackSize += amount;
        this.onCrafting(stack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    protected void onCrafting(ItemStack stack) {
        stack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.stackSize);

        if (!this.thePlayer.worldObj.isRemote) {
            int i = this.stackSize;
            float f = FurnaceRecipes.instance().getSmeltingExperience(stack);

            if (f == 0.0F) {
                i = 0;
            }
            else if (f < 1.0F) {
                int j = MathHelper.floor_float((float)i * f);

                if (j < MathHelper.ceiling_float_int((float)i * f) && Math.random() < (double)((float)i * f - (float)j))
                {
                    ++j;
                }

                i = j;
            }

            while (i > 0) {
                int k = EntityXPOrb.getXPSplit(i);
                i -= k;
                this.thePlayer.worldObj.spawnEntityInWorld(new EntityXPOrb(this.thePlayer.worldObj, this.thePlayer.posX, this.thePlayer.posY + 0.5D, this.thePlayer.posZ + 0.5D, k));
            }
        }

        this.stackSize = 0;

        net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerSmeltedEvent(thePlayer, stack);

        if (stack.getItem() == Items.IRON_INGOT) {
            this.thePlayer.addStat(AchievementList.ACQUIRE_IRON);
        }

        if (stack.getItem() == Items.COOKED_FISH) {
            this.thePlayer.addStat(AchievementList.COOK_FISH);
        }
    }

    @Override
    public Enumeration.Value getSlotSize() {
        return SLOT_SIZE.LARGE();
    }

    @Override
    public Tuple2<Integer, Integer> getPoint() {
        return new Tuple2<>(xDisplayPosition - 5, yDisplayPosition - 5);
    }

    @Override
    public boolean hasColor() { return false; }

    @Override
    public Color getColor() { return new Color(0, 0, 0); }
}
