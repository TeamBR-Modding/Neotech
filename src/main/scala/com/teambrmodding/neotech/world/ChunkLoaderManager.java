package com.teambrmodding.neotech.world;

import com.teambrmodding.neotech.common.tiles.misc.TileChunkLoader;
import com.teambrmodding.neotech.managers.BlockManager;
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
public class ChunkLoaderManager implements ForgeChunkManager.LoadingCallback {

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
}