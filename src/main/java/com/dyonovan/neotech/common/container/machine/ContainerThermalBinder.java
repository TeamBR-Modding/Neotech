package com.dyonovan.neotech.common.container.machine;

import com.dyonovan.neotech.common.container.BaseContainer;
import com.dyonovan.neotech.common.container.SlotMB;
import com.dyonovan.neotech.common.tileentity.machine.TileThermalBinder;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;

public class ContainerThermalBinder extends BaseContainer {

    public ContainerThermalBinder(InventoryPlayer inventory, TileThermalBinder tileEntity) {

        addSlotToContainer(new Slot(tileEntity, TileThermalBinder.INPUT_SLOT_1, 70, 18));
        addSlotToContainer(new Slot(tileEntity, TileThermalBinder.INPUT_SLOT_2, 105, 18));
        addSlotToContainer(new Slot(tileEntity, TileThermalBinder.INPUT_SLOT_3, 70, 52));
        addSlotToContainer(new Slot(tileEntity, TileThermalBinder.INPUT_SLOT_4, 105, 52));
        addSlotToContainer(new SlotMB(tileEntity, TileThermalBinder.MB_SLOT_INPUT, 41, 34));
        addSlotToContainer(new SlotFurnaceOutput(inventory.player, tileEntity, TileThermalBinder.MB_SLOT_OUTPUT, 138, 35));
        bindPlayerInventory(inventory, 8, 111);
    }
}
