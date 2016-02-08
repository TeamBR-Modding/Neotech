package com.dyonovan.neotech.common.tiles.misc

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.registries.ConfigRegistry
import com.teambr.bookshelf.common.tiles.traits.Syncable
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.ChunkCoordIntPair
import net.minecraftforge.common.ForgeChunkManager
import net.minecraftforge.common.ForgeChunkManager.{Ticket, Type}

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
    var diameter = 0

    override def invalidate() = {
        if(chunkTicket != null)
            ForgeChunkManager.releaseTicket(chunkTicket)
        super.invalidate()
    }

    override def onServerTick() = {
        if(chunkTicket == null)
            chunkTicket = ForgeChunkManager.requestTicket(NeoTech, worldObj, Type.NORMAL)

        if(chunkTicket != null) {
            chunkTicket.getModData.setInteger("neotech.loaderX", pos.getX)
            chunkTicket.getModData.setInteger("neotech.loaderY", pos.getY)
            chunkTicket.getModData.setInteger("neotech.loaderZ", pos.getZ)
            forceChunkLoading(chunkTicket)
        }
    }

    def forceChunkLoading(ticket: Ticket): Unit = {
        if(chunkTicket == null)
            chunkTicket = ticket

        //Load Us
        ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(pos.getX >> 4, pos.getY >> 4))

        for(i <- (pos.getX >> 4) - diameter to (pos.getX >> 4) + diameter) {
            for(j <- (pos.getZ >> 4) - diameter to (pos.getZ >> 4) + diameter) {
                val chunk = new ChunkCoordIntPair(i, j)
                ForgeChunkManager.forceChunk(ticket, chunk)
            }
        }
    }

    override def readFromNBT(tag : NBTTagCompound) = {
        super.readFromNBT(tag)
        diameter = tag.getInteger("Diameter")
    }

    override def writeToNBT(tag : NBTTagCompound) = {
        super.writeToNBT(tag)
        tag.setInteger("Diameter", diameter)
    }

    override def setVariable(id: Int, value: Double): Unit = {
        diameter = value.toInt
        if(diameter > ConfigRegistry.chunkLoaderMax)
            diameter = ConfigRegistry.chunkLoaderMax
        else if(diameter < 0)
            diameter = 0
        worldObj.markBlockForUpdate(pos)
    }

    override def getVariable(id: Int): Double = {0.0}
}
