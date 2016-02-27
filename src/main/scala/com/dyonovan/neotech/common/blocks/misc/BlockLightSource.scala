package com.dyonovan.neotech.common.blocks.misc

import java.util.Random

import com.dyonovan.neotech.lib.Reference
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraft.util.{EnumWorldBlockLayer, AxisAlignedBB, EnumParticleTypes, BlockPos}
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 2/27/2016
  */
class BlockLightSource extends Block(Material.plants) {
    setUnlocalizedName(Reference.MOD_ID + ":lightSource")
    setLightLevel(1.0F)
    setBlockBounds(0.3F, 0.3F, 0.3F, 0.7F, 0.7F, 0.7F)

    @SideOnly(Side.CLIENT)
    override def randomDisplayTick(world: World, pos: BlockPos, state: IBlockState, rand: Random): Unit = {
        val xPos = pos.getX + 0.5
        val yPos = pos.getY + 0.5
        val zPos = pos.getZ + 0.5
        val rand1 = -0.2D + (0.2D - (-0.2D)) * rand.nextDouble()
        val rand2 = -0.2D + (0.2D - (-0.2D)) * rand.nextDouble()

        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, xPos + rand1 + rand1, yPos, zPos + rand2, 0.0D, 0.03D, 0.0D)
        world.spawnParticle(EnumParticleTypes.FLAME, xPos + rand1, yPos + rand1, zPos + rand2, 0.0D, 0.03D, 0.0D)
        world.spawnParticle(EnumParticleTypes.FLAME, xPos, yPos, zPos, 0.0D, 0.0D, 0.0D)
    }

    override def isOpaqueCube = false
    override def getItemDropped(state: IBlockState, rand: Random, fortune: Int) : Item = null
    override def getCollisionBoundingBox(worldIn: World, pos: BlockPos, state: IBlockState) : AxisAlignedBB = null
    override def canRenderInLayer(layer: EnumWorldBlockLayer) : Boolean = layer == EnumWorldBlockLayer.TRANSLUCENT
}
