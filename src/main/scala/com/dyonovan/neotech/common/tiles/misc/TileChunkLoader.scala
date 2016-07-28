package com.dyonovan.neotech.common.tiles.misc

import java.util.UUID

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.registries.ConfigRegistry
import com.teambr.bookshelf.common.tiles.traits.Syncable
import net.minecraft.util.math.ChunkPos
import net.minecraft.nbt.NBTTagCompound
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
    var chunkTicket: Ticket = null
    var diameter = 0
    var owner: UUID = _

    override def invalidate() = {
        if (chunkTicket != null)
            ForgeChunkManager.releaseTicket(chunkTicket)
        super.invalidate()
    }

    override def onServerTick() = {
        if (ConfigRegistry.onlineOnly && (worldObj.getPlayerEntityByUUID(owner) == null || owner == null)) {
            if (chunkTicket != null) {
                ForgeChunkManager.releaseTicket(chunkTicket)
                chunkTicket = null
            }
        } else {
            if (chunkTicket == null) {
                chunkTicket = ForgeChunkManager.requestTicket(NeoTech, worldObj, Type.NORMAL)
                if(chunkTicket != null) {
                    chunkTicket.getModData.setInteger("neotech.loaderX", pos.getX)
                    chunkTicket.getModData.setInteger("neotech.loaderY", pos.getY)
                    chunkTicket.getModData.setInteger("neotech.loaderZ", pos.getZ)
                    forceChunkLoading(chunkTicket)
                }
            }
        }
    }

    def forceChunkLoading(ticket: Ticket): Unit = {
        if (chunkTicket == null)
            chunkTicket = ticket

        //Load Us
        ForgeChunkManager.forceChunk(ticket, new ChunkPos(pos.getX >> 4, pos.getY >> 4))

        for (i <- (pos.getX >> 4) - diameter to (pos.getX >> 4) + diameter) {
            for (j <- (pos.getZ >> 4) - diameter to (pos.getZ >> 4) + diameter) {
                val chunk = new ChunkPos(i, j)
                ForgeChunkManager.forceChunk(ticket, chunk)
            }
        }
    }

    override def readFromNBT(tag: NBTTagCompound) = {
        super.readFromNBT(tag)
        diameter = tag.getInteger("Diameter")
        if (tag.hasKey("Owner"))
            owner = UUID.fromString(tag.getString("Owner"))
    }

    override def writeToNBT(tag: NBTTagCompound): NBTTagCompound = {
        super.writeToNBT(tag)
        tag.setInteger("Diameter", diameter)
        if (owner != null)
            tag.setString("Owner", owner.toString)
        tag
    }

    override def setVariable(id: Int, value: Double): Unit = {
        diameter = value.toInt
        if (diameter > ConfigRegistry.chunkLoaderMax)
            diameter = ConfigRegistry.chunkLoaderMax
        else if (diameter < 0)
            diameter = 0
        worldObj.setBlockState(pos, worldObj.getBlockState(pos), 6)
    }

    override def getVariable(id: Int): Double = {
        0.0
    }

    def setOwner(entity: UUID): Unit = owner = entity
}
