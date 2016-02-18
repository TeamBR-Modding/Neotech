package com.dyonovan.neotech.world;

import com.dyonovan.neotech.managers.BlockManager;
import com.dyonovan.neotech.managers.MetalManager;
import com.dyonovan.neotech.registries.ConfigRegistry;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

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
public class NeotechWorldGenerator implements IWorldGenerator {

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider iChunkProvider, IChunkProvider iChunkProvider1) {
        switch (world.provider.getDimensionId()) {
            case 0:
                generateOre(world, random, chunkX * 16, chunkZ * 16);
        }
    }

    private void generateOre(World world, Random random, int chunkX, int chunkZ) {
        if (ConfigRegistry.genCopper() && MetalManager.getMetal("copper").isDefined() &&
                MetalManager.getMetal("copper").get().oreBlock().isDefined()) {
            for (int i = 0; i < ConfigRegistry.copperPerChunk(); i++) {
                int x = chunkX + random.nextInt(16);
                int y = random.nextInt(ConfigRegistry.copperMax() - ConfigRegistry.copperMin()) + ConfigRegistry.copperMin();
                int z = chunkZ + random.nextInt(16);
                BlockPos pos = new BlockPos(x, y, z);

                new WorldGenMinable(MetalManager.getMetal("copper").get().oreBlock().get().getDefaultState(), ConfigRegistry.copperSize()).generate(world,
                        random, pos);
            }
        }

        if (ConfigRegistry.genTin() && MetalManager.getMetal("tin").isDefined() &&
                MetalManager.getMetal("tin").get().oreBlock().isDefined()) {
            for (int i = 0; i < ConfigRegistry.tinPerChunk(); i++) {
                int x = chunkX + random.nextInt(16);
                int y = random.nextInt(ConfigRegistry.tinMax() - ConfigRegistry.tinMin()) + ConfigRegistry.tinMin();
                int z = chunkZ + random.nextInt(16);
                BlockPos pos = new BlockPos(x, y, z);

                new WorldGenMinable(MetalManager.getMetal("tin").get().oreBlock().get().getDefaultState(), ConfigRegistry.tinSize()).generate(world,
                        random, pos);
            }
        }

        if (ConfigRegistry.genLead() && MetalManager.getMetal("lead").isDefined() &&
                MetalManager.getMetal("lead").get().oreBlock().isDefined()) {
            for (int i = 0; i < ConfigRegistry.leadPerChunk(); i++) {
                int x = chunkX + random.nextInt(16);
                int y = random.nextInt(ConfigRegistry.leadMax() - ConfigRegistry.leadMin()) + ConfigRegistry.leadMin();
                int z = chunkZ + random.nextInt(16);
                BlockPos pos = new BlockPos(x, y, z);

                new WorldGenMinable(MetalManager.getMetal("lead").get().oreBlock().get().getDefaultState(), ConfigRegistry.leadSize()).generate(world,
                        random, pos);
            }
        }
    }
}
