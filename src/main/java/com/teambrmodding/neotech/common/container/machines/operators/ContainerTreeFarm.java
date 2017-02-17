package com.teambrmodding.neotech.common.container.machines.operators;

import com.teambrmodding.neotech.common.container.machines.ContainerAbstractMachine;
import com.teambrmodding.neotech.common.tiles.AbstractMachine;
import com.teambrmodding.neotech.common.tiles.machines.operators.TileTreeFarm;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/15/2017
 */
public class ContainerTreeFarm extends ContainerAbstractMachine {

    /**
     * Creates the object
     *
     * @param playerInventory The players inventory
     * @param inventory       The tile/object inventory
     */
    public ContainerTreeFarm(InventoryPlayer playerInventory, TileTreeFarm inventory) {
        super(playerInventory, inventory);

        // Add Tool Slots
        addSlotToContainer(new SlotItemHandler(inventory, inventory.AXE_SLOT(), 62, 20){
            /**
             * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
             */
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() instanceof ItemTool || stack.getItem().getToolClasses(stack).contains("axe");
            }

            @Nullable
            @Override
            public String getSlotTexture() {
                return "neotech:items/axe_ghost";
            }
        });
        addSlotToContainer(new SlotItemHandler(inventory, inventory.SHEARS_SLOT(), 89, 20) {
            /**
             * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
             */
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() instanceof ItemShears;
            }

            @Nullable
            @Override
            public String getSlotTexture() {
                return "neotech:items/shears_ghost";
            }
        });

        // Add Inventory Slots
        addInventoryGrid(62, 40, 6, 3);

        // Add sapling slots
        addInventoryLine(116, 20, 15, 3);

        // Add player slots
        addPlayerInventorySlots(84);
    }
}
