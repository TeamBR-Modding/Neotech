package com.dyonovan.neotech.pipes.world;

import com.dyonovan.neotech.pipes.network.PipeNetwork;
import com.dyonovan.neotech.pipes.network.WorldNetworkList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Iterator;
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

    public static final WeakHashMap<World, WorldNetworkList> networks = new WeakHashMap<>();

    public static void register() {
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    /**
     * Used to add a network to the tick list
     * @param world The world the network is in
     * @param network The network to add
     */
    public void addNetwork(World world, WorldNetworkList network) {
        networks.put(world, network);
    }

    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event) {
        if(networks.isEmpty())
            return;
        synchronized (networks) {
            WorldNetworkList localNetwork = networks.get(event.world);
            Iterator<PipeNetwork> networkIterator = localNetwork.activeNetworks.iterator();
            while(networkIterator.hasNext()) {
                PipeNetwork network = networkIterator.next();
                if(network == null || network.shouldBeDestroyed())
                    networkIterator.remove();
                else {
                    if(event.phase == TickEvent.Phase.END)
                        network.onTickEnd();
                    else
                        network.onTickStart();
                }
            }
        }
    }
}
