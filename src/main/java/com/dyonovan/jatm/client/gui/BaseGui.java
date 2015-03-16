package com.dyonovan.jatm.client.gui;

import com.dyonovan.jatm.client.gui.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BaseGui extends GuiContainer {

    protected List<Widget> widgets;
    protected List<Zone> toolTips;
    protected Rectangle arrowLoc;
    protected Container parent;
    Minecraft minecraft = Minecraft.getMinecraft();
    public BaseGui(Container c) {
        super(c);
        this.parent = c;
        widgets = new ArrayList<Widget>();
        toolTips = new ArrayList<Zone>();
        arrowLoc = new Rectangle(0, 0, 0, 0);
    }
    public void setArrowLocation(int x, int y, int width, int height) {
        arrowLoc = new Rectangle(x, y, width, height);
    }
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        for(Widget widget : widgets) {
            widget.render(guiLeft, guiTop);
        }
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        super.drawScreen(mouseX, mouseY, par3);
        for(Zone zone : toolTips) {
            if(zone.isWithin(mouseX, mouseY))
                renderToolTip(mouseX, mouseY, zone.value);
        }
        /*if(mouseX >= guiLeft + arrowLoc.x && mouseX <= guiLeft + arrowLoc.x + arrowLoc.width &&
                mouseY >= guiTop + arrowLoc.y && mouseY <= guiTop + arrowLoc.y + arrowLoc.height &&
                TeamBRCore.nei != null) {
            renderToolTip(mouseX, mouseY, "Recipes");
        }*/
    }
    /*@Override
    protected void mouseClicked(int mouseX, int mouseY, int button)
    {
        *//*if(mouseX >= guiLeft + arrowLoc.x && mouseX <= guiLeft + arrowLoc.x + arrowLoc.width &&
                mouseY >= guiTop + arrowLoc.y && mouseY <= guiTop + arrowLoc.y + arrowLoc.height &&
                TeamBRCore.nei != null) {
            TeamBRCore.nei.onArrowClicked(parent);
        }*//*
        super.mouseClicked(mouseX, mouseY, button);
    }*/
    public void renderToolTip(int x, int y, List<String> strings)
    {
        drawHoveringText(strings, x, y, fontRendererObj);
    }
    public void renderToolTip(int x, int y, String string)
    {
        List<String> list = new ArrayList<String>();
        list.add(string);
        drawHoveringText(list, x, y, fontRendererObj);
    }
    /**
     * Sometimes you need to get the gui measurements before minecraft updates them. This will force an update
     */
    protected void updateScale()
    {
        GL11.glViewport(0, 0, this.minecraft.displayWidth, this.minecraft.displayHeight);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        this.width = this.minecraft.displayWidth;
        this.height = this.minecraft.displayHeight;
        ScaledResolution scaledresolution = new ScaledResolution(this.minecraft, this.minecraft.displayWidth, this.minecraft.displayHeight);
        this.width = scaledresolution.getScaledWidth();
        this.height = scaledresolution.getScaledHeight();
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, (double) this.width, (double) this.height, 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
    }
    public class Zone {
        int x;
        int y;
        int width;
        int height;
        List<String> value;
        public Zone(int i, int j, int w, int h, List<String> v) {
            x = i;
            y = j;
            width = w;
            height = h;
            value = v;
        }
        public boolean isWithin(int mx, int my) {
            return mx >= x && mx <= (x + width) && my <= (y + height) && my >= y;
        }
    }
}
