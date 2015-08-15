package com.dyonovan.neotech.pipes.network;

import com.dyonovan.neotech.pipes.tiles.IPipe;

import java.util.LinkedList;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 15, 2015
 */
public class PipeNetwork {

    protected LinkedList<IPipe> sources;
    protected LinkedList<IPipe> sinks;

    //Flag to tell if should be destroyed
    protected boolean needsDestroyed = false;

    /**
     * Used to tell if this should be removed
     * @return True if should stop working
     */
    public boolean shouldBeDestroyed() {
        return needsDestroyed;
    }

    /**
     * Used to set this to be destroyed
     */
    public void destory() {
        needsDestroyed = true;
    }

    /**
     * Called at the start of the tick
     */
    public void onTickStart() {}

    /**
     * Called at the end of the tick
     */
    public void onTickEnd() {}
}
