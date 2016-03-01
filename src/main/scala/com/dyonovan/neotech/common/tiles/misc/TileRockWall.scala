package com.dyonovan.neotech.common.tiles.misc

import com.teambr.bookshelf.common.tiles.traits.UpdatingTile
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.AxisAlignedBB

import scala.collection.JavaConversions._

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 2/28/2016
  */
class TileRockWall extends UpdatingTile {

    override def update() : Unit = {
        val players = worldObj.getEntitiesWithinAABB(classOf[EntityPlayer], AxisAlignedBB.fromBounds(pos.getX - 0.5, pos.getY - 0.5, pos.getZ - 0.5,
            pos.getX + 1.5, pos.getY + 1.5, pos.getZ + 1.5))
        if(!players.isEmpty) {
            for(player <- players) {
                if(player.isSneaking) {
                    player.motionY = 0
                    player.fallDistance = 0.0F
                }
            }
        }
    }
}
