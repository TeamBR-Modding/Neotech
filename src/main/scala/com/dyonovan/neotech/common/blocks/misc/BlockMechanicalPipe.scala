package com.dyonovan.neotech.common.blocks.misc

import java.util.Random

import com.dyonovan.neotech.common.tiles.machines.operators.TilePump
import com.dyonovan.neotech.lib.Reference
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.world.{IBlockAccess, World}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/8/2016
  */
class BlockMechanicalPipe(name: String) extends Block(Material.rock) {

    setUnlocalizedName(Reference.MOD_ID + ":" + name)
    //setCreativeTab(NeoTech.tabNeoTech)
    setHardness(3.0F)

    def getName: String = name

    lazy val BB = new AxisAlignedBB(4F / 16F, 0F, 4F / 16F, 12F / 16F, 1F, 12F / 16F)

    override def isOpaqueCube(state : IBlockState) = false
    override def isFullCube(state : IBlockState) = false

    override def getItemDropped(state: IBlockState, rand: Random, fortune: Int) : Item = null

    override def onNeighborBlockChange(world: World, pos: BlockPos, state: IBlockState, block: Block): Unit = {
        if (!world.isRemote) {
            world.getTileEntity(pos.offset(EnumFacing.UP)) match {
                case pump : TilePump => return //We have something to attach to
                case _ =>
            }
            world.getBlockState(pos.offset(EnumFacing.UP)).getBlock match {
                case blockPipe : BlockMechanicalPipe => return //We have a brother to attach to
                case _ => world.setBlockToAir(pos) //Break ourselves
            }
        }
    }

    override def getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB = {
        BB
    }
}