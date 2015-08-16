package com.dyonovan.neotech.pipes.world;

import com.dyonovan.neotech.network.PacketDispatcher;
import com.dyonovan.neotech.network.RenderPipeResourcePacket;
import com.dyonovan.neotech.pipes.network.ResourceEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Iterator;
import java.util.LinkedList;
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

    public static final WeakHashMap<World, LinkedList<ResourceEntity>> networks = new WeakHashMap<>();
    public float renderTickTime;

    public static void register() {
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    /**
     * Used to add a resource to the tick list
     * @param world The world the resource is in
     * @param resource The resource to add
     */
    public void addResourceToWorld(World world, ResourceEntity resource) {
        if(networks.containsKey(world)) {
            networks.get(world).add(resource);
        } else {
            LinkedList<ResourceEntity> list = new LinkedList<>();
            list.add(resource);
            networks.put(world, list);
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event) {
        if(networks.isEmpty())
            return;
        synchronized (networks) {
            LinkedList<ResourceEntity> resources = networks.get(event.world);
            if (resources != null) {
                Iterator<ResourceEntity> networkIterator = resources.iterator();
                while (networkIterator.hasNext()) {
                    ResourceEntity resource = networkIterator.next();
                    if (resource == null)
                        networkIterator.remove();
                    else {
                        if (event.phase == TickEvent.Phase.END) {
                            resource.updateEntity();
                            PacketDispatcher.net.sendToAllAround(new RenderPipeResourcePacket(resource),
                                    new NetworkRegistry.TargetPoint(event.world.provider.getDimensionId(),
                                            resource.xPos, resource.yPos, resource.zPos, 100));
                        } else {
                            if (resource.isDead) {
                                resource.onDropInWorld();
                                networkIterator.remove();
                            }
                        }
                    }
                }
            }
        }
    }



    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void tick(TickEvent.RenderTickEvent event) {
       renderTickTime = event.renderTickTime;
    }
}
