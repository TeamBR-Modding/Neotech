package com.dyonovan.neotech.pipes.tiles

import com.dyonovan.neotech.pipes.network.{PipeInformation, ResourceEntity}
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
trait IPipe extends TileEntity {
    def canConnect(facing: EnumFacing): Boolean = getWorld.getTileEntity(getPos.offset(facing)).isInstanceOf[IPipe]

    def onResourceEnteredPipe(resource: ResourceEntity[_]): Unit = {}

    def getInformation: PipeInformation = {
        val connections = Array[Boolean](false, false, false, false, false, false)
        for(dir <- EnumFacing.values()) {
            getWorld.getTileEntity(getPos.offset(dir)) match {
                case otherPipe: IPipe =>
                    connections(dir.ordinal()) = true
                case _ =>
            }
        }
        new PipeInformation(connections, canAcceptResource(null))
    }

    def canAcceptResource(resource: ResourceEntity[_]): Boolean = false

    def getPosAsLong: Long = getPos.toLong
}
