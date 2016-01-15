package com.dyonovan.neotech.network

import com.dyonovan.neotech.pipes.types.ExtractionPipe
import io.netty.buffer.ByteBuf
import net.minecraft.util.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.simpleimpl.{MessageContext, IMessageHandler, IMessage}

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
class UpdateExtractionPipeValue extends IMessage with IMessageHandler[UpdateExtractionPipeValue, IMessage] {
    var blockPosition = new BlockPos(0, 0, 0)
    var value = -1
    var newValue = 0

    /**
      * Used to set modes and such for pipes
      * @param blockPos
      * @param id 0 Redstone, 1 Mode
      * @param newVal
      */
    def this(blockPos : BlockPos, id : Int, newVal : Int) {
        this()
        blockPosition = blockPos
        value = id
        newValue = newVal
    }

    override def toBytes(buf: ByteBuf): Unit = {
        buf.writeInt(value)
        buf.writeInt(newValue)
        buf.writeLong(blockPosition.toLong)
    }

    override def fromBytes(buf: ByteBuf): Unit = {
        value = buf.readInt()
        newValue = buf.readInt()
        blockPosition = BlockPos.fromLong(buf.readLong())
    }

    override def onMessage(message: UpdateExtractionPipeValue, ctx: MessageContext): IMessage = {
        if(ctx.side.isServer) {
            val world: World = ctx.getServerHandler.playerEntity.worldObj
            if (world.getTileEntity(message.blockPosition) != null) {
                val tile : ExtractionPipe[_, _] = world.getTileEntity(message.blockPosition).asInstanceOf[ExtractionPipe[_, _]]
                message.value match {
                    case 0 => tile.setRedstoneMode(message.newValue)
                    case 1 => tile.setMode(message.newValue)
                }
                world.markBlockForUpdate(message.blockPosition)
            }
        }
        null
    }
}
