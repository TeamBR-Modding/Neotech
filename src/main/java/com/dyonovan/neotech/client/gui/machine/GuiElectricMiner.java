package com.dyonovan.neotech.client.gui.machine;

import com.dyonovan.neotech.common.container.machine.ContainerElectricMiner;
import com.dyonovan.neotech.common.tileentity.machine.TileElectricMiner;
import com.dyonovan.neotech.helpers.GuiHelper;
import com.dyonovan.neotech.lib.Constants;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiElectricMiner extends GuiContainer {

    private TileElectricMiner tile;
    private ResourceLocation background = new ResourceLocation(Constants.MODID + ":textures/gui/electricMiner.png");

    public GuiElectricMiner(InventoryPlayer inventory, TileElectricMiner tileEntity) {
        super(new ContainerElectricMiner(inventory, tileEntity));

        this.tile = tileEntity;
        ySize = 177;
    }

    @Override
    public void initGui() {
        super.initGui();

        drawButtons(true, false, false);
    }

    @SuppressWarnings("unchecked")
    private void drawButtons(boolean scan, boolean start, boolean stop) {

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        GuiButton btnScan = new GuiButton(0, x + 37, y + 72, 30, 20, StatCollector.translateToLocal("title.neotech:scan.name"));
        btnScan.enabled = scan;
        GuiButton btnStart = new GuiButton(1, x + 74, y + 72, 30, 20, StatCollector.translateToLocal("title.neotech:start.name"));
        btnStart.enabled = start;
        GuiButton btnStop = new GuiButton(2, x + 109, y + 72, 30, 20, StatCollector.translateToLocal("title.neotech:stop.name"));
        btnStop.enabled = stop;

        buttonList.clear();
        buttonList.add(btnScan);
        buttonList.add(btnStart);
        buttonList.add(btnStop);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        final String invTitle = StatCollector.translateToLocal("tile.neotech:electricMiner.name");
        fontRendererObj.drawString(invTitle, (((ySize + 10) - fontRendererObj.getStringWidth(invTitle)) / 2), 6, 4210752);

        GL11.glPushMatrix();
        GL11.glScalef(.8F, .8F, 1F);

        final String isRunningTitle = StatCollector.translateToLocal("title.neotech:isRunning.name") + " ";
        final boolean isRunning = tile.isRunning;
        final String strIsRunning = isRunning ? "True" : "False";
        final GuiHelper.GuiColor runningColor = !isRunning ? GuiHelper.GuiColor.RED : GuiHelper.GuiColor.GREEN;
        fontRendererObj.drawString(isRunningTitle + runningColor + strIsRunning, 50, 25, 16777215);


        final String rfUsage = StatCollector.translateToLocal("title.neotech:rfUsage.name") + " ";
        final int rfActual = TileElectricMiner.RF_TICK;
        final GuiHelper.GuiColor rfColor = rfActual > TileElectricMiner.RF_TICK ? GuiHelper.GuiColor.RED : GuiHelper.GuiColor.GREEN;
        fontRendererObj.drawString(rfUsage + rfColor + Integer.toString(rfActual), 50, 35, 16777215);

        final String waitTime = StatCollector.translateToLocal("title.neotech:waitTime.name") + " ";
        final int waitActual = TileElectricMiner.DEFAULT_SPEED;
        final GuiHelper.GuiColor waitColor = waitActual > TileElectricMiner.DEFAULT_SPEED ? GuiHelper.GuiColor.RED : GuiHelper.GuiColor.GREEN;
        fontRendererObj.drawString(waitTime + waitColor + Integer.toString(waitActual), 50, 45, 16777215);

        final String currentSize = StatCollector.translateToLocal("title.neotech:currentSize.name") + " ";
        final int sizeActual = TileElectricMiner.DEFAULT_SIZE;
        fontRendererObj.drawString(currentSize + Integer.toString(sizeActual) + "x" + Integer.toString(sizeActual), 50, 55, 16777215);

        final String totalBlocks = StatCollector.translateToLocal("title.neotech:totalBlocks.name") + " ";
        final int totalBlocksActual = tile.getTotalBlocks();
        final GuiHelper.GuiColor totalBlocksColor = totalBlocksActual > 0 ? GuiHelper.GuiColor.GREEN : GuiHelper.GuiColor.RED;
        fontRendererObj.drawString(totalBlocks + totalBlocksColor + Integer.toString(totalBlocksActual), 50, 65, 16777215);

        final String blocksLeft =  StatCollector.translateToLocal("title.neotech:blocksLeft.name") + " ";
        final int blocksLeftActual = totalBlocksActual - tile.numBlock;
        final GuiHelper.GuiColor blocksLeftColor = blocksLeftActual > 0 ? GuiHelper.GuiColor.GREEN : GuiHelper.GuiColor.RED;
        fontRendererObj.drawString(blocksLeft + blocksLeftColor + Integer.toString(blocksLeftActual), 50, 75, 16777215);


        GL11.glPopMatrix();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        tile = (TileElectricMiner) mc.theWorld.getTileEntity(tile.getPos());

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}
