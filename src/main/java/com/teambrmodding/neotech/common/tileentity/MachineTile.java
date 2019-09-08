package com.teambrmodding.neotech.common.tileentity;

import com.teambr.nucleus.common.tiles.Syncable;
import com.teambrmodding.neotech.managers.TileEntityManager;
import net.minecraft.tileentity.TileEntityType;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 9/7/2019
 */
public class MachineTile extends Syncable {

    /**
     * Creates the machine tile
     */
    public MachineTile() {
        super(TileEntityManager.machine);
    }

    /**
     * Used to set the value of a field
     *
     * @param id    The field id
     * @param value The value of the field
     */
    @Override
    public void setVariable(int id, double value) {

    }

    /**
     * Used to get the field on the server, this will fetch the server value and overwrite the current
     *
     * @param id The field id
     * @return The value on the server, now set to ourselves
     */
    @Override
    public Double getVariable(int id) {
        return null;
    }
}
