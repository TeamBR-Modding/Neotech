package com.dyonovan.neotech.client.renderers

import com.dyonovan.neotech.pipes.entities.ResourceEntity
import com.dyonovan.neotech.pipes.types.ExtractionPipe
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
class ExtractionPipeRenderer[T, R <: ResourceEntity[T], P <: ExtractionPipe[T, R]] extends TileEntitySpecialRenderer {
    override def renderTileEntityAt(tile : TileEntity, posX: Double, posY: Double, posZ : Double, partialTick : Float, integer : Int): Unit = {
        val resourceList = tile.asInstanceOf[P].resources
        for(i <- 0 until resourceList.size()) {
            val resource = resourceList.get(i)
            resource.renderResource(partialTick)
        }
    }
}
