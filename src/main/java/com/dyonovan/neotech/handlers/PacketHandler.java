package com.dyonovan.neotech.handlers;

import com.dyonovan.neotech.lib.Constants;
import com.dyonovan.neotech.network.ThermalBinderPacket;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

    public static SimpleNetworkWrapper net;
    private static int nextPacketId = 0;

    public static void initPackets() {
        net = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MODID.toUpperCase());

        registerMessage(ThermalBinderPacket.class, ThermalBinderPacket.StartMessage.class);
    }

    @SuppressWarnings("unchecked")
    private static void registerMessage(Class packet, Class message)
    {
        net.registerMessage(packet, message, nextPacketId, Side.CLIENT);
        net.registerMessage(packet, message, nextPacketId, Side.SERVER);
        nextPacketId++;
    }
}
