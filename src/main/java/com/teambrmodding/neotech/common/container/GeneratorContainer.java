package com.teambrmodding.neotech.common.container;

import com.teambr.nucleus.common.container.BaseContainer;
import com.teambrmodding.neotech.common.tileentity.GeneratorTile;
import com.teambrmodding.neotech.managers.ContainerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 * This file was created for AssistedProgression
 * <p>
 * AssistedProgression is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author James Rogers - Dyonovan
 * @since 9/3/2019
 */
public class GeneratorContainer extends BaseContainer {

    public GeneratorTile tile;

    /**
     * Creates the container object
     *
     * @param id
     * @param playerInventory The players inventory
     * @param inventory       The tile/object inventory
     */
    public GeneratorContainer(int id, IInventory playerInventory, IItemHandler inventory) {
        super(ContainerManager.generator, id, playerInventory, inventory);

        addInventoryLine(62, 19, 0, 3);
        addSlot(new SlotItemHandler(inventory, 3, 80, 38) {
            @Override
            public boolean isItemValid(@Nonnull ItemStack stack) {
                return false;
            }
        });
        this.tile = (GeneratorTile) inventory;
        addInventoryLine(62, 57, 4, 3);
        addPlayerInventorySlots(8, 84);
    }

    public GeneratorContainer(int windowID, PlayerInventory playerInv, PacketBuffer extraData) {
        this(windowID, playerInv,
                ((GeneratorTile) Minecraft.getInstance().world.getTileEntity(extraData.readBlockPos())));
    }
}
