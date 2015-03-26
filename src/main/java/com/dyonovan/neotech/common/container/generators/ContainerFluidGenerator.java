package com.dyonovan.neotech.common.container.generators;

import com.dyonovan.neotech.common.container.BaseContainer;
import com.dyonovan.neotech.common.tileentity.generator.TileFluidGenerator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;

public class ContainerFluidGenerator extends BaseContainer {
    public ContainerFluidGenerator(InventoryPlayer inventory, TileFluidGenerator tileEntity) {
        addSlotToContainer(new Slot(tileEntity, TileFluidGenerator.BUCKET_IN, 114, 18));
        addSlotToContainer(new SlotFurnaceOutput(inventory.player, tileEntity, TileFluidGenerator.BUCKET_OUT, 114, 54));
        bindPlayerInventory(inventory, 8, 84);
    }
}
