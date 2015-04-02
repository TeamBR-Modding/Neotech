package com.dyonovan.neotech.network;

import com.dyonovan.neotech.common.tileentity.machine.TileElectricMiner;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ElectricMinerPacket implements IMessageHandler<ElectricMinerPacket.StartMessage, IMessage> {

    @Override
    public IMessage onMessage(ElectricMinerPacket.StartMessage message, MessageContext ctx) {
        if(ctx.side.isServer()) {
            TileElectricMiner tile = (TileElectricMiner) ctx.getServerHandler().playerEntity.worldObj.
                    getTileEntity(message.pos);

            switch (message.btnPress) {
                case TileElectricMiner.BTN_SCAN:
                    tile.setArea();
                    break;
                case TileElectricMiner.BTN_START:
                    tile.isRunning = true;
                    break;
                case TileElectricMiner.BTN_STOP:
                    tile.isRunning = false;
                    break;
            }
        }
        return null;
    }

    public static class StartMessage implements IMessage {

        private int btnPress;
        private BlockPos pos;

        @SuppressWarnings("unused")
        public StartMessage() {}

        public StartMessage(BlockPos pos, int btnPress) {
            this.btnPress = btnPress;
            this.pos = pos;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            int x = buf.readInt();
            int y = buf.readInt();
            int z = buf.readInt();
            pos = new BlockPos(x, y, z);
            btnPress = buf.readInt();

        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(pos.getX());
            buf.writeInt(pos.getY());
            buf.writeInt(pos.getZ());
            buf.writeInt(btnPress);
        }
    }
}
