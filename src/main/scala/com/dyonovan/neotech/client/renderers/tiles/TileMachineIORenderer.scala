package com.dyonovan.neotech.client.renderers.tiles

import com.dyonovan.neotech.common.tiles.AbstractMachine
import net.minecraft.client.Minecraft
import net.minecraft.util.EnumFacing

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/2/2016
  */
class TileMachineIORenderer extends TileRenderHelper[AbstractMachine] {
    override def renderTileEntityAt(te: AbstractMachine, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int): Unit = {
        try {
            if (te != null && te.shouldRenderInputOutputOnTile && te.getWorld.getBlockState(te.getPos) != null) {
                for (dir <- EnumFacing.values()) {
                    val iconName = te.getDisplayIconForSide(dir)
                    if(iconName != null) {
                        renderIconOnBlock(Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(iconName),
                            0, dir, dir, new LocationDouble(x, y, z), 16.0F, 0.0, 0.0, -0.001)
                    }
                }
            }
        } catch {
            case e : IllegalArgumentException => //Weird Minecraft stuff
            case _: Throwable =>
        }
    }
}
