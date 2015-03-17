package com.dyonovan.jatm.common.container.generators;

import com.dyonovan.jatm.common.container.BaseContainer;
import com.dyonovan.jatm.common.tileentity.generator.TileGenerator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.SlotFurnaceFuel;

public class ContainerGenerator extends BaseContainer {

    TileGenerator tile;

    public ContainerGenerator(InventoryPlayer inventory, TileGenerator tileGenerator) {

        this.tile = tileGenerator;

        addSlotToContainer(new SlotFurnaceFuel(tileGenerator, TileGenerator.FUEL_SLOT, 80, 53));
        bindPlayerInventory(inventory, 8, 84);
    }
}

