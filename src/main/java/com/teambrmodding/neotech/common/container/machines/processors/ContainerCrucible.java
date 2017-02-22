package com.teambrmodding.neotech.common.container.machines.processors;

import com.teambrmodding.neotech.common.container.machines.ContainerAbstractMachine;
import com.teambrmodding.neotech.common.tiles.AbstractMachine;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.SlotItemHandler;

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
public class ContainerCrucible extends ContainerAbstractMachine {

    /**
     * Creates the object
     *
     * @param playerInventory The players inventory
     * @param inventory       The tile/object inventory
     */
    public ContainerCrucible(InventoryPlayer playerInventory, AbstractMachine inventory) {
        super(playerInventory, inventory);

        addSlotToContainer(new SlotItemHandler(inventory, 0, 56, 35));

        addPlayerInventorySlots(84);
    }
}
