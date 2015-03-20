package com.dyonovan.jatm.common.container.generators;

import com.dyonovan.jatm.common.container.BaseContainer;
import com.dyonovan.jatm.common.tileentity.generator.TileLavaGenerator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;

public class ContainerLavaGenerator  extends BaseContainer {
    public ContainerLavaGenerator(InventoryPlayer inventory, TileLavaGenerator tileEntity) {
        addSlotToContainer(new Slot(tileEntity, TileLavaGenerator.BUCKET_IN, 80, 53));
        addSlotToContainer(new SlotFurnaceOutput(inventory.player, tileEntity, TileLavaGenerator.BUCKET_OUT, 80, 23));
        bindPlayerInventory(inventory, 8, 84);
    }
}
