package com.dyonovan.neotech.common.blocks.machines

import com.dyonovan.neotech.common.tiles.AbstractMachine
import com.dyonovan.neotech.common.tiles.machines.generators.TileSolarPanel
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.world.{IBlockAccess, World}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/3/2016
  */
class BlockSolarPanel(name: String, tier: Int) extends BlockMachine(name, classOf[TileSolarPanel], fourWayRotation = false) {
    override def isFullCube(state: IBlockState) : Boolean = false
    def getTier: Int = tier

    override def createNewTileEntity(worldIn: World, meta: Int): TileEntity = new TileSolarPanel(tier)

    override def hasComparatorInputOverride(state: IBlockState): Boolean =
        true

    override def getComparatorInputOverride(state: IBlockState, worldIn: World, pos: BlockPos): Int =
        worldIn.getTileEntity(pos).asInstanceOf[AbstractMachine].getRedstoneOutput

    override def getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB = {
        BB
    }
}
