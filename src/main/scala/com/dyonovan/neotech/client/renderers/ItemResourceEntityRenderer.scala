package com.dyonovan.neotech.client.renderers

import com.dyonovan.neotech.pipes.tiles.item.ItemExtractionPipe
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 20, 2015
 */
class ItemResourceEntityRenderer extends TileEntitySpecialRenderer[ItemExtractionPipe] {

    override def renderTileEntityAt(te: ItemExtractionPipe, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int): Unit = {
        val resourceList = te.resources
        for (i <- 0 until resourceList.size()) {
            val resource = resourceList.get(i)
            resource.renderResource(partialTicks)
        }
    }
}
