package com.dyonovan.jatm.client.gui.storage;

import com.dyonovan.jatm.common.container.storage.ContainerRFStorage;
import com.dyonovan.jatm.common.tileentity.storage.TileBasicRFStorage;
import com.dyonovan.jatm.helpers.GuiHelper;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiRFStorage extends GuiContainer {

    private TileBasicRFStorage tile;
    private ResourceLocation background = new ResourceLocation(Constants.MODID + ":textures/gui/rfStorage.png");

    public GuiRFStorage(InventoryPlayer inventory, TileBasicRFStorage tileEntity) {
        super(new ContainerRFStorage(inventory, tileEntity));
        this.tile = tileEntity;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        String invTitle = "";
        switch (tile.getSizeInventory()) {
            case 1:
                invTitle =  StatCollector.translateToLocal("tile.jatm:basicRFStorage.name");
                break;
            case 2:
                invTitle =  StatCollector.translateToLocal("tile.jatm:advancedRFStorage.name");
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
        switch (tile.getSizeInventory()) {
            case 1:
                drawTexturedModalRect(x + 148, y + 36, 176, 52, 16, 16);
                drawTexturedModalRect(x + 148, y + 54, 176, 52, 16, 16);
                break;
            case 2:
                drawTexturedModalRect(x + 148, y + 54, 176, 52, 16, 16);
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        super.drawScreen(mouseX, mouseY, par3);

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        if (GuiHelper.isInBounds(mouseX, mouseY, x + 12, y + 18, x + 28, y + 70)) {
            List<String> toolTip = new ArrayList<>();
            toolTip.add(GuiHelper.GuiColor.YELLOW + "Energy");
            toolTip.add(tile.getEnergyStored(null) + "/" + tile.getMaxEnergyStored(null) + GuiHelper.GuiColor.RED + "RF");
            drawHoveringText(toolTip, mouseX, mouseY);
        }
    }
}
