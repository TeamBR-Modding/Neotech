package com.dyonovan.jatm.client.gui.storage;

import com.dyonovan.jatm.common.container.storage.ContainerRFStorage;
import com.dyonovan.jatm.common.tileentity.storage.TileRFStorage;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiRFStorage extends GuiContainer {

    private TileRFStorage tile;
    private ResourceLocation background = new ResourceLocation(Constants.MODID + ":textures/gui/rfStorage.png");

    public GuiRFStorage(InventoryPlayer inventory, TileRFStorage tileEntity) {
        super(new ContainerRFStorage(inventory, tileEntity));
        this.tile = tileEntity;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        String invTitle = "";
        switch (tile.tier) {
            case 1:
                invTitle =  StatCollector.translateToLocal("tile.jatm:basicRFStorage.name");
                break;
            case 2:
                invTitle =  StatCollector.translateToLocal("tile.jatm:advRFStorage.name");
                break;
            case 3:
                invTitle =  StatCollector.translateToLocal("tile.jatm:eliteRFStorage.name");
                break;
        }
        fontRendererObj.drawString(invTitle, (((ySize + 10) - fontRendererObj.getStringWidth(invTitle)) / 2), 6, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 5, ySize - 95 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        //RF Energy bar
        int heightRF = tile.getEnergyStored(null) * 52 / tile.getMaxEnergyStored(null);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.startDrawingQuads();
        worldrenderer.addVertexWithUV(x + 12, y + 70, 0, 0.6875F, 0.203125F);
        worldrenderer.addVertexWithUV(x + 28, y + 70, 0, 0.75F, 0.203125F);
        worldrenderer.addVertexWithUV(x + 28, y + 70 - heightRF, 0, 0.75F, (float) (52 - heightRF) / 256);
        worldrenderer.addVertexWithUV(x + 12, y + 70 - heightRF, 0, 0.6875F, (float) (52 - heightRF) / 256);
        tessellator.draw();

        //Disable slots based on tiers
        switch (tile.tier) {
            case 1:
                drawTexturedModalRect(x + 148, y + 36, 176, 52, 16, 16);
                drawTexturedModalRect(x + 148, y + 54, 176, 52, 16, 16);
                break;
            case 2:
                drawTexturedModalRect(x + 148, y + 36, 176, 52, 16, 16);
                break;
        }
    }
}
