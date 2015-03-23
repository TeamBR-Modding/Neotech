package com.dyonovan.jatm.common.container.storage;

import com.dyonovan.jatm.common.container.BaseContainer;
import com.dyonovan.jatm.common.tileentity.storage.IRFStorage;
import com.dyonovan.jatm.common.tileentity.storage.TileAdvancedRFStorage;
import com.dyonovan.jatm.common.tileentity.storage.TileBasicRFStorage;
import com.dyonovan.jatm.common.tileentity.storage.TileEliteRFStorage;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ContainerRFStorage extends BaseContainer {

    public ContainerRFStorage(InventoryPlayer inventory, IRFStorage tile) {

        switch (tile.getTier()) {
            case 1:
                TileBasicRFStorage tileEntityBasic = (TileBasicRFStorage) inventory.player.getEntityWorld().getTileEntity(tile.getTilePos());
                addSlotToContainer(new Slot(tileEntityBasic, TileBasicRFStorage.CHARGE_SLOT_1, 148, 18));
                break;
            case 2:
                TileAdvancedRFStorage tileEntityAdv = (TileAdvancedRFStorage) inventory.player.getEntityWorld().getTileEntity(tile.getTilePos());
                addSlotToContainer(new Slot(tileEntityAdv, TileBasicRFStorage.CHARGE_SLOT_1, 148, 18));
                addSlotToContainer(new Slot(tileEntityAdv, TileBasicRFStorage.CHARGE_SLOT_2, 148, 36));
                break;
            case 3:
                TileEliteRFStorage tileEntityElite = (TileEliteRFStorage) inventory.player.getEntityWorld().getTileEntity(tile.getTilePos());
                addSlotToContainer(new Slot(tileEntityElite, TileBasicRFStorage.CHARGE_SLOT_1, 148, 18));
                addSlotToContainer(new Slot(tileEntityElite, TileBasicRFStorage.CHARGE_SLOT_2, 148, 36));
                addSlotToContainer(new Slot(tileEntityElite, TileBasicRFStorage.CHARGE_SLOT_3, 148, 54));
                break;
        }
        bindPlayerInventory(inventory, 8, 84);
    }
}
