package com.dyonovan.neotech.common.container.machine;

import com.dyonovan.neotech.common.container.BaseContainer;
import com.dyonovan.neotech.common.tileentity.machine.TileElectricCrusher;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;

public class ContainerElectricCrusher extends BaseContainer {

    public ContainerElectricCrusher(InventoryPlayer inventory, TileElectricCrusher tileEntity) {

        addSlotToContainer(new Slot(tileEntity, TileElectricCrusher.INPUT_SLOT, 57, 34));
        addSlotToContainer(new SlotFurnaceOutput(inventory.player, tileEntity, TileElectricCrusher.OUTPUT_SLOT, 117, 34));
        bindPlayerInventory(inventory, 8, 84);
    }
}
