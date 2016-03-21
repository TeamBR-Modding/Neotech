package com.dyonovan.neotech.chunkloader

import java.util

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.registries.ConfigRegistry
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.ForgeChunkManager
import net.minecraftforge.common.ForgeChunkManager.{LoadingCallback, Ticket}

import scala.collection.JavaConversions._

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 3/21/2016
  */
object ChunkLoaderManager extends LoadingCallback {

    override def ticketsLoaded(tickets: util.List[Ticket], world: World): Unit = {
        for (ticket <- tickets) {
            if (ticket.getModId.equalsIgnoreCase(Reference.MOD_ID)) {
                if (!ConfigRegistry.onlineOnly) {
                    val chunkTicket = ForgeChunkManager.requestTicket(NeoTech, ticket.world, ticket.getType)
                    val posX = chunkTicket.getModData.getInteger("neotech.loaderX")
                    val posY = chunkTicket.getModData.getInteger("neotech.loaderY")
                    val posZ = chunkTicket.getModData.getInteger("neotech.loaderZ")
                    val tile = world.getTileEntity(new BlockPos(posX, posY, posZ)).asInstanceOf[TileChunkLoader]
                    tile.forceChunkLoading(ticket)
                } else {
                    //TODO online only
                }
            }
        }
    }

}
