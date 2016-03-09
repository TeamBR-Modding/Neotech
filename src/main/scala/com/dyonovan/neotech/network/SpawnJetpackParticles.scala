package com.dyonovan.neotech.network

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumParticleTypes
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint
import net.minecraftforge.fml.common.network.simpleimpl.{MessageContext, IMessageHandler, IMessage}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 3/6/2016
  */
class SpawnJetpackParticles extends IMessage with IMessageHandler[SpawnJetpackParticles, IMessage] {

    var x = 0.0
    var y = -255.0
    var z = 0.0
    var dimId = 0

    def this(player : EntityPlayer) {
        this()
        x = player.posX
        y = player.posY
        z = player.posZ
        dimId = player.worldObj.provider.getDimensionId
    }

    override def toBytes(buf: ByteBuf): Unit = {
        buf.writeDouble(x)
        buf.writeDouble(y)
        buf.writeDouble(z)
        buf.writeInt(dimId)
    }

    override def fromBytes(buf: ByteBuf): Unit = {
        x = buf.readDouble()
        y = buf.readDouble()
        z = buf.readDouble()
        dimId = buf.readInt()
    }

    override def onMessage(message: SpawnJetpackParticles, ctx: MessageContext): IMessage = {
        if(ctx.side.isServer)
            PacketDispatcher.net.sendToAllAround(message,
                new TargetPoint(message.dimId, message.x, message.y, message.z, 100))
        else {
            val r = 0.2
            for(t <- 1 until 3) {
                for (i <- 0 until (360 / 20)) {
                    val x = message.x + Math.cos(Math.toRadians(i * 20)) * r * t * 2
                    val z = message.z + Math.sin(Math.toRadians(i * 20)) * r * t * 2
                    Minecraft.getMinecraft.theWorld.spawnParticle(EnumParticleTypes.FLAME, x, message.y, z, 0, -1 + (t * 0.4), 0)
                }
            }
        }
        null
    }
}