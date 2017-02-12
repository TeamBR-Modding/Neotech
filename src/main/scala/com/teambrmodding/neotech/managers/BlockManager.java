package com.teambrmodding.neotech.managers;

import com.teambrmodding.neotech.common.blocks.machines.BlockMachine;
import com.teambrmodding.neotech.common.tiles.machines.processors.TileElectricFurnace;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/11/2017
 */
public class BlockManager {
    // Machines
    public static BlockMachine electricFurnace = new BlockMachine("electricFurnace", TileElectricFurnace.class);

}
