package com.dyonovan.neotech.network

import com.dyonovan.neotech.common.tiles.misc.TileFertilizer
import io.netty.buffer.ByteBuf
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.{MessageContext, IMessageHandler, IMessage}

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 24, 2015
 */
class DisableSunPacket extends IMessage with IMessageHandler[DisableSunPacket, IMessage] {

    var blockPos = new BlockPos(0, 0, 0)
    var value: Boolean = false

    def this(p: BlockPos, v: Boolean) {
        this()
        blockPos = p
        value = v
    }

    override def toBytes(buf: ByteBuf): Unit = {
        buf.writeLong(blockPos.toLong)
        buf.writeBoolean(value)
    }

    override def fromBytes(buf: ByteBuf): Unit = {
        blockPos = BlockPos.fromLong(buf.readLong())
        value = buf.readBoolean()
    }

    override def onMessage(message: DisableSunPacket, ctx: MessageContext): IMessage = {
        if (ctx.side.isServer) {
            val pos = message.blockPos
            val world = ctx.getServerHandler.playerEntity.worldObj
            val tile = world.getTileEntity(pos).asInstanceOf[TileFertilizer]
            if (tile != null) {
                tile.disabled = message.value
                world.markBlockForUpdate(pos)
            }
        }
        null
    }
}
