package com.dyonovan.neotech.common.blocks

import com.dyonovan.neotech.common.tiles.TileChunkLoader
import net.minecraft.block.material.Material

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
class BlockChunkLoader extends BaseBlock(Material.rock, "chunkLoader", classOf[TileChunkLoader]) {
    setHardness(1.5F)

    override def getRenderType: Int = 3
}
