package com.dyonovan.neotech.pipes.network;

import net.minecraft.world.World;

import java.util.LinkedHashSet;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 15, 2015
 *
 * Used to hold a list of networks
 */
public class WorldNetworkList {
    public World world;
    public WorldNetworkList(World worldObj) {
        world = worldObj;
    }
    public LinkedHashSet<PipeNetwork> activeNetworks = new LinkedHashSet<>();
}
