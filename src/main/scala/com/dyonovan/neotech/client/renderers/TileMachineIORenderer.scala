package com.dyonovan.neotech.client.renderers

import com.dyonovan.neotech.common.tiles.AbstractMachine
import com.teambr.bookshelf.common.blocks.properties.PropertyRotation
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.EnumFaceDirection
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
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
        if (te.shouldRenderInputOutputOnTile) {
            val facing = te.getWorld.getBlockState(te.getPos).getValue(PropertyRotation.FOUR_WAY)
            for (dir <- EnumFacing.values()) {
                if (te.canInputFromSide(dir, facing) && te.canOutputFromSide(dir, facing))
                    renderIconOnBlock(Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite("neotech:blocks/inputOutputFace"),
                        0, te.getDirFromFacing(dir, facing), te.getDirFromFacing(dir, facing), new LocationDouble(x, y, z), 16.0F, 0.0, 0.0, -0.001)
                else if (!te.canInputFromSide(dir, facing) && te.canOutputFromSide(dir, facing))
                    renderIconOnBlock(Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite("neotech:blocks/outputFace"),
                        0, te.getDirFromFacing(dir, facing), te.getDirFromFacing(dir, facing), new LocationDouble(x, y, z), 16.0F, 0.0F, 0.0F, -0.001)
                else if (te.canInputFromSide(dir, facing) && !te.canOutputFromSide(dir, facing))
                    renderIconOnBlock(Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite("neotech:blocks/inputFace"),
                        0, te.getDirFromFacing(dir, facing), te.getDirFromFacing(dir, facing), new LocationDouble(x, y, z), 16.0F, 0.0, 0.0, -0.001)
            }
        }
    }
}
