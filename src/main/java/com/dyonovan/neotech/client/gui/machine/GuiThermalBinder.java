package com.dyonovan.neotech.client.gui.machine;

import com.dyonovan.neotech.common.container.machine.ContainerThermalBinder;
import com.dyonovan.neotech.common.tileentity.machine.TileThermalBinder;
import com.dyonovan.neotech.handlers.PacketHandler;
import com.dyonovan.neotech.helpers.GuiHelper;
import com.dyonovan.neotech.lib.Constants;
import com.dyonovan.neotech.network.ThermalBinderPacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiThermalBinder extends GuiContainer {

    private TileThermalBinder tile;
    private ResourceLocation background = new ResourceLocation(Constants.MODID + ":textures/gui/thermalbinder.png");

    public GuiThermalBinder(InventoryPlayer inventory, TileThermalBinder tileEntity) {
        super(new ContainerThermalBinder(inventory, tileEntity));
        ySize = 193;
        tile = tileEntity;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, x + 64, y + 85, 63, 20, StatCollector.translateToLocal("button.neotech:buttonStart.name")));

        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            if (tile.getStackInSlot(TileThermalBinder.MB_SLOT_INPUT) == null) return;
            if (tile.getStackInSlot(TileThermalBinder.INPUT_SLOT_1) == null &&
                    tile.getStackInSlot(TileThermalBinder.INPUT_SLOT_2) == null &&
                    tile.getStackInSlot(TileThermalBinder.INPUT_SLOT_3) == null &&
                    tile.getStackInSlot(TileThermalBinder.INPUT_SLOT_4) == null) return;

            PacketHandler.net.sendToServer(new ThermalBinderPacket.StartMessage(tile.getPos()));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        final String invTitle = StatCollector.translateToLocal("tile.neotech:thermalBinder.name");
        fontRendererObj.drawString(invTitle, (((ySize + 10) - fontRendererObj.getStringWidth(invTitle)) / 2), 7, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 5, ySize - 93, 4210752);
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
        worldrenderer.addVertexWithUV(x + 18, y + 77, 0, 0.6875F, 0.26953125F);
        worldrenderer.addVertexWithUV(x + 34, y + 77, 0, 0.75F, 0.26953125F);
        worldrenderer.addVertexWithUV(x + 34, y + 77 - heightRF, 0, 0.75F, (float) (69 - heightRF) / 256);
        worldrenderer.addVertexWithUV(x + 18, y + 77 - heightRF, 0, 0.6875F, (float) (69 - heightRF) / 256);
        tessellator.draw();

        //Draw Progress Arrow
        int arrow = tile.currentProcessTime != 0 ? tile.currentProcessTime * 23 / TileThermalBinder.BASE_PROCESS_TIME : 0;
        this.drawTexturedModalRect(x + 133, y + 24, 176, 69, arrow > 15 ? 15 : arrow, 4);
        this.drawTexturedModalRect(x + 133, y + 76, 176, 89, arrow > 15 ? 15 : arrow, 4);
        if (arrow > 15) {
            this.drawTexturedModalRect(x + 143, y + 28, 186, 73, 7, arrow - 15);
            this.drawTexturedModalRect(x + 143, y + 76, 186, 81 + (8 - (arrow - 15)), 7, (arrow - 15));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        super.drawScreen(mouseX, mouseY, par3);

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        if (GuiHelper.isInBounds(mouseX, mouseY, x + 18, y + 25, x + 34, y + 77)) {
            List<String> toolTip = new ArrayList<>();
            toolTip.add(GuiHelper.GuiColor.YELLOW + "Energy");
            toolTip.add(tile.getEnergyStored(null) + "/" + tile.getMaxEnergyStored(null) + GuiHelper.GuiColor.RED + "RF");
            drawHoveringText(toolTip, mouseX, mouseY);
        }


    }
}
