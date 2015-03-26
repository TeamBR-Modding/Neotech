package com.dyonovan.neotech.common.container.machine;

import com.dyonovan.neotech.common.container.BaseContainer;
import com.dyonovan.neotech.common.tileentity.machine.TileElectricFurnace;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;

public class ContainerElectricFurnace extends BaseContainer {

    public ContainerElectricFurnace(InventoryPlayer inventory, TileElectricFurnace tileEntity) {

        addSlotToContainer(new Slot(tileEntity, TileElectricFurnace.INPUT_SLOT, 57, 34));
        addSlotToContainer(new SlotFurnaceOutput(inventory.player, tileEntity, TileElectricFurnace.OUTPUT_SLOT, 117, 34));
        bindPlayerInventory(inventory, 8, 84);
    }
}
