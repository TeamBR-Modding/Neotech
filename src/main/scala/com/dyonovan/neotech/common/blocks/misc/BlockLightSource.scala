package com.dyonovan.neotech.common.blocks.misc

import java.util.Random

import com.dyonovan.neotech.lib.Reference
import net.minecraft.block.BlockAir
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
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
class BlockLightSource extends BlockAir {
    setUnlocalizedName(Reference.MOD_ID + ":lightSource")
    setLightLevel(1.0F)


    @SideOnly(Side.CLIENT)
    override def randomDisplayTick(world: World, pos: BlockPos, state: IBlockState, rand: Random): Unit = {
       /* val xPos = pos.getX + 0.5
        val yPos = pos.getY + 0.5
        val zPos = pos.getZ + 0.5
        val rand1 = -0.2D + (0.2D - (-0.2D)) * rand.nextDouble()
        val rand2 = -0.2D + (0.2D - (-0.2D)) * rand.nextDouble()

        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, xPos + rand1 + rand1, yPos, zPos + rand2, 0.0D, 0.03D, 0.0D)
        world.spawnParticle(EnumParticleTypes.FLAME, xPos + rand1, yPos + rand1, zPos + rand2, 0.0D, 0.03D, 0.0D)
        world.spawnParticle(EnumParticleTypes.FLAME, xPos, yPos, zPos, 0.0D, 0.0D, 0.0D)*/
    }
}
