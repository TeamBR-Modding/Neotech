package com.dyonovan.neotech.notification;

import com.dyonovan.neotech.NeoTech;
import com.teambr.bookshelf.client.gui.GuiColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 17, 2015
 */
@SideOnly(Side.CLIENT)
public class GuiNotificationConfig extends GuiScreen {
    private Minecraft minecraft;
    private int xPos;
    private static final ResourceLocation backGround = new ResourceLocation("textures/gui/achievement/achievement_background.png");

    public GuiNotificationConfig()
    {
        minecraft = Minecraft.getMinecraft();
        xPos = NeoTech.notificationXPos();
    }

    @Override
    public void initGui() {
        updateScale();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2, 60, 20, "Left"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 30, this.height / 2, 60, 20, "Center"));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 40, this.height / 2, 60, 20, "Right"));
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        switch(button.id) {
            case 0 :
                xPos = 0;
                break;
            case 1 :
                xPos = 1;
                break;
            case 2 :
                xPos = 2;
                break;
        }
    }
    @Override
    public void drawScreen(int x, int y, float z) {
        super.drawScreen(x, y, z);
        updateScale();
        minecraft.getTextureManager().bindTexture(backGround);
        int i;
        switch(xPos) {
            case 0 :
                i = 0;
                break;
            case 1 :
                i = (this.width / 2) - 80;
                break;
            case 2 :
                i = this.width - 160;
                break;
            default :
                i = 0;
        }
        this.drawTexturedModalRect(i, 0, 96, 202, 160, 32);
        this.minecraft.fontRendererObj.drawSplitString(GuiColor.WHITE + "Display Position for Notifications", i + 4, 8, 150, 0);
    }

    @Override
    public void onGuiClosed() {
        set("notifications", "notification xpos", xPos);
    }

    public static void set(String categoryName, String propertyName, int newValue) {

        NeoTech.notificationConfig().load();
        if (NeoTech.notificationConfig().getCategoryNames().contains(categoryName)) {
            if (NeoTech.notificationConfig().getCategory(categoryName).containsKey(propertyName)) {
                NeoTech.notificationConfig().getCategory(categoryName).get(propertyName).set(newValue);
            }
        }
        NeoTech.notificationConfig().save();
        reloadValues();
    }

    public static void reloadValues() {
        NeoTech.notificationXPos_$eq(NeoTech.notificationConfig().getInt("notification xpos", "notifications", 1, 0, 2, "0: Left   1: Center   2: Right"));
    }

    private void updateScale() {
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
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, (double)this.width, (double)this.height, 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
    }
}

