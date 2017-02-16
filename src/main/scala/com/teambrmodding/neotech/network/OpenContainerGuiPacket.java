package com.teambrmodding.neotech.network;

import com.teambr.bookshelf.Bookshelf;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/15/2017
 */
public class OpenContainerGuiPacket implements IMessage, IMessageHandler<OpenContainerGuiPacket, IMessage> {
    public BlockPos blockPos;
    public int id;

    /**
     * Stub to prevent network manager crash
     */
    public OpenContainerGuiPacket() {}

    /**
     * Creates the packet
     * @param blockPos The block to open
     * @param id The id
     */
    public OpenContainerGuiPacket(BlockPos blockPos, int id) {
        this.blockPos = blockPos;
        this.id = id;
    }

    /*******************************************************************************************************************
     * IMessage                                                                                                        *
     *******************************************************************************************************************/

    @Override
    public void fromBytes(ByteBuf buf) {
        blockPos = BlockPos.fromLong(buf.readLong());
        id       = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(blockPos.toLong());
        buf.writeInt(id);
    }

    /*******************************************************************************************************************
     * IMessageHandler                                                                                                 *
     *******************************************************************************************************************/

    @Override
    public IMessage onMessage(OpenContainerGuiPacket message, MessageContext ctx) {
        if(ctx.side.isServer())
            ctx.getServerHandler().playerEntity.openGui(Bookshelf.INSTANCE, message.id,
                    ctx.getServerHandler().playerEntity.worldObj, message.blockPos.getX(), message.blockPos.getY(), message.blockPos.getZ());
        return null;
    }
}
