package com.dyonovan.neotech.chunkloader

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

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
class ChunkList() {

    var player: EntityPlayer = _
    var location: BlockPos = _
    var world: World = _

    def this(player: EntityPlayer, location: BlockPos, world: World) {
        this()
        this.player = player
        this.location = location
        this.world = world
    }
}
