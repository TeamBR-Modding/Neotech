package com.teambrmodding.neotech.common.container.storage;

import com.teambr.bookshelf.common.container.BaseContainer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.items.IItemHandler;
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
public class ContainerEnergyStorage extends BaseContainer {

    /**
     * Creates the contianer object
     *
     * @param playerInventory The players inventory
     * @param inventory       The tile/object inventory
     */
    public ContainerEnergyStorage(IInventory playerInventory, IItemHandler inventory) {
        super(playerInventory, inventory);

        addSlotToContainer(new SlotItemHandler(inventory, 0, 37, 12){
            @Nullable
            @Override
            public String getSlotTexture() {
                return "neotech:gui/in";
            }
        });
        addSlotToContainer(new SlotItemHandler(inventory, 1, 37, 58){
            @Nullable
            @Override
            public String getSlotTexture() {
                return "neotech:gui/out";
            }
        });

        addPlayerInventorySlots(84);
    }
}
