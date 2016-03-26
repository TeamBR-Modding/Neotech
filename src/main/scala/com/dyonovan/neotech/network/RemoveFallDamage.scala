package com.dyonovan.neotech.network

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 3/25/2016
  */
import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}

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
class RemoveFallDamage extends IMessage with IMessageHandler[RemoveFallDamage, IMessage] {

    override def toBytes(buf: ByteBuf): Unit = {}

    override def fromBytes(buf: ByteBuf): Unit = {}

    override def onMessage(message: RemoveFallDamage, ctx: MessageContext): IMessage = {
        if(ctx.side.isServer)
            ctx.getServerHandler.playerEntity.fallDistance = 0
        null
    }
}
