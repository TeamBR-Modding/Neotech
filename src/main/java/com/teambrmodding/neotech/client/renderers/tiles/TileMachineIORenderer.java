package com.teambrmodding.neotech.client.renderers.tiles;

import com.teambrmodding.neotech.common.tiles.AbstractMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/11/2017
 */
public class TileMachineIORenderer extends TileRenderHelper<AbstractMachine> {
    @Override
    public void renderTileEntityAt(AbstractMachine te, double x, double y, double z, float partialTicks, int destroyStage) {
        try {
            if (te != null && te.shouldRenderInputOutputOnTile()) {
                for (EnumFacing dir : EnumFacing.values()) {
                    String iconName = te.getDisplayIconForSide(dir);
                    if (iconName != null) {
                        renderIconOnBlock(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(iconName),
                                0, dir, dir, new LocationDouble(x, y, z), 16.0F, 0.0F, 0.0F, -0.001);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace(); // Seems we need a one tick catch for MC timing issues
        }
    }
}
