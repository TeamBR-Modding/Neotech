package com.dyonovan.jatm.common.container.generators;

import com.dyonovan.jatm.common.container.BaseContainer;
import com.dyonovan.jatm.common.tileentity.generator.TileFurnaceGenerator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.SlotFurnaceFuel;

public class ContainerFurnaceGenerator extends BaseContainer {

    public ContainerFurnaceGenerator(InventoryPlayer inventory, TileFurnaceGenerator tileFurnaceGenerator) {

        addSlotToContainer(new SlotFurnaceFuel(tileFurnaceGenerator, TileFurnaceGenerator.FUEL_SLOT, 80, 53));
        bindPlayerInventory(inventory, 8, 84);
    }
}

