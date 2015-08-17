package com.dyonovan.neotech.pipes.types

import com.dyonovan.neotech.pipes.network.ResourceEntity
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
 */
trait SimplePipe extends TileEntity {
    def canConnect(facing: EnumFacing): Boolean = getWorld.getTileEntity(getPos.offset(facing)).isInstanceOf[SimplePipe]

    def onResourceEnteredPipe(resource: ResourceEntity[_]): Unit = {}

    def getPosAsLong: Long = getPos.toLong

    def onPipeBroken() : Unit = {}
}
