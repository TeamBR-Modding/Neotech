package com.teambrmodding.neotech.common.container.machines.processors;

import com.teambrmodding.neotech.common.container.machines.ContainerAbstractMachine;
import com.teambrmodding.neotech.common.tiles.AbstractMachine;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

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
public class ContainerElectricCrusher extends ContainerAbstractMachine {

    /**
     * Creates the object
     *
     * @param playerInventory The players inventory
     * @param inventory       The tile/object inventory
     */
    public ContainerElectricCrusher(InventoryPlayer playerInventory, AbstractMachine inventory) {
        super(playerInventory, inventory);

        addSlotToContainer(new SlotItemHandler(inventory, 0, 56, 35));
        addSlotToContainer(new SlotItemHandler(inventory, 1, 117, 35) {
            /**
             * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
             */
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });
        addSlotToContainer(new SlotItemHandler(inventory, 2, 139, 35) {
            /**
             * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
             */
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });

        addPlayerInventorySlots(84);
    }
}
