package com.dyonovan.neotech.pipes.network;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 15, 2015
 */
abstract public class AbstractNetwork {

    /**
     * The types of networks. Used simply to compare if things should connect
     */
    public enum NETWORK_TYPE {
        ITEM,
        FLUID,
        ENERGY,
        REDSTONE
    }

    /**
     * Used to check what kind of network this one is
     * @return What kind of network we are
     */
    abstract NETWORK_TYPE getNetworkType();
}
