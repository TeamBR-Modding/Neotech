package com.dyonovan.jatm.common.container.generators;

import com.dyonovan.jatm.common.container.BaseContainer;
import com.dyonovan.jatm.common.tileentity.generator.TileCoalGenerator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.SlotFurnaceFuel;

public class ContainerCoalGenerator extends BaseContainer {

    public ContainerCoalGenerator(InventoryPlayer inventory, TileCoalGenerator tileCoalGenerator) {

        addSlotToContainer(new SlotFurnaceFuel(tileCoalGenerator, TileCoalGenerator.FUEL_SLOT, 80, 53));
        bindPlayerInventory(inventory, 8, 84);
    }
}

