package com.dyonovan.neotech.pipes.types

import com.dyonovan.neotech.pipes.entities.ResourceEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 16, 2015
 *
 * This is the base pipe and interface for all pipes. Every pipe should extend this
 */
trait SimplePipe extends TileEntity {
    /**
     * Used as a simple check to see if the pipe can connect. At it's most basic, it just checks if the tile in that
     * direction is a pipe. This is mainly used for path finding but also on the renderer
     * @param facing The direction from this block
     * @return
     */
    def canConnect(facing: EnumFacing): Boolean = getWorld.getTileEntity(getPos.offset(facing)).isInstanceOf[SimplePipe]

    /**
     * Sometimes we need to know if the connection is more than just a pipe. Usually an inventory of some sort.
     * This is used primarily on the renderer to render the block on the pipe
     * @param facing The direction from this block
     * @return
     */
    def isSpecialConnection(facing : EnumFacing) : Boolean = !getWorld.getTileEntity(getPos.offset(facing)).isInstanceOf[SimplePipe]

    /**
     * Called when a resource enters this pipe. You can do cool stuff here. The special pipes use it to insert and send
     * back while the upgraded pipes apply a speed update.
     *
     * NOTE: If you are applying a speed update, either use the helper method or set nextSpeed. The resource will update to
     * the next speed
     * @param resource
     */
    def onResourceEnteredPipe(resource: ResourceEntity[_]): Unit = {}

    /**
     * Convert the position to a long format
     * @return
     */
    def getPosAsLong: Long = getPos.toLong

    /**
     * Called when this pipe is broken
     */
    def onPipeBroken() : Unit = {}
}
