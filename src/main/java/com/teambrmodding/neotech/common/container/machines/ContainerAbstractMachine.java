package com.teambrmodding.neotech.common.container.machines;

import com.teambr.bookshelf.common.container.BaseContainer;
import com.teambrmodding.neotech.common.tiles.AbstractMachine;
import com.teambrmodding.neotech.managers.CapabilityLoadManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/15/2017
 */
public class ContainerAbstractMachine extends BaseContainer {
    protected AbstractMachine abstractMachine;

    // List of upgrade slots
    public List<SlotItemHandler> upgradeSlots;

    /**
     * Creates the object
     *
     * @param playerInventory The players inventory
     * @param inventory       The tile/object inventory
     */
    public ContainerAbstractMachine(InventoryPlayer playerInventory, AbstractMachine inventory) {
        super(playerInventory, inventory);
        abstractMachine = inventory;

        // Manage upgrade slots
        upgradeSlots = new ArrayList<>();
        for(int id = 0; id < abstractMachine.upgradeInventory.inventoryContents.size(); id++) {
            SlotItemHandler slot = new SlotItemHandler(abstractMachine.upgradeInventory, id, -1000, -1000) {
                /**
                 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
                 *
                 * @param stack
                 */
                @Override
                public boolean isItemValid(ItemStack stack) {
                    return !abstractMachine.hasUpgradeAlready(stack) &&
                            stack.hasCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null);
                }
            };
            upgradeSlots.add(slot);
            addSlotToContainer(slot);
        }
    }

    /*******************************************************************************************************************
     * BaseContainer                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Get the size of the inventory that isn't the players
     *
     * @return The inventory size that doesn't count the player inventory
     */
    @Override
    public int getInventorySizeNotPlayer() {
        return abstractMachine.getInitialSize() + abstractMachine.upgradeInventory.getSlots();
    }
}
