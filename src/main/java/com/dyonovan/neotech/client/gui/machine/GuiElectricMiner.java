package com.dyonovan.neotech.client.gui.machine;

import com.dyonovan.neotech.common.container.machine.ContainerElectricMiner;
import com.dyonovan.neotech.common.tileentity.machine.TileElectricMiner;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiElectricMiner extends GuiContainer {
    public GuiElectricMiner(InventoryPlayer inventory, TileElectricMiner tileEntity) {
        super(new ContainerElectricMiner(inventory, tileEntity));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

    }
}
