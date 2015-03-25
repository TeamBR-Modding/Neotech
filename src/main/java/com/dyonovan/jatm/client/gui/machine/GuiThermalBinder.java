package com.dyonovan.jatm.client.gui.machine;

import com.dyonovan.jatm.common.container.machine.ContainerThermalBinder;
import com.dyonovan.jatm.common.tileentity.machine.TileThermalBinder;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiThermalBinder extends GuiContainer {
    public GuiThermalBinder(InventoryPlayer inventory, TileThermalBinder tileEntity) {
        super(new ContainerThermalBinder(inventory, tileEntity));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

    }
}
