package com.teambrmodding.neotech.common.tiles.machines.operators;

import com.teambr.bookshelf.common.tiles.EnergyHandler;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 3/4/2017
 */
public class TileWirelessCharger extends EnergyHandler {

    /*******************************************************************************************************************
     * Tile Methods                                                                                                    *
     *******************************************************************************************************************/

    @Override
    protected void onServerTick() {
        super.onServerTick();
    }

    /*******************************************************************************************************************
     * EnergyHandler                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Used to define the default size of this energy bank
     *
     * @return The default size of the energy bank
     */
    @Override
    protected int getDefaultEnergyStorageSize() {
        return 100000;
    }

    /**
     * Is this tile an energy provider
     *
     * @return True to allow energy out
     */
    @Override
    protected boolean isProvider() {
        return false;
    }

    /**
     * Is this tile an energy reciever
     *
     * @return True to accept energy
     */
    @Override
    protected boolean isReceiver() {
        return true;
    }
}
