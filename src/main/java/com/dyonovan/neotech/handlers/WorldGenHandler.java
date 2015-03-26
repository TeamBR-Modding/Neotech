package com.dyonovan.neotech.handlers;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class WorldGenHandler implements IWorldGenerator {

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        switch (world.provider.getDimensionId()) {
            case 0:
                generateOre(world, random, chunkX * 16, chunkZ * 16);
                break;
            default:
                break;
        }
    }

    private void generateOre (World world, Random random, int chunkX, int chunkZ) {

        //Copper
        if (ConfigHandler.genCopper) {
            for (int i = 0; i < ConfigHandler.copperPerChunk; i++) {
                int x = chunkX + random.nextInt(16);
                int y = random.nextInt(ConfigHandler.copperMax - ConfigHandler.copperMin) + ConfigHandler.copperMin;
                int z = chunkZ + random.nextInt(16);
                BlockPos pos = new BlockPos(x, y, z);

                new WorldGenMinable(BlockHandler.oreCopper.getDefaultState(), ConfigHandler.copperSize).generate(world, random, pos);
            }
        }

        //Tin
        if (ConfigHandler.genTin) {
            for (int i = 0; i < ConfigHandler.tinPerChunk; i++) {
                int x = chunkX + random.nextInt(16);
                int y = random.nextInt(ConfigHandler.tinMax - ConfigHandler.tinMin) + ConfigHandler.tinMin;
                int z = chunkZ + random.nextInt(16);
                BlockPos pos = new BlockPos(x, y, z);

                new WorldGenMinable(BlockHandler.oreTin.getDefaultState(), ConfigHandler.tinSize).generate(world, random, pos);
            }
        }
    }
}
