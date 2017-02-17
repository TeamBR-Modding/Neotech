package com.teambrmodding.neotech.common.container.machines.generators;

import com.teambrmodding.neotech.common.container.machines.ContainerAbstractMachine;
import com.teambrmodding.neotech.common.tiles.AbstractMachine;
import net.minecraft.entity.player.InventoryPlayer;
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
public class ContainerFluidGenerator extends ContainerAbstractMachine {

    /**
     * Creates the object
     *
     * @param playerInventory The players inventory
     * @param inventory       The tile/object inventory
     */
    public ContainerFluidGenerator(InventoryPlayer playerInventory, AbstractMachine inventory) {
        super(playerInventory, inventory);

        // Fluid Handler In
        addSlotToContainer(new SlotItemHandler(inventory, 0, 123, 12) {
            @Nullable
            @Override
            public String getSlotTexture() {
                return "neotech:gui/in";
            }
        });

        // Fluid Handler Out
        addSlotToContainer(new SlotItemHandler(inventory, 1, 123, 58) {
            @Nullable
            @Override
            public String getSlotTexture() {
                return "neotech:gui/out";
            }
        });

        addPlayerInventorySlots(84);
    }
}
