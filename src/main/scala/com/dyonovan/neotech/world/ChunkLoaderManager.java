package com.dyonovan.neotech.world;

import com.dyonovan.neotech.common.tiles.misc.TileChunkLoader;
import com.dyonovan.neotech.managers.BlockManager;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.List;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis <pauljoda>
 * @since 1/19/2016
 */
public class ChunkLoaderManager implements ForgeChunkManager.OrderedLoadingCallback {
    @Override
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
        for (ForgeChunkManager.Ticket ticket : tickets) {
            int quarryX = ticket.getModData().getInteger("neotech.loaderX");
            int quarryY = ticket.getModData().getInteger("neotech.loaderY");
            int quarryZ = ticket.getModData().getInteger("neotech.loaderZ");
            BlockPos pos = new BlockPos(quarryX, quarryY, quarryZ);

            Block block = world.getBlockState(pos).getBlock();
            if (block == BlockManager.chunkLoader()) {
                TileChunkLoader tq = (TileChunkLoader) world.getTileEntity(pos);
                tq.forceChunkLoading(ticket);
            }
        }
    }

    @Override
    public List<ForgeChunkManager.Ticket> ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world, int maxTicketCount) {
        List<ForgeChunkManager.Ticket> validTickets = Lists.newArrayList();
        for (ForgeChunkManager.Ticket ticket : tickets) {
            int quarryX = ticket.getModData().getInteger("neotech.loaderX");
            int quarryY = ticket.getModData().getInteger("neotech.loaderY");
            int quarryZ = ticket.getModData().getInteger("neotech.loaderZ");
            BlockPos pos = new BlockPos(quarryX, quarryY, quarryZ);

            Block block = world.getBlockState(pos).getBlock();
            if (block == BlockManager.chunkLoader()) {
                validTickets.add(ticket);
            }
        }
        return validTickets;
    }
}