package com.dyonovan.neotech.pipes.tiles

import com.teambr.bookshelf.common.tiles.traits.UpdatingTile
import net.minecraft.util.EnumFacing

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 14, 2015
 */
class TilePipe extends UpdatingTile {
    /**
     * Can this connect in this direction
     * @param face The facing direction
     */
    def canConnect(face : EnumFacing) : Boolean = {
        worldObj.getTileEntity(pos.offset(face)).isInstanceOf[TilePipe]
    }
}
