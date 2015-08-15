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

    /**
     * Used to tell the resource moving through it how much to speed up when passing through.
     *
     * Keep in mind, the speed is equal to how many blocks to offset. Unless you want it moving tons of blocks at once
     * don't go over 1
     * @return
     */
    def getSpeedupValue : Double = 0
}
