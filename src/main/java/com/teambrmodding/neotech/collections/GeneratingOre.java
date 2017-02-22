package com.teambrmodding.neotech.collections;

import net.minecraft.block.Block;

/**
 * Object to hold information about ores to generate
 */
public class GeneratingOre {
    public int minElevation, maxElevation, veinSize, veinsPerChunk;
    public Block oreBlock;

    /**
     * An object to hold all info about a generating ore
     * @param minElevation  The lower y level
     * @param maxElevation  The highest y level
     * @param veinSize      The vein size
     * @param veinsPerChunk How many veins per chunk
     */
    public GeneratingOre(int minElevation, int maxElevation, int veinSize, int veinsPerChunk, Block oreBlock) {
        this.minElevation = minElevation;
        this.maxElevation = maxElevation;
        this.veinSize = veinSize;
        this.veinsPerChunk = veinsPerChunk;
        this.oreBlock = oreBlock;
    }
}
