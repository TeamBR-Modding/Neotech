package com.dyonovan.neotech.client.gui.generators;

import com.dyonovan.neotech.common.container.generators.ContainerFurnaceGenerator;
import com.dyonovan.neotech.common.tileentity.generator.TileFurnaceGenerator;
import com.dyonovan.neotech.helpers.GuiHelper;
import com.dyonovan.neotech.lib.Constants;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiFurnaceGenerator extends GuiContainer {

    private TileFurnaceGenerator tile;
    private ResourceLocation background = new ResourceLocation(Constants.MODID + ":textures/gui/furnaceGenerator.png");

    public GuiFurnaceGenerator(InventoryPlayer inventoryPlayer, TileFurnaceGenerator tileFurnaceGenerator) {
        super(new ContainerFurnaceGenerator(inventoryPlayer, tileFurnaceGenerator));

        this.tile = tileFurnaceGenerator;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        final String invTitle = StatCollector.translateToLocal("tile.neotech:furnaceGenerator.name");
        fontRendererObj.drawString(invTitle, (((ySize + 10) - fontRendererObj.getStringWidth(invTitle)) / 2), 6, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 5, ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        //RF Energy bar
        int widthRF = tile.energyRF.getEnergyStored() * 52 / tile.energyRF.getMaxEnergyStored();
        drawTexturedModalRect(x + 62, y + 18, 176, 14, widthRF, 16);

        //Buring Bar
        int heightBurn = tile.currentBurnTime == 0 ? 13 : tile.currentBurnTime * 13 / tile.totalBurnTime;
        drawTexturedModalRect(x + 81,   y + 37 + heightBurn,   176,    heightBurn,    14,     14 - heightBurn);

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        super.drawScreen(mouseX, mouseY, par3);

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        if (GuiHelper.isInBounds(mouseX, mouseY, x + 62, y + 18, x + 114, y + 34)) {
            List<String> toolTip = new ArrayList<>();
            toolTip.add(GuiHelper.GuiColor.YELLOW + "Energy");
            toolTip.add(tile.getEnergyStored(null) + "/" + tile.getMaxEnergyStored(null) + GuiHelper.GuiColor.RED + "RF");
            drawHoveringText(toolTip, mouseX, mouseY);
        }
    }
}
