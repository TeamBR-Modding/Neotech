package com.dyonovan.neotech.pipes.world;

import com.dyonovan.neotech.pipes.network.AbstractNetwork;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.WeakHashMap;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 15, 2015
 */
public class WorldTicker {
    public static WorldTicker INSTANCE = new WorldTicker();

    public static final WeakHashMap<World, AbstractNetwork> networks = new WeakHashMap<>();

    public static void register() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    /**
     * Used to add a network to the tick list
     * @param world The world the network is in
     * @param network The network to add
     */
    public void addNetwork(World world, AbstractNetwork network) {
        networks.put(world, network);
    }

    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event) {
        if(networks.isEmpty())
            return;
        synchronized (networks) {
            AbstractNetwork localNetwork = networks.get(event.world);
        }
    }
}
