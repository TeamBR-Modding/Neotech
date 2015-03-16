package com.dyonovan.jatm.client.gui.generators;

import com.dyonovan.jatm.client.gui.BaseGui;
import com.dyonovan.jatm.common.container.generators.ContainerGenerator;
import com.dyonovan.jatm.common.tileentity.TileGenerator;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiGenerator extends BaseGui {

    private TileGenerator tile;
    private ResourceLocation background = new ResourceLocation(Constants.MODID + ":textures/gui/generator.png");

    public GuiGenerator(InventoryPlayer inventoryPlayer, TileGenerator tileGenerator) {
        super(new ContainerGenerator(inventoryPlayer, tileGenerator));

        this.tile = tileGenerator;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        final String invTitle = "RF Generator";
        fontRendererObj.drawString(invTitle, (fontRendererObj.getStringWidth(invTitle) / 2), 6, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 05, ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F); //Could do some fun colors and transparency here
        this.mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        //Render RF energy
        int heightRF = tile.energyRF.getEnergyStored() * 52 / tile.energyRF.getMaxEnergyStored();

        Tessellator tessRF = Tessellator.getInstance();
        tessRF.getWorldRenderer().startDrawingQuads();
        tessRF.getWorldRenderer().addVertexWithUV(x + 8, y + 78, 0, 0.6875F, 0.35546875F);
        tessRF.getWorldRenderer().addVertexWithUV(x + 24, y + 78, 0, 0.75F, 0.35546875F);
        tessRF.getWorldRenderer().addVertexWithUV(x + 24, y + 78 - heightRF, 0, 0.75F, (float) (91 - heightRF) / 256); //256);
        tessRF.getWorldRenderer().addVertexWithUV(x + 8, y + 78 - heightRF, 0, 0.6875F, (float) (91 - heightRF) / 256);
        tessRF.draw();

        //super.drawGuiContainerBackgroundLayer(f, i, j);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        super.drawScreen(mouseX, mouseY, par3);


    }
}
