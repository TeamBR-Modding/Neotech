package com.dyonovan.jatm.client.gui.widget;

import net.minecraft.client.gui.Gui;

public abstract class Widget {
    protected Gui parent;

    /**
     * How far from the guiLeft
     */
    protected int xPos;

    /**
     * How far down from the guiTop
     */
    protected int yPos;

    /**
     * @param gui Parent Gui
     * @param x   How far from guiLeft
     * @param y   How far down from guiTop
     */
    public Widget(Gui gui, int x, int y) {
        parent = gui;
        xPos = x;
        yPos = y;
    }

    /**
     * Render the widget
     *
     * @param x guiLeft
     * @param y guiTop
     */
    public abstract void render(int x, int y);
}