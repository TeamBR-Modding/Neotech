package com.dyonovan.neotech.client.gui.machine;

import com.dyonovan.neotech.common.container.machine.ContainerElectricFurnace;
import com.dyonovan.neotech.common.tileentity.BaseMachine;
import com.dyonovan.neotech.common.tileentity.machine.TileElectricCrusher;
import com.dyonovan.neotech.common.tileentity.machine.TileElectricFurnace;
import com.dyonovan.neotech.helpers.GuiHelper;
import com.dyonovan.neotech.lib.Constants;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiElectricFurnace extends GuiContainer {

    private TileElectricFurnace tile;
    private ResourceLocation background = new ResourceLocation(Constants.MODID + ":textures/gui/electricfurnace.png");

    public GuiElectricFurnace(InventoryPlayer inventory, TileElectricFurnace tileEntity) {
        super(new ContainerElectricFurnace(inventory, tileEntity));

        this.tile = tileEntity;
        ySize = 181;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        final String invTitle = StatCollector.translateToLocal("tile.neotech:electricFurnace.name");
        fontRendererObj.drawString(invTitle, (((ySize + 10) - fontRendererObj.getStringWidth(invTitle)) / 2), 6, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 5, ySize - 96 + 2, 4210752);

        tile = (TileElectricFurnace) mc.theWorld.getTileEntity(tile.getPos());

        GL11.glPushMatrix();
        GL11.glScalef(.8F, .8F, 1F);

        final String rfUsage = StatCollector.translateToLocal("title.neotech:rfUsage.name") + " ";
        final int rfActual = tile.findEff(TileElectricFurnace.RF_TICK,
                tile.getField(BaseMachine.SPEED), tile.getField(BaseMachine.EFFICIENCY));
        final GuiHelper.GuiColor rfColor = rfActual > TileElectricFurnace.RF_TICK ? GuiHelper.GuiColor.RED : GuiHelper.GuiColor.GREEN;
        fontRendererObj.drawString(rfUsage + rfColor + Integer.toString(rfActual), 75, 70, 16777215);

        final String processTime = StatCollector.translateToLocal("title.neotech:processTime.name") + " ";
        final int speedActual = tile.findSpeed(TileElectricFurnace.TOTAL_PROCESS_TIME, tile.getField(BaseMachine.SPEED));
        final GuiHelper.GuiColor speedColor = speedActual > TileElectricFurnace.TOTAL_PROCESS_TIME ? GuiHelper.GuiColor.RED : GuiHelper.GuiColor.GREEN;
                fontRendererObj.drawString(processTime + speedColor + Integer.toString(speedActual), 75, 80, 16777215);

        final String autoOutput = StatCollector.translateToLocal("title.neotech:autoOutput.name") + " ";
        final String outputActual = tile.getField(BaseMachine.IO) == 1 ? GuiHelper.GuiColor.GREEN + "True" :
                GuiHelper.GuiColor.RED + "False";
        fontRendererObj.drawString(autoOutput + outputActual, 75, 90, 16777215);
        GL11.glScalef(1F, 1F, 1F);
        GL11.glPopMatrix();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        //tile = (TileElectricFurnace) mc.theWorld.getTileEntity(tile.getPos());

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
        this.drawTexturedModalRect(x + 81, y + 27, 176, 0, arrow + 1, 16);
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
