package com.teambrmodding.neotech.network;

import com.teambrmodding.neotech.lib.Reference;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 15, 2015
 */
public class PacketDispatcher {
    // Our network wrapper
    public static SimpleNetworkWrapper net;

    /**
     * Registers all packets
     */
    public static void initPackets() {
        net = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID.toUpperCase());

        registerMessage(OpenContainerGuiPacket.class, OpenContainerGuiPacket.class);
    }

    // Local hold for next packet id
    private static int nextPacketId = 0;

    /**
     * Registers a message to the network registry
     * @param packet The packet class
     * @param message The return packet class
     */
    @SuppressWarnings("unchecked")
    private static void registerMessage(Class packet, Class message) {
        net.registerMessage(packet, message, nextPacketId, Side.CLIENT);
        net.registerMessage(packet, message, nextPacketId, Side.SERVER);
        nextPacketId++;
    }
}
