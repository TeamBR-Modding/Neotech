package com.dyonovan.jatm.client.gui.storage;

import com.dyonovan.jatm.common.container.storage.ContainerRFStorage;
import com.dyonovan.jatm.common.tileentity.notmachines.TileRFStorage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiRFStorage extends GuiContainer {

    public GuiRFStorage(InventoryPlayer inventory, TileRFStorage tileEntity) {
        super(new ContainerRFStorage(inventory, tileEntity));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

    }
}
