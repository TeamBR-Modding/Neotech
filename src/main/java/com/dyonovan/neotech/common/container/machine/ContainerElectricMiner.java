package com.dyonovan.neotech.common.container.machine;

import com.dyonovan.neotech.common.container.BaseContainer;
import com.dyonovan.neotech.common.container.SlotUpgrade;
import com.dyonovan.neotech.common.tileentity.machine.TileElectricMiner;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerElectricMiner extends BaseContainer {

    public ContainerElectricMiner(InventoryPlayer inventory, TileElectricMiner tileEntity) {

        addSlotToContainer(new SlotUpgrade(tileEntity, TileElectricMiner.UPGRADE_SLOT, 153, 7));
        bindPlayerInventory(inventory, 8, 95);
    }
}
