package com.dyonovan.jatm.client.gui.machine;

import com.dyonovan.jatm.common.container.machine.ContainerCrafter;
import com.dyonovan.jatm.common.tileentity.machine.TileEntityCrafter;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiCrafter extends GuiContainer {
    private ResourceLocation background = new ResourceLocation(Constants.MODID + ":textures/gui/crafter.png");

    public GuiCrafter(InventoryPlayer playerInv, TileEntityCrafter crafter) {
        super(new ContainerCrafter(playerInv, crafter));
        xSize = 255;
        ySize = 165;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        this.fontRendererObj.drawString(StatCollector.translateToLocal("jatm.container.crafter"), 167 - (fontRendererObj.getStringWidth(StatCollector.translateToLocal("jatm.container.crafter")) / 2), 5, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}
