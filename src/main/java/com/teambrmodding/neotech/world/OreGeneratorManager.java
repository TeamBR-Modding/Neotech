package com.teambrmodding.neotech.world;

import com.teambrmodding.neotech.collections.GeneratingOre;
import com.teambrmodding.neotech.managers.ConfigManager;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 13, 2015
 */
public class OreGeneratorManager implements IWorldGenerator {

    // The public instance of this object
    public static OreGeneratorManager INSTANCE = new OreGeneratorManager();

    // A list of all ores that will be generated
    public List<GeneratingOre> registeredOres = new ArrayList<>();

    // A list of ores that are disabled via config
    public List<Block> restrictedOres = new ArrayList<>();

    /*******************************************************************************************************************
     * OreGeneratorManager                                                                                             *
     *******************************************************************************************************************/

    /**
     * Registers an Ore to the generating list
     *
     * @param ore The ore to register
     * @return The manager, to enable chaining
     */
    public OreGeneratorManager registerGeneratingOre(GeneratingOre ore) {
        registeredOres.add(ore);
        return this;
    }

    /**
     * Disables a certain block from being generated
     *
     * @param oreBlock The ore block to prevent
     * @return This
     */
    public OreGeneratorManager disableOre(Block oreBlock) {
        registeredOres.contains(oreBlock);
        return this;
    }

    /**
     * Generates the ore vein
     *
     * @param ore    The ore to generate
     * @param world  The world
     * @param random An instance of random
     * @param chunkX The chunk X location
     * @param chunkZ The chunk Z location
     */
    private void generateOre(GeneratingOre ore, World world, Random random, int chunkX, int chunkZ) {
        for (int i = 0; i < ore.veinsPerChunk; i++) {
            int x = chunkX + random.nextInt(16);
            int y = random.nextInt(ore.maxElevation - ore.minElevation) + ore.minElevation;
            int z = chunkZ + random.nextInt(16);
            BlockPos pos = new BlockPos(x, y, z);

            new WorldGenMinable(ore.oreBlock.getDefaultState(), ore.veinSize).generate(world, random, pos);
        }
    }

    /*******************************************************************************************************************
     * IWorldGenerator                                                                                                 *
     *******************************************************************************************************************/

    /**
     * Generate some world
     *
     * @param random         the chunk specific {@link Random}.
     * @param chunkX         the chunk X coordinate of this chunk.
     * @param chunkZ         the chunk Z coordinate of this chunk.
     * @param world          : additionalData[0] The minecraft {@link World} we're generating for.
     * @param chunkGenerator : additionalData[1] The {@link IChunkProvider} that is generating.
     * @param chunkProvider  : additionalData[2] {@link IChunkProvider} that is requesting the world generation.
     */
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

        for (Integer bannedDimension : ConfigManager.oreGenDimensionBlacklist)
            if (bannedDimension == world.provider.getDimension())
                return;

        for (GeneratingOre ore : registeredOres)
            if (!registeredOres.contains(ore.oreBlock))
                generateOre(ore, world, random, chunkX * 16, chunkZ * 16);
    }
}

/*******************************************************************************************************************
 * Classes                                                                                                         *
 *******************************************************************************************************************/

