package com.dyonovan.neotech.helpers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.FMLClientHandler;

public class GuiHelper {

    /**
     * Test if location is in bounds
     * @param x xLocation
     * @param y yLocation
     * @param a Rectangle point a
     * @param b Rectangle point b
     * @param c Rectangle point c
     * @param d Rectangle point d
     * @return boolean
     */
    public static boolean isInBounds(int x, int y, int a, int b, int c, int d)
    {
        return (x >= a && x <= c && y >= b && y <=d);
    }
    
    public static void drawFluid(FluidStack fluid, int x, int y, float zLevel, int width, int height, int maxCapacity) 
    {
        if (fluid == null || fluid.getFluid() == null)
            return;
        TextureAtlasSprite sprite = fluid.getFluid().getIcon(fluid);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(0x302, 0x303);
        int rgb = fluid.getFluid().getColor(fluid);
        float r = (rgb >> 16 & 255) / 255.0F;
        float g = (rgb >> 8 & 255) / 255.0F;
        float b = (rgb & 255) / 255.0F;
        GlStateManager.color(r, g, b, 1);
        int fullX = width / 16;
        int fullY = height / 16;
        int lastX = width - fullX * 16;
        int lastY = height - fullY * 16;
        int level = fluid.amount * height / maxCapacity;
        int fullLvl = (height - level) / 16;
        int lastLvl = (height - level) - fullLvl * 16;
        for (int i = 0; i < fullX; i++) {
            for (int j = 0; j < fullY; j++) {
                if (j >= fullLvl)
                    drawCutIcon(sprite, x + i * 16, y + j * 16, zLevel, 16, 16, j == fullLvl ? lastLvl : 0);
            }
        }
        for (int i = 0; i < fullX; i++) {
            drawCutIcon(sprite, x + i * 16, y + fullY * 16, zLevel, 16, lastY, fullLvl == fullY ? lastLvl : 0);
        }
        for (int i = 0; i < fullY; i++) {
            if (i >= fullLvl)
                drawCutIcon(sprite, x + fullX * 16, y + i * 16, zLevel, lastX, 16, i == fullLvl ? lastLvl : 0);
        }
        drawCutIcon(sprite, x + fullX * 16, y + fullY * 16, zLevel, lastX, lastY, fullLvl == fullY ? lastLvl : 0);
    }

    public static void drawCutIcon(TextureAtlasSprite sprite, int x, int y, float zLevel, int width, int height, int cut) 
    {
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer renderer = tess.getWorldRenderer();
        renderer.startDrawingQuads();
        renderer.addVertexWithUV(x, y + height, zLevel, sprite.getMinU(), sprite.getInterpolatedV(height));
        renderer.addVertexWithUV(x + width, y + height, zLevel, sprite.getInterpolatedU(width), sprite.getInterpolatedV(height));
        renderer.addVertexWithUV(x + width, y + cut, zLevel, sprite.getInterpolatedU(width), sprite.getInterpolatedV(cut));
        renderer.addVertexWithUV(x, y + cut, zLevel, sprite.getMinU(), sprite.getInterpolatedV(cut));
        tess.draw();
    }

    public enum GuiColor {
        BLACK(0),
        BLUE(1),
        GREEN(2),
        CYAN(3),
        RED(4),
        PURPLE(5),
        ORANGE(6),
        LIGHTGRAY(7),
        GRAY(8),
        LIGHTBLUE(9),
        LIME(10),
        TURQUISE(11),
        PINK(12),
        MAGENTA(13),
        YELLOW(14),
        WHITE(15);

        private int number;
        GuiColor(int number) {
            this.number = number;
        }

        @Override
        public String toString() {
            return "\u00a7" + Integer.toHexString(number);
        }
    }
}
