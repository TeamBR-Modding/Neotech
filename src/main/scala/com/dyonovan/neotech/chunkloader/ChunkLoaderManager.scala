package com.dyonovan.neotech.chunkloader

import java.util

import com.dyonovan.neotech.lib.Reference
import gnu.trove.map.hash.THashMap
import net.minecraft.world.World
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

    lazy val chunkList: THashMap[Integer, ChunkList] = new THashMap[Integer, ChunkList]()

    override def ticketsLoaded(tickets: util.List[Ticket], world: World): Unit = {
        for (ticket <- tickets) {
            if (ticket.getModId.equalsIgnoreCase(Reference.MOD_ID)) {

            }
        }
    }
}
