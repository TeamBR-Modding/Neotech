package com.dyonovan.neotech.common.blocks.storage

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
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
  * @since 2/8/2016
  */
class BlockCreativeDimStorage extends BlockDimStorage("creativeDimStorage") {

    override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, facing: EnumFacing, f1: Float, f2: Float, f3: Float): Boolean = {

        true
    }

    override def onBlockClicked(world: World, pos: BlockPos, player: EntityPlayer): Unit = {

    }

    override def onBlockHarvested(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer): Unit = {

    }

    override def getItemDropped(state: IBlockState, rand: java.util.Random, fortune: Int): Item = {
        Item.getItemFromBlock(this)
    }
}
