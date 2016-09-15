package com.teambrmodding.neotech.common.blocks.machines

import com.teambr.bookshelf.common.blocks.traits.DropsItems
import com.teambr.bookshelf.traits.HasToolTip
import com.teambrmodding.neotech.common.tiles.machines.operators.TileBreaker
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
  * @since 9/4/2016
  */
class BlockBreaker extends BlockMachine("breaker", classOf[TileBreaker], sixWayRotation = true) with DropsItems with HasToolTip {

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        world.getTileEntity(new BlockPos(x, y, z)) match {
            case _ => null
        }
    }

    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        world.getTileEntity(new BlockPos(x, y, z)) match {
            case _ => null
        }
    }

    override def getToolTip(): List[String] = {
        List[String]("Places blocks into the world")
    }
}
