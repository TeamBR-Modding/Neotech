package com.dyonovan.jatm.client.gui.machine;

import com.dyonovan.jatm.common.container.machine.ContainerElectricCrusher;
import com.dyonovan.jatm.common.container.machine.ContainerElectricFurnace;
import com.dyonovan.jatm.common.tileentity.machine.TileElectricCrusher;
import com.dyonovan.jatm.common.tileentity.machine.TileElectricFurnace;
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

public class GuiElectricCrusher extends GuiContainer {

    private TileElectricCrusher tile;
    private ResourceLocation background = new ResourceLocation(Constants.MODID + ":textures/gui/electriccrusher.png");

    public GuiElectricCrusher(InventoryPlayer inventory, TileElectricCrusher tileEntity) {
        super(new ContainerElectricCrusher(inventory, tileEntity));

        this.tile = tileEntity;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        final String invTitle = StatCollector.translateToLocal("tile.jatm:electricCrusher.name");
        fontRendererObj.drawString(invTitle, (((ySize + 10) - fontRendererObj.getStringWidth(invTitle)) / 2), 6, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 5, ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
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
        worldrenderer.addVertexWithUV(x + 18, y + 68, 0, 0.6875F, 0.26953125F);
        worldrenderer.addVertexWithUV(x + 34, y + 68, 0, 0.75F, 0.26953125F);
        worldrenderer.addVertexWithUV(x + 34, y + 68 - heightRF, 0, 0.75F, (float) (69 - heightRF) / 256);
        worldrenderer.addVertexWithUV(x + 18, y + 68 - heightRF, 0, 0.6875F, (float) (69 - heightRF) / 256);
        tessellator.draw();

        //Progress Arrow
        int arrow = tile.currentProcessTime != 0 ? tile.currentProcessTime * 24 / TileElectricFurnace.TOTAL_PROCESS_TIME : 0;
        this.drawTexturedModalRect(x + 79, y + 34, 176, 0, arrow + 1, 16);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        super.drawScreen(mouseX, mouseY, par3);

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        if (GuiHelper.isInBounds(mouseX, mouseY, x + 18, y + 16, x + 34, y + 68)) {
            List<String> toolTip = new ArrayList<>();
            toolTip.add(GuiHelper.GuiColor.YELLOW + "Energy");
            toolTip.add(tile.getEnergyStored(null) + "/" + tile.getMaxEnergyStored(null) + GuiHelper.GuiColor.RED + "RF");
            drawHoveringText(toolTip, mouseX, mouseY);
        }
    }
}
