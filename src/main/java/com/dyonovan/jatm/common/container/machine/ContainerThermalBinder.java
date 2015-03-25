package com.dyonovan.jatm.common.container.machine;

import com.dyonovan.jatm.common.container.BaseContainer;
import com.dyonovan.jatm.common.tileentity.machine.TileThermalBinder;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerThermalBinder extends BaseContainer {

    public ContainerThermalBinder(InventoryPlayer inventory, TileThermalBinder tileEntity) {

        addSlotToContainer(new Slot(tileEntity, TileThermalBinder.INPUT_SLOT_1, 78, 18));
        addSlotToContainer(new Slot(tileEntity, TileThermalBinder.INPUT_SLOT_2, 113, 18));
        addSlotToContainer(new Slot(tileEntity, TileThermalBinder.INPUT_SLOT_3, 78, 52));
        addSlotToContainer(new Slot(tileEntity, TileThermalBinder.INPUT_SLOT_4, 113, 52));
        addSlotToContainer(new Slot(tileEntity, TileThermalBinder.MB_SLOT, 43, 34));
        addSlotToContainer(new Slot(tileEntity, TileThermalBinder.INGOT_SLOT, 142, 10));
        bindPlayerInventory(inventory, 8, 84);
    }
}
