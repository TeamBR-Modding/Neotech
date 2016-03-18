package com.dyonovan.neotech.common.blocks.machines

import com.dyonovan.neotech.common.tiles.AbstractMachine
import com.dyonovan.neotech.common.tiles.machines.generators.TileSolarPanel
import net.minecraft.tileentity.TileEntity
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
  * @since 2/3/2016
  */
class BlockSolarPanel(name: String, tier: Int) extends BlockMachine(name, classOf[TileSolarPanel], fourWayRotation = false) {
    setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F)

    override def isFullCube : Boolean = false
    def getTier: Int = tier

    override def createNewTileEntity(worldIn: World, meta: Int): TileEntity = new TileSolarPanel(tier)

    override def hasComparatorInputOverride: Boolean =
        true

    override def getComparatorInputOverride(worldIn: World, pos: BlockPos): Int =
        worldIn.getTileEntity(pos).asInstanceOf[AbstractMachine].getRedstoneOutput
}
