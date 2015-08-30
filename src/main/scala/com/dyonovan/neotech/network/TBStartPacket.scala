package com.dyonovan.neotech.network

import com.dyonovan.neotech.common.tiles.machines.TileThermalBinder
import io.netty.buffer.ByteBuf
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}

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
class TBStartPacket extends IMessage with IMessageHandler[TBStartPacket, IMessage] {

    var blockPos = new BlockPos(0, 0, 0)
    var count = 0

    def this(p: BlockPos, c: Int) {
        this()
        blockPos = p
        count = c
    }

    override def toBytes(buf: ByteBuf): Unit = {
        buf.writeLong(blockPos.toLong)
        buf.writeInt(count)
    }

    override def fromBytes(buf: ByteBuf): Unit = {
        blockPos = BlockPos.fromLong(buf.readLong())
        count = buf.readInt()
    }

    override def onMessage(message: TBStartPacket, ctx: MessageContext): IMessage = {
        if (ctx.side.isServer) {
            val pos = message.blockPos
            val world = ctx.getServerHandler.playerEntity.worldObj
            val tile = world.getTileEntity(pos).asInstanceOf[TileThermalBinder]
            if (tile != null && tile.values.currentItemBurnTime <= 0) {
                tile.count = message.count
                if (tile.count > 0)
                tile.values.burnTime = 20 * 20 * message.count
                tile.values.currentItemBurnTime = tile.values.burnTime
                world.markBlockForUpdate(pos)
            }
        }
        null
    }
}
