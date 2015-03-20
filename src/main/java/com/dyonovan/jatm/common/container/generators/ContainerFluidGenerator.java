package com.dyonovan.jatm.common.container.generators;

import com.dyonovan.jatm.common.container.BaseContainer;
import com.dyonovan.jatm.common.tileentity.generator.TileFluidGenerator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;

public class ContainerFluidGenerator extends BaseContainer {
    public ContainerFluidGenerator(InventoryPlayer inventory, TileFluidGenerator tileEntity) {
        addSlotToContainer(new Slot(tileEntity, TileFluidGenerator.BUCKET_IN, 80, 53));
        addSlotToContainer(new SlotFurnaceOutput(inventory.player, tileEntity, TileFluidGenerator.BUCKET_OUT, 80, 23));
        bindPlayerInventory(inventory, 8, 84);
    }
}
