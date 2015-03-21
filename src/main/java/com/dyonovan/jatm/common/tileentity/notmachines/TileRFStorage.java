package com.dyonovan.jatm.common.tileentity.notmachines;

import cofh.api.energy.EnergyStorage;
import com.dyonovan.jatm.common.tileentity.BaseMachine;
import com.dyonovan.jatm.common.tileentity.InventoryTile;
import net.minecraft.server.gui.IUpdatePlayerListBox;

public class TileRFStorage extends BaseMachine implements IUpdatePlayerListBox {

    private static final int RF_TOTAL_1 = 250000;
    private static final int RF_TICK_1 = 200;
    public static final int CHARGE_SLOT_1 = 0;

    private int tier;
    public EnergyStorage energyRF;

    public TileRFStorage(int tier) {
        if (tier == 1) energyRF = new EnergyStorage(RF_TOTAL_1, RF_TICK_1);
        inventory = new InventoryTile(tier);
        this.tier = tier;
    }

    @Override
    public void update() {

    }
}
