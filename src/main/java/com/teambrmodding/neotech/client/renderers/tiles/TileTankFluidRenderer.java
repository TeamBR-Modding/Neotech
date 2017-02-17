package com.teambrmodding.neotech.client.renderers.tiles;

import com.teambr.bookshelf.util.RenderUtils;
import com.teambrmodding.neotech.common.tiles.storage.tanks.TileBasicTank;
import com.teambrmodding.neotech.common.tiles.storage.tanks.TileVoidTank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/11/2017
 */
public class TileTankFluidRenderer extends TileEntitySpecialRenderer<TileBasicTank> {
    @Override
    public void renderTileEntityAt(TileBasicTank te, double x, double y, double z, float partialTicks, int destroyStage) {
        if(te.tanks[TileBasicTank.TANK].getFluid() != null || te instanceof TileVoidTank) {
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();

            GlStateManager.translate(x, y, z);

            RenderUtils.bindMinecraftBlockSheet();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableLighting();

            if(!(te instanceof TileVoidTank)) {
                TextureAtlasSprite fluidIcon = Minecraft.getMinecraft().getTextureMapBlocks()
                        .getAtlasSprite(te.tanks[TileBasicTank.TANK].getFluid().getFluid().getStill().toString());
                RenderUtils.setColor(Color.decode(String.valueOf(te.tanks[TileBasicTank.TANK].getFluid().getFluid().getColor())));
                if(te.tanks[TileBasicTank.TANK].getFluid().getFluid().isGaseous())
                    GlStateManager.translate(0, 1 - (te.getFluidLevelScaled() / 16) - 0.1, 0);
                RenderUtils.renderCubeWithTexture(2.01 / 16.0, 1.01 / 16.0, 2.01 / 16.0,
                        13.99 / 16.0, 13.99 / 16.0, 13.99 / 16.0,
                        fluidIcon.getMinU(), fluidIcon.getMinV(), fluidIcon.getMaxU(), fluidIcon.getMaxV());
            } else {
                TextureAtlasSprite voidIcon = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/portal");
                RenderUtils.renderCubeWithTexture(2.01 / 16.0, 1.01 / 16, 2.01 / 16.0, 13.99 / 16.0, 13.99 / 16, 13.99 / 16.0,
                        voidIcon.getMinU(), voidIcon.getMinV(), voidIcon.getMaxU(), voidIcon.getMaxV());
            }
            RenderUtils.restoreColor();
            GlStateManager.enableLighting();

            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        }
    }
}
