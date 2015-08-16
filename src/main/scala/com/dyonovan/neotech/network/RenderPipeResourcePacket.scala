package com.dyonovan.neotech.network

import com.dyonovan.neotech.pipes.network.{ItemResourceEntity, ResourceEntity}
import com.dyonovan.neotech.pipes.world.WorldTicker
import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import net.minecraftforge.fml.relauncher.Side

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 15, 2015
 */
object RenderPipeResourcePacket {
    val ITEM_ID = 0
    val FLUID_ID = 1
    val ENERGY_ID = 2
}

class RenderPipeResourcePacket extends IMessage with IMessageHandler[RenderPipeResourcePacket, IMessage] {

    var resource : ResourceEntity[_] = null

    def this(r : ResourceEntity[_]) {
        this()
        resource = r
    }

    override def toBytes(buf: ByteBuf): Unit = {
        if(resource.isInstanceOf[ItemResourceEntity])
            buf.writeInt(RenderPipeResourcePacket.ITEM_ID)
        resource.toBytes(buf)
    }

    override def fromBytes(buf: ByteBuf): Unit = {
        buf.readInt() match {
            case RenderPipeResourcePacket.ITEM_ID =>
                resource = new ItemResourceEntity().fromBytes(buf).asInstanceOf[ItemResourceEntity]
            case _ =>
        }
    }

    override def onMessage(message: RenderPipeResourcePacket, ctx: MessageContext): IMessage = {
        if(ctx.side == Side.CLIENT) {
            if(message.resource != null) {
                message.resource.renderResource(WorldTicker.INSTANCE.renderTickTime)
            }
        }
        null
    }
}
