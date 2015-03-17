package com.dyonovan.jatm.common.container.machine;

import com.dyonovan.jatm.common.container.BaseContainer;
import com.dyonovan.jatm.common.tileentity.machine.TileElectricFurnace;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;

public class ContainerElectricFurnace extends BaseContainer {

    TileElectricFurnace tile;

    public ContainerElectricFurnace(InventoryPlayer inventory, TileElectricFurnace tileEntity) {
        this.tile = tileEntity;

        addSlotToContainer(new Slot(tileEntity, TileElectricFurnace.INPUT_SLOT, 57, 34));
        addSlotToContainer(new SlotFurnaceOutput(inventory.player, tileEntity, TileElectricFurnace.OUTPUT_SLOT, 117, 34));
        bindPlayerInventory(inventory, 8, 84);
    }
}
