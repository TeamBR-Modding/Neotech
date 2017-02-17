package com.teambrmodding.neotech.common.tiles.storage.tanks;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/11/2017
 */
public class TileEliteTank extends TileBasicTank {
    @Override
    protected int getCapacity() {
        return super.getCapacity() * 8;
    }
}
