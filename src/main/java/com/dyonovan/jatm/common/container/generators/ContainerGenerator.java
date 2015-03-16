package com.dyonovan.jatm.common.container.generators;

import com.dyonovan.jatm.common.container.BaseContainer;
import com.dyonovan.jatm.common.tileentity.TileGenerator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.SlotFurnaceFuel;

public class ContainerGenerator extends BaseContainer {

    TileGenerator tile;

    public ContainerGenerator(InventoryPlayer inventory, TileGenerator tileGenerator) {

        this.tile = tileGenerator;

        addSlotToContainer(new SlotFurnaceFuel(inventory, 0, 67, 44));
        bindPlayerInventory(inventory, 8, 84);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();


    }
}
