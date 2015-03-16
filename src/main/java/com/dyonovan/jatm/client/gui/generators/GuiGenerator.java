package com.dyonovan.jatm.client.gui.generators;

import com.dyonovan.jatm.common.container.generators.ContainerGenerator;
import com.dyonovan.jatm.common.tileentity.generator.TileGenerator;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiGenerator extends GuiContainer {

    private TileGenerator tile;
    private ResourceLocation background = new ResourceLocation(Constants.MODID + ":textures/gui/generator.png");

    public GuiGenerator(InventoryPlayer inventoryPlayer, TileGenerator tileGenerator) {
        super(new ContainerGenerator(inventoryPlayer, tileGenerator));

        this.tile = tileGenerator;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        final String invTitle = "RF Generator";
        fontRendererObj.drawString(invTitle, (((ySize + 10) - fontRendererObj.getStringWidth(invTitle)) / 2), 6, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 05, ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        //Render RF energy
        int heightRF = tile.energyRF.getEnergyStored() * 52 / tile.energyRF.getMaxEnergyStored();
        Tessellator tessRF = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessRF.getWorldRenderer();
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertexWithUV(x + 62, y + 34, 50, (float) (176 / 256), (float) (30 / 256));
        worldRenderer.addVertexWithUV(x + 114 - heightRF, y + 34, 50, (float) ((228 - heightRF) / 256), (float) (30 / 256));
        worldRenderer.addVertexWithUV(x + 114 - heightRF, y + 18, 50, (float) ((228 - heightRF) / 256), (float) (14 / 256));
        worldRenderer.addVertexWithUV(x + 62, y + 18, 50, (float) (176 / 256), (float) (14 / 256));
        tessRF.draw();
        GL11.glPopMatrix();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        super.drawScreen(mouseX, mouseY, par3);

    }
}
