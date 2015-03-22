package com.dyonovan.jatm.common.container.storage;

import com.dyonovan.jatm.common.container.BaseContainer;
import com.dyonovan.jatm.common.tileentity.storage.TileBasicRFStorage;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerRFStorage extends BaseContainer {

    public ContainerRFStorage(InventoryPlayer inventory, TileBasicRFStorage tileEntity) {

        switch (tileEntity.getTier()) {
            case 1:
                addSlotToContainer(new Slot(tileEntity, TileBasicRFStorage.CHARGE_SLOT_1, 148, 18));
                break;
            case 2:
                addSlotToContainer(new Slot(tileEntity, TileBasicRFStorage.CHARGE_SLOT_1, 148, 18));
                addSlotToContainer(new Slot(tileEntity, TileBasicRFStorage.CHARGE_SLOT_2, 148, 36));
                break;
            case 3:
                addSlotToContainer(new Slot(tileEntity, TileBasicRFStorage.CHARGE_SLOT_1, 148, 18));
                addSlotToContainer(new Slot(tileEntity, TileBasicRFStorage.CHARGE_SLOT_2, 148, 36));
                addSlotToContainer(new Slot(tileEntity, TileBasicRFStorage.CHARGE_SLOT_3, 148, 54));
                break;
        }
        bindPlayerInventory(inventory, 8, 84);
    }
}
