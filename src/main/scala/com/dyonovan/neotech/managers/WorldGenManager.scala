package com.dyonovan.neotech.managers

import java.util.Random

import com.dyonovan.neotech.registries.ConfigRegistry
import net.minecraft.util.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.feature.WorldGenMinable
import net.minecraftforge.fml.common.IWorldGenerator

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 13, 2015
 */
object WorldGenManager extends IWorldGenerator {

    override def generate(random: Random, chunkX: Int, chunkZ: Int, world: World, chunkGenerator: IChunkProvider,
                          chunkProvider: IChunkProvider): Unit = {
        world.provider.getDimensionId match {
            case 0 =>
                generateOre(world, random, chunkX * 16, chunkZ * 16);
            case _ =>
        }
    }

    def generateOre(world: World, random: Random, chunkX: Int, chunkZ: Int): Unit = {
        //Copper
        if (ConfigRegistry.genCopper) {
            for (i <- 0 until ConfigRegistry.copperPerChunk) {
                val x = chunkX + random.nextInt(16)
                val y = random.nextInt(ConfigRegistry.copperMax - ConfigRegistry.copperMin) + ConfigRegistry.copperMin
                val z = chunkZ + random.nextInt(16)
                val pos = new BlockPos(x, y, z)

                new WorldGenMinable(BlockManager.oreCopper.getDefaultState, ConfigRegistry.copperSize).generate(world,
                    random, pos)
            }
        }
        //Tin
        if (ConfigRegistry.genTin) {
            for (i <- 0 until ConfigRegistry.tinPerChunk) {
                val x = chunkX + random.nextInt(16)
                val y = random.nextInt(ConfigRegistry.tinMax - ConfigRegistry.tinMin) + ConfigRegistry.tinMin
                val z = chunkZ + random.nextInt(16)
                val pos = new BlockPos(x, y, z)

                new WorldGenMinable(BlockManager.oreTin.getDefaultState, ConfigRegistry.tinSize).generate(world,
                    random, pos)
            }
        }
    }
}
