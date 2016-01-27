package com.dyonovan.neotech.common.blocks.misc

import com.dyonovan.neotech.common.blocks.BaseBlock
import com.dyonovan.neotech.common.tiles.misc.TileRedstoneClock
import com.teambr.bookshelf.common.container.ContainerGeneric
import com.teambr.bookshelf.common.tiles.traits.OpensGui
import net.minecraft.block.BlockPressurePlate
import net.minecraft.block.material.Material
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.{IBlockAccess, World}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/26/2016
  */
class BlockRedstoneClock extends BaseBlock(Material.rock, "redstoneClock", classOf[TileRedstoneClock]) {

    this.setDefaultState(this.blockState.getBaseState.withProperty(BlockPressurePlate.POWERED, false.asInstanceOf[java.lang.Boolean]))

    override def getRenderType: Int = 3

    override def isNormalCube(world: IBlockAccess, pos: BlockPos) = true

    def setRedstoneStrength(state: IBlockState, strength: Int): IBlockState =
        state.withProperty(BlockPressurePlate.POWERED, (strength > 0).asInstanceOf[java.lang.Boolean])

    def getRedstoneStrength(state: IBlockState): Int =
        if(state.getValue(BlockPressurePlate.POWERED).asInstanceOf[Boolean]) 15 else 0

    def updateState (worldIn: World, pos: BlockPos, state: IBlockState, toMax : Boolean) : Unit = {
        val i: Int = if(toMax) 15 else 0
        val flag: Boolean = i > 0
        worldIn.setBlockState(pos, this.setRedstoneStrength(state, i), 2)
        this.updateNeighbors(worldIn, pos)
    }

    /**
      * Notify block and block below of changes
      */
    protected def updateNeighbors(worldIn: World, pos: BlockPos) {
        worldIn.notifyNeighborsOfStateChange(pos, this)
        worldIn.notifyNeighborsOfStateChange(pos.down, this)
    }

    override def getWeakPower(worldIn: IBlockAccess, pos: BlockPos, state: IBlockState, side: EnumFacing): Int = {
        this.getRedstoneStrength(state)
    }

    override def getStrongPower(worldIn: IBlockAccess, pos: BlockPos, state: IBlockState, side: EnumFacing): Int = {
        this.getRedstoneStrength(state)
    }

    /**
      * Can this block provide power. Only wire currently seems to have this change based on its state.
      */
    override def canProvidePower: Boolean = {
        true
    }

    override def getStateFromMeta (meta: Int) : IBlockState = {
        this.getDefaultState.withProperty(BlockPressurePlate.POWERED, (meta == 1).asInstanceOf[java.lang.Boolean])
    }
    /**
      * Convert the BlockState into the correct metadata value
      */
    override def getMetaFromState(state : IBlockState) : Int = {
        if(state.getValue(BlockPressurePlate.POWERED).asInstanceOf[Boolean]) 1 else 0
    }

    override def createBlockState() : BlockState = {
        new BlockState(this, BlockPressurePlate.POWERED)
    }
}
