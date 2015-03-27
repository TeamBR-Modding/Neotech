package com.dyonovan.neotech.network;

import com.dyonovan.neotech.common.tileentity.machine.TileThermalBinder;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ThermalBinderPacket implements IMessageHandler<ThermalBinderPacket.StartMessage, IMessage> {

    @Override
    public IMessage onMessage(ThermalBinderPacket.StartMessage message, MessageContext ctx) {
        if(ctx.side.isServer()) {
            TileThermalBinder tile = (TileThermalBinder) ctx.getServerHandler().playerEntity.worldObj.
                    getTileEntity(message.pos);
            tile.mergeMB();
        }
        return null;
    }

    public static class StartMessage implements IMessage {

        private BlockPos pos;

        @SuppressWarnings("unused")
        public StartMessage() {}

        public StartMessage(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            int x = buf.readInt();
            int y = buf.readInt();
            int z = buf.readInt();
            pos = new BlockPos(x, y, z);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(pos.getX());
            buf.writeInt(pos.getY());
            buf.writeInt(pos.getZ());
        }
    }
}
