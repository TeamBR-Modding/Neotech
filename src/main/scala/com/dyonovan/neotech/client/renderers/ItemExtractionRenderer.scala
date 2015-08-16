package com.dyonovan.neotech.client.renderers

import com.dyonovan.neotech.pipes.tiles.ItemExtractionPipe
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 16, 2015
 */
class ItemExtractionRenderer extends TileEntitySpecialRenderer {
    override def renderTileEntityAt(tile : TileEntity, posX: Double, posY: Double, posZ : Double, partialTick : Float, integer : Int): Unit = {
        val resourceList = tile.asInstanceOf[ItemExtractionPipe].resources
        for(i <- 0 until resourceList.size()) {
            val item = resourceList.get(i)
            item.renderResource(partialTick)
        }
    }
}
