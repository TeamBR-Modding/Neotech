package com.dyonovan.neotech.client.renderers

import com.dyonovan.neotech.pipes.tiles.item.ItemInterfacePipe
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraftforge.fml.relauncher.{SideOnly, Side}

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
@SideOnly(Side.CLIENT)
class ItemResourceEntityRenderer extends TileEntitySpecialRenderer[ItemInterfacePipe] {

    override def renderTileEntityAt(te: ItemInterfacePipe, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int): Unit = {
        val resourceList = te.resources
        for (i <- 0 until resourceList.size()) {
            val resource = resourceList.get(i)
            resource.renderResource(partialTicks)
        }
    }
}
