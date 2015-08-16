package com.dyonovan.neotech.pipes.tiles;

import com.dyonovan.neotech.pipes.network.PipeInformation;
import com.dyonovan.neotech.pipes.network.ResourceEntity;
import net.minecraft.util.EnumFacing;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 15, 2015
 */
public interface IPipe {

    /**
     * Can the pipe connect on this end?
     * @param facing The face of us
     * @return True if should connect
     */
    boolean canConnect(EnumFacing facing);

    /**
     * Called when a resource enters this pipe
     * @param resource The resource entering
     */
    void onResourceEnteredPipe(ResourceEntity resource);

    /**
     * Used to get the information for this pipe
     */
    PipeInformation getInformation();

    /**
     * Used to get the current position as a long
     * @return BlockPos as long
     */
    Long getPosAsLong();

    /**
     * Used to tell if this can receive a resource
     * @param resource The resource to check, pass null to check if is sink
     * @return True if it can accept, if null is passed and this is a sink, still return true
     */
    boolean canAcceptResource(ResourceEntity resource);
}
