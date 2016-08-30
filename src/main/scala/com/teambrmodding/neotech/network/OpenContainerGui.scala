package com.teambrmodding.neotech.network

import com.teambr.bookshelf.Bookshelf
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/15/2016
  */
class OpenContainerGui extends IMessage with IMessageHandler[OpenContainerGui, IMessage] {
    var blockPosition = new BlockPos(0, 0, 0)
    var id = 0

    def this(blockPos : BlockPos, ID : Int) {
        this()
        blockPosition = blockPos
        id = ID
    }

    override def toBytes(buf: ByteBuf): Unit = {
        buf.writeLong(blockPosition.toLong)
        buf.writeInt(id)
    }

    override def fromBytes(buf: ByteBuf): Unit = {
        blockPosition = BlockPos.fromLong(buf.readLong())
        id = buf.readInt()
    }

    override def onMessage(message: OpenContainerGui, ctx: MessageContext): IMessage = {
        if(ctx.side.isServer) {
            ctx.getServerHandler.playerEntity.openGui(Bookshelf, message.id, ctx.getServerHandler.playerEntity.worldObj, message.blockPosition.getX, message.blockPosition.getY, message.blockPosition.getZ)
        }
        null
    }
}
