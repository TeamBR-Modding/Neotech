package com.dyonovan.neotech.common.blocks.machines

import com.dyonovan.neotech.common.blocks.BaseBlock
import com.dyonovan.neotech.common.tiles.machines.TileGrinder
import com.dyonovan.neotech.pipes.blocks.BlockPipe
import net.minecraft.block.material.Material
import net.minecraft.entity.Entity
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.util.BlockPos
import net.minecraft.world.World

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/11/2016
  */
class BlockGrinder extends BaseBlock(Material.rock, "grinder", classOf[TileGrinder]) {
    override def getRenderType : Int = 3

    override def onLanded(world : World, entity : Entity) : Unit = {
        super.onLanded(world, entity)
        if(entity.isInstanceOf[EntityPlayer] && entity.fallDistance > 0.0 &&
                !world.isAirBlock(new BlockPos(entity.posX, entity.posY, entity.posZ))) {
            println(entity.fallDistance)
        }
    }
}
