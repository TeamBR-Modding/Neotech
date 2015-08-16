package com.dyonovan.neotech.pipes.tiles

import java.lang.Long

import com.dyonovan.neotech.pipes.network.{PipeInformation, ResourceEntity}
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
 * @since August 15, 2015
 */
class BasePipe extends UpdatingTile with IPipe {
    override def canConnect(facing: EnumFacing): Boolean = worldObj.getTileEntity(pos.offset(facing)).isInstanceOf[IPipe]

    override def onResourceEnteredPipe(resource: ResourceEntity[_]): Unit = {
        resource.nextSpeed += 0.0
    }

    override def getInformation: PipeInformation = {
        val connections = Array[Boolean](false, false, false, false, false, false)
        for(dir <- EnumFacing.values()) {
            worldObj.getTileEntity(pos.offset(dir)) match {
                case otherPipe: IPipe =>
                    connections(dir.ordinal()) = true
                case _ =>
            }
        }
        new PipeInformation(connections, canAcceptResource(null))
    }

    override def canAcceptResource(resource: ResourceEntity[_]): Boolean = true

    override def getPosAsLong: Long = pos.toLong
}
