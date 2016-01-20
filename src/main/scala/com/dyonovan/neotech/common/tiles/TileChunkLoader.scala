package com.dyonovan.neotech.common.tiles

import com.dyonovan.neotech.NeoTech
import com.teambr.bookshelf.common.tiles.traits.Syncable
import net.minecraft.world.ChunkCoordIntPair
import net.minecraftforge.common.ForgeChunkManager
import net.minecraftforge.common.ForgeChunkManager.{Type, Ticket}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/19/2016
  */
class TileChunkLoader extends Syncable {
    var chunkTicket : Ticket = null

    override def invalidate() = {
        if(chunkTicket != null)
            ForgeChunkManager.releaseTicket(chunkTicket)
        super.invalidate()
    }

    var i = 0
    override def onServerTick() = {
        i += 1
        if(chunkTicket == null)
            chunkTicket = ForgeChunkManager.requestTicket(NeoTech, worldObj, Type.NORMAL)

        if(chunkTicket != null) {
            chunkTicket.getModData.setInteger("neotech.loaderX", pos.getX)
            chunkTicket.getModData.setInteger("neotech.loaderY", pos.getY)
            chunkTicket.getModData.setInteger("neotech.loaderZ", pos.getZ)
            ForgeChunkManager.forceChunk(chunkTicket, new ChunkCoordIntPair(pos.getX >> 4, pos.getZ >> 4))
        }
    }

    def forceChunkLoading(ticket: Ticket): Unit = {
        if(chunkTicket == null)
            chunkTicket = ticket
        ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(pos.getX >> 4, pos.getY >> 4))
    }

    override def setVariable(id: Int, value: Double): Unit = {}

    override def getVariable(id: Int): Double = {0.0}
}
