package com.dyonovan.neotech.common.blocks

import com.dyonovan.neotech.common.blocks.traits.CoreStates
import com.dyonovan.neotech.common.tiles.AbstractTile
import com.teambr.bookshelf.Bookshelf
import com.teambr.bookshelf.common.blocks.traits.DropsItems
import com.teambr.bookshelf.common.tiles.traits.OpensGui
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.World

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 11, 2015
 */
class BlockMachine(name: String, tileEntity: Class[_ <: TileEntity]) extends BaseBlock(Material.iron, name, tileEntity)
with OpensGui with CoreStates with DropsItems {

    override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
        world.getTileEntity(pos) match {
            case tile: AbstractTile =>
                player.openGui(Bookshelf, 0, world, pos.getX, pos.getY, pos.getZ)
            case _ =>
        }
        true
    }

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = ???

    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = ???
}
