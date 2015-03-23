package com.dyonovan.jatm.common.tileentity.storage;

import net.minecraft.util.EnumFacing;

public class TileEliteRFStorage extends TileAdvancedRFStorage implements IRFStorage {

    private static final int RF_TOTAL_3 = 10000000;
    private static final int RF_TICK_3 = 10000;

    public TileEliteRFStorage() {
        setInventory(3);
        setEnergyRF(RF_TOTAL_3, RF_TICK_3);
    }

    @Override
    public int getTier() { return 3; }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[] {0, 1, 2};
    }
}
