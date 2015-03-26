package com.dyonovan.neotech.common.tileentity.storage;

import net.minecraft.util.EnumFacing;

public class TileAdvancedRFStorage extends TileBasicRFStorage {

    private static final int RF_TOTAL_2 = 1000000;
    private static final int RF_TICK_2 = 1000;

    public TileAdvancedRFStorage() {
        setInventory(2);
        setEnergyRF(RF_TOTAL_2, RF_TICK_2);
    }

    /*******************************************************************************************************************
     **************************************** Inventory Functions ******************************************************
     *******************************************************************************************************************/

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[] {0, 1};
    }
}
